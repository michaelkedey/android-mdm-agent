package org.flyve.mdm.agent.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.flyve.mdm.agent.R;
import org.flyve.mdm.agent.core.mqtt.MqttPresenter;
import org.flyve.mdm.agent.core.mqtt.mqtt;
import org.flyve.mdm.agent.utils.FlyveLog;

/**
 * This is the service get and send message from MQTT
 */
public class MQTTService extends Service implements MqttCallback, mqtt.View {

    private static final String CHANNEL_ID = "flyve_mqtt_service";
    private static final int NOTIFICATION_ID = 101;

    private mqtt.Presenter presenter;
    private final IBinder mBinder = new LocalBinder();

    public static Intent start(Context context) {
        Intent serviceIntent = new Intent(context.getApplicationContext(), MQTTService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getApplicationContext().startForegroundService(serviceIntent);
        } else {
            context.getApplicationContext().startService(serviceIntent);
        }

        return serviceIntent;
    }

    /**
     * Constructor
     */
    public MQTTService() {
        FlyveLog.d("MQTT Service Constructor");
        presenter = new MqttPresenter(this);
    }

    public class LocalBinder extends Binder {
        public MQTTService getServerInstance() {
            return MQTTService.this;
        }
    }

    /**
     * Return the communication channel to the service
     * @param intent that was used to bind to this service
     * @return IBinder null if clients cannot bind to the service
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called by the system every time a client explicitly starts the service
     *
     * @param intent supplied to start the service
     * @param flags the additional data about this start request
     * @param startId a unique integer representing this specific request to start
     * @return constant START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FlyveLog.d("MQTT Service onStartCommand");

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification("MQTT service running"));

        presenter.connect(getApplicationContext(), MQTTService.this);
        return START_STICKY;
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed
     */
    @Override
    public void onDestroy() {
        FlyveLog.d("MQTT Service onDestroy");
        presenter.onDestroy(getApplicationContext());
        stopForeground(true);
        super.onDestroy();
    }

    /**
     * If connection fail trigger this function
     * @param cause Throwable error
     */
    @Override
    public void connectionLost(Throwable cause) {
        String reason = (cause != null && cause.getMessage() != null)
                ? cause.getMessage()
                : "Unknown connection loss";

        FlyveLog.e("MQTTService", "MQTT connection lost: " + reason);
        updateNotification("MQTT disconnected");
        presenter.connectionLost(getApplicationContext(), MQTTService.this, reason);
    }

    /**
     * If delivery of the message was complete
     * @param token get message token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        presenter.deliveryComplete(getApplicationContext(), token);
    }

    /**
     * When a message from server arrive
     * @param topic String topic where the message from
     * @param message MqttMessage message content
     * @throws Exception error
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String debugInfo = "Notification (message): " + message + "\n" + "Notification (topic): " + topic;
        FlyveLog.d(debugInfo);
        updateNotification("MQTT message received");
        presenter.messageArrived(getApplicationContext(), topic, message);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager == null) {
                return;
            }

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Flyve MQTT Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Keeps Flyve MQTT connection alive");
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String contentText) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Flyve MDM")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_notification_white)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    private void updateNotification(String contentText) {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(contentText));
        }
    }
}