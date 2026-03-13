/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package org.flyve.mdm.agent.core.mqtt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.flyve.mdm.agent.R;
import org.flyve.mdm.agent.core.CommonErrorType;
import org.flyve.mdm.agent.data.database.MqttData;
import org.flyve.mdm.agent.data.database.PoliciesData;
import org.flyve.mdm.agent.data.database.TopicsData;
import org.flyve.mdm.agent.data.localstorage.AppData;
import org.flyve.mdm.agent.policies.PoliciesAsyncTask;
import org.flyve.mdm.agent.services.MQTTService;
import org.flyve.mdm.agent.ui.MainActivity;
import org.flyve.mdm.agent.utils.FlyveLog;
import org.flyve.mdm.agent.utils.Helpers;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;

public class MqttModel implements mqtt.Model {

    private mqtt.Presenter presenter;
    private MqttAndroidClient client;
    private Boolean connected = false;

    private Timer reconnectionTimer;
    private int reconnectionCounter = 0;
    private int reconnectionPeriod = 1000;
    private int reconnectionDelay = 5;
    private long timeLastReconnection = 0;
    private Boolean executeConnection = true;
    private int tryEverySeconds = 30;

    private MqttController policiesController = null;

    public MqttModel(mqtt.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public MqttAndroidClient getMqttClient() {
        return client;
    }

    @Override
    public Boolean isConnected() {
        return connected;
    }

    @Override
    public void connect(final Context context, final MqttCallback callback) {
        if(getMqttClient()!=null && getMqttClient().isConnected()) {
            setStatus(context, callback, true);
            return;
        }

        MqttData cache = new MqttData(context);
        final String mBroker = cache.getBroker();
        final String mPort = cache.getPort();
        final String mUser = cache.getMqttUser();
        final String mPassword = cache.getMqttPasswd();
        final String mTopic = cache.getTopic();
        final String mTLS = cache.getTls();

        String protocol = mTLS.equals("1") ? "ssl" : "tcp";
        String clientId;
        MqttConnectOptions options;

        if(client == null) {
            try {
                clientId = mUser; // FIX: Use Username as ClientID
                client = new MqttAndroidClient(context, protocol + "://" + mBroker + ":" + mPort, clientId);
            } catch (ExceptionInInitializerError ex) {
                showDetailError(context, CommonErrorType.MQTT_IN_INITIALIZER_ERROR, ex.getMessage());
                reconnect(context, callback);
                return;
            }
            client.setCallback(callback);
        }

        try {
            options = new MqttConnectOptions();
            options.setPassword(mPassword.toCharArray());
            options.setUserName(mUser);
            //options.setConnectionTimeout(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT);
            //options.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);

            options.setCleanSession(true); // Change to true for now to force a fresh state
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            options.setConnectionTimeout(30); // Give it more time to handshake
            options.setKeepAliveInterval(60); // Heartbeat every 60 seconds
            options.setAutomaticReconnect(true); // Let Paho handle the small drops

            String will = "{ \"online\": false }";
            options.setWill(mTopic + "/Status/Online", will.getBytes(), 0, true);

            // FIX: SSL Bypass Logic
            if (mTLS.equals("1")) {
                try {
                    javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] {
                            new javax.net.ssl.X509TrustManager() {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                            }
                    };
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                    options.setSocketFactory(sslContext.getSocketFactory());
                    Log.d("FLYVE_DEBUG", "SSL Bypass active.");
                } catch (Exception ex) {
                    Log.e("FLYVE_DEBUG", "SSL Bypass Failed", ex);
                }
            }
        } catch (Exception ex) {
            showDetailError(context, CommonErrorType.MQTT_OPTIONS, ex.getMessage());
            return;
        }

        new TopicsData(context).clearTopics();

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Helpers.storeLog("MQTT", "Connection Success", "");
//                    policiesController = new MqttController(context, client);
//                    setStatus(context, callback, true);
//                    reconnectionCounter = 0;
//
//                    String fullTopic = mTopic;
//                    if (!fullTopic.startsWith("0/agent/")) {
//                        fullTopic = "0/agent/" + mUser;
//                    }
//
//                    Log.d("MQTT_DEBUG", "Subscribing to: " + fullTopic + "/#");
//                    policiesController.subscribe(fullTopic + "/#");
//                    policiesController.subscribe("FlyvemdmManifest/#");
//                }
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("FLYVE_DEBUG", "Connection Success!");
                    setStatus(context, callback, true);
                    reconnectionCounter = 0;

                    // Initialize controller only once
                    if (policiesController == null) {
                        policiesController = new MqttController(context, client);
                    }

                    String fullTopic = mTopic;
                    if (!fullTopic.startsWith("0/agent/")) {
                        fullTopic = "0/agent/" + mUser;
                    }

                    // Only subscribe if the client is actually connected
                    if (client != null && client.isConnected()) {
                        try {
                            // Combine into a single log for clarity
                            Log.d("FLYVE_DEBUG", "Applying subscriptions for: " + fullTopic);
                            client.subscribe(fullTopic + "/#", 0);
                            client.subscribe("FlyvemdmManifest/#", 0);
                        } catch (Exception e) {
                            Log.e("FLYVE_DEBUG", "Subscription Error: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable ex) {
                    setStatus(context, callback, false);
                    showDetailError(context, CommonErrorType.MQTT_ACTION_CALLBACK, ex.getMessage());
                }
            });
        } catch (Exception ex) {
            setStatus(context, callback, false);
            showDetailError(context, CommonErrorType.MQTT_CONNECTION, ex.getMessage());
        }
    }

    private void reconnect(final Context context, final MqttCallback callback) {
        if(reconnectionTimer==null) {
            reconnectionTimer = new Timer();
        }

        reconnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(timeLastReconnection>0) {
                    long currentDate = new Date().getTime();
                    if(((currentDate - timeLastReconnection) / 1000 % 60) >= tryEverySeconds) {
                        timeLastReconnection = currentDate;
                        executeConnection = true;
                    } else {
                        executeConnection = false;
                        return;
                    }
                } else {
                    timeLastReconnection = new Date().getTime();
                    executeConnection = true;
                }

                if(!isConnected() && executeConnection) {
                    reconnectionCounter++;
                    connect(context, callback);
                } else if (isConnected()) {
                    reconnectionTimer.cancel();
                    reconnectionTimer = null;
                }
            }
        }, reconnectionDelay, reconnectionPeriod);
    }

    public void messageArrived(Context context, String topic, MqttMessage message) {
        Log.d("FLYVE_DEBUG", "!!! GOT MESSAGE !!! Topic: " + topic + " Payload: " + new String(message.getPayload()));
        String messageBody = new String(message.getPayload());
        MqttController mqttController = new MqttController(context, getMqttClient());

        if(topic.isEmpty()) return;

        if(messageBody.contains("default")) {
            try {
                String taskId = new JSONObject(messageBody).getString("taskId");
                new PoliciesData(context).removeValue(taskId);
            } catch (Exception ex) {
                FlyveLog.e("MQTT", "Error deleting policy", ex.getMessage());
            }
            return;
        }

        new PoliciesAsyncTask().execute(context, PoliciesAsyncTask.POLICIES, topic, messageBody, this.client);

        if(topic.toLowerCase().contains("ping"))
            new PoliciesAsyncTask().execute(context, PoliciesAsyncTask.PING, topic, messageBody, this.client);

        if(topic.toLowerCase().contains("geolocate"))
            new PoliciesAsyncTask().execute(context, PoliciesAsyncTask.GEOLOCATE, topic, messageBody, this.client);

        if(topic.toLowerCase().contains("inventory"))
            new PoliciesAsyncTask().execute(context, PoliciesAsyncTask.INVENTORY, topic, messageBody, this.client);

        if(topic.toLowerCase().contains("subscribe")) {
            try {
                JSONObject jsonObj = new JSONObject(messageBody);
                if(jsonObj.has("subscribe")) {
                    JSONArray jsonTopics = jsonObj.getJSONArray("subscribe");
                    for(int i=0; i<jsonTopics.length();i++) {
                        String channel = jsonTopics.getJSONObject(i).getString("topic")+"/#";
                        mqttController.subscribe(channel);
                    }
                }
            } catch (Exception ex) {
                showDetailError(context, CommonErrorType.MQTT_SUBSCRIBE, ex.getMessage());
            }
        }
    }

    @Override
    public void showDetailError(Context context, int type, String message) {
        FlyveLog.e(this.getClass().getName(), "Error " + type + ": " + message);
    }

    @Override
    public void onDestroy(Context context) {
        Helpers.deleteMQTTCache(context);
        try {
            context.startService(new Intent(context, MQTTService.class));
        } catch (Exception ex) {
            showDetailError(context, CommonErrorType.MQTT_DESTROY_START_SERVICE, ex.getMessage());
        }
    }

    @Override
    public void deliveryComplete(Context context, IMqttDeliveryToken token) {
        FlyveLog.d("Delivery Complete");
    }

    @Override
    public void connectionLost(Context context, MqttCallback callback, String message) {
        setStatus(context, callback, false);
    }

    private void setStatus(Context context, MqttCallback callback, Boolean isConnected){
        this.connected = isConnected;
        if(!isConnected) {
            reconnect(context, callback);
        } else {
            PoliciesAsyncTask.sendStatusbyHttp(context, true);
        }
        new AppData(context).setOnlineStatus(isConnected);
        Helpers.sendBroadcast(isConnected, Helpers.BROADCAST_STATUS, context);
    }
}