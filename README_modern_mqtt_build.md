

#### FLYVE MDM MODERN MQTT BUILD

```bash
git clone https://github.com/michaelkedey/android-mdm-agent.git
git checkout -b modern-mqtt-client

```



1. ```
   vi gradle/wrapper/gradlewarapper.properties
   ```

   ```
   #Wed Oct 16 14:04:30 CEST 2019
   distributionBase=GRADLE_USER_HOME
   distributionPath=wrapper/dists
   zipStoreBase=GRADLE_USER_HOME
   zipStorePath=wrapper/dists
   distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-all.zip
   ```

   



`app/src/mqtt/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="org.flyve.mdm.agent">

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application>

        <service
            android:name="org.eclipse.paho.android.service.MqttService"
            android:label="Paho MQTT Service" />

        <service
            android:name=".services.MQTTService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="dataSync"
            android:label="M2M communications">
            <intent-filter>
                <action android:name="org.flyve.mdm.agent.ACTION_START" />
                <action android:name="org.flyve.mdm.agent.ACTION_INVENTORY" />
            </intent-filter>
        </service>

        <receiver
            android:name=".receivers.MQTTRestarterBroadcastReceiver"
            android:enabled="true"
            android:exported="true"
            android:label="RestartServiceWhenStopped">
            <intent-filter>
                <action android:name="org.flyve.mdm.agent.restart" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

        <receiver
            android:name=".receivers.FlyveAdminReceiver"
            android:permission="android.permission.BIND_DEVICE_ADMIN"
            android:exported="true"> <!-- ADDED exported -->
            <meta-data
                android:name="android.app.device_admin"
                android:resource="@xml/device_admin" />

            <intent-filter>
                <action android:name="android.app.action.DEVICE_ADMIN_ENABLED" />
                <action android:name="android.app.action.ACTION_DEVICE_ADMIN_DISABLE_REQUESTED" />
                <action android:name="android.app.action.ACTION_DEVICE_ADMIN_DISABLED" />
                <action android:name="android.app.action.ACTION_PASSWORD_CHANGED" />
                <action android:name="android.app.action.ACTION_PASSWORD_EXPIRING" />
                <action android:name="android.app.action.ACTION_PASSWORD_FAILED" />
                <action android:name="android.app.action.ACTION_PASSWORD_SUCCEEDED" />
                <action android:name="android.nfc.extra.ADAPTER_STATE" />
            </intent-filter>
        </receiver>

        <receiver
            android:name=".receivers.MQTTConnectivityReceiver"
            android:exported="false">
            <intent-filter android:priority="100">
                <action android:name="android.net.wifi.STATE_CHANGE" />
                <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
                <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
                <action android:name="android.bluetooth.adapter.action.STATE_CHANGED" />
                <action android:name="android.location.PROVIDERS_CHANGED" />
                <action android:name="android.intent.action.AIRPLANE_MODE" />
                <action android:name="android.intent.action.PHONE_STATE" />
                <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
                <action android:name="android.hardware.usb.action.USB_STATE" />
            </intent-filter>
        </receiver>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="androidx.core.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>

</manifest>
```

```bash
vi app/src/main/AndroidManfifest.xml
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:versionCode="3882"
    android:versionName="2.0.0-rc.7">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.ACTION_MANAGE_OVERLAY_PERMISSION" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.NFC" />
    <uses-permission android:name="android.permission.USB_PERMISSION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.CHANGE_CONFIGURATION" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" />
    <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
    <uses-permission android:name="android.permission.REORDER_TASKS" />
    <uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.REBOOT" />
    <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

    <uses-feature
        android:name="android.hardware.nfc"
        android:required="false" />

    <application
        android:name=".ui.MDMAgent"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme"
        android:usesCleartextTraffic="true">

        <!-- Receivers -->
        <receiver
            android:name=".service.RunOnStartup"
            android:permission="android.permission.RECEIVE_BOOT_COMPLETED"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
                <action android:name="android.intent.action.QUICKBOOT_POWERON" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </receiver>

        <receiver
            android:name=".receivers.AppReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_INSTALL" />
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_FULLY_REMOVED" />
                <data android:scheme="package"/>
            </intent-filter>
        </receiver>

        <!-- Meta-data -->
        <meta-data
            android:name="com.bugsnag.android.API_KEY"
            android:value="b6bc7ce0da33f92fe6c49c568af43963" />

        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification_white" />
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/colorAccent" />

        <!-- Activities -->
        <activity
            android:name=".ui.InstallAppActivity"
            android:launchMode="singleTask" />
        <activity
            android:name=".ui.UninstallAppActivity"
            android:launchMode="singleTask" />
        <activity
            android:name=".ui.OptionsEnrollmentActivity"
            android:launchMode="singleTask" />
        <activity
            android:name=".ui.ScanActivity"
            android:launchMode="singleInstance"
            android:screenOrientation="landscape" />
        <activity
            android:name=".ui.LockActivity"
            android:label="@string/app_name"
            android:launchMode="singleInstance"
            android:screenOrientation="portrait" />
        <activity
            android:name=".ui.PermissionEnrollmentActivity"
            android:configChanges="orientation|screenSize"
            android:launchMode="singleTask"
            android:theme="@style/NoActionBar" />

        <activity
            android:name=".ui.SplashActivity"
            android:configChanges="orientation|screenSize"
            android:launchMode="singleTask"
            android:theme="@style/NoActionBar"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".ui.MainActivity"
            android:configChanges="orientation|screenSize"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:host="robotest" android:scheme="flyve" />
            </intent-filter>
        </activity>

        <activity
            android:name=".ui.StartEnrollmentActivity"
            android:configChanges="orientation|screenSize"
            android:launchMode="singleTask"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:host="flyve.org" android:pathPrefix="/deeplink" android:scheme="http" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:host="register" android:scheme="flyve" />
            </intent-filter>
        </activity>

        <!-- Remaining activities without intent filters -->
        <activity android:name=".ui.EnrollmentActivity" android:configChanges="orientation|screenSize" />
        <activity android:name=".ui.EditUserActivity" android:configChanges="orientation|screenSize" />
        <activity android:name=".ui.ErrorActivity" android:configChanges="orientation|screenSize" />
        <activity android:name=".ui.PreviewSupervisorActivity" android:configChanges="orientation|screenSize" />
        <activity android:name=".ui.DisclosureActivity" android:configChanges="orientation|screenSize" />
        <activity android:name=".ui.PreviewUserActivity" android:configChanges="orientation|screenSize" />

    </application>

</manifest>
```

```
vi policies/src/main/AndroidManifest.xml
```

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
</manifest>

```





`app/src/mqtt/java/org/flyve/mdm/agent/services/MQTTService.java`

```java
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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.flyve.mdm.agent.R;
import org.flyve.mdm.agent.core.mqtt.MqttPresenter;
import org.flyve.mdm.agent.core.mqtt.mqtt;
import org.flyve.mdm.agent.utils.FlyveLog;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

        //FlyveLog.e("MQTT connection lost: " + reason);
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
```



`app/build.gradle`

```
apply plugin: 'com.android.application'

static def releaseTime() {
    return new Date().format("yyyy-MM-dd_HHmmss", TimeZone.getTimeZone("UTC"))
}

android {
    namespace 'org.flyve.mdm.agent'
    compileSdkVersion 34

    defaultConfig {
        applicationId 'org.flyve.mdm.agent'
        minSdkVersion 21
        targetSdkVersion 34
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled true
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        debug {
            testCoverageEnabled true
        }
    }

    flavorDimensions "mdm"
    productFlavors {
        fcm {
            dimension "mdm"
        }
        mqtt {
            dimension "mdm"
            applicationIdSuffix ".mqtt"
        }
    }

    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            def outputFile = outputFileName
            if (outputFile != null && outputFile.endsWith('.apk')) {
                def fileName = outputFile.replace("app",
                        "${defaultConfig.applicationId}_${releaseTime()}")
                outputFileName = fileName
            }
        }
    }

    lintOptions {
        abortOnError false
    }
    buildFeatures {
        buildConfig true
    }
}

sonarqube {
    properties {
        property "sonar.sourceEncoding", "UTF-8"
    }
}

dependencies {
    androidTestImplementation('androidx.test.espresso:espresso-core:3.5.1')
    //androidTestImplementation('tools.fastlane:screengrab:1.1.0')
    //androidTestImplementation 'tools.fastlane:screengrab:2.4.1'
    testImplementation 'junit:junit:4.12'
    testImplementation 'org.mockito:mockito-core:2.21.0'
    androidTestImplementation 'org.mockito:mockito-android:2.21.0'
    // MQTT libraries
    mqttImplementation('org.eclipse.paho:org.eclipse.paho.android.service:1.0.2') {
        exclude module: 'support-v4'
        transitive = true
    }
    mqttImplementation 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.0'
    mqttImplementation 'com.madgag.spongycastle:core:1.54.0.0'
    mqttImplementation 'com.madgag.spongycastle:prov:1.54.0.0'
    mqttImplementation 'com.madgag.spongycastle:pkix:1.54.0.0'
    implementation 'com.google.code.gson:gson:2.8.2'
    implementation 'com.google.zxing:core:3.3.0'
    implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.1.0'
    //Firebase
    //fcmImplementation 'com.google.firebase:firebase-core:16.0.5'
    //fcmImplementation 'com.google.firebase:firebase-messaging:17.3.4'
    // Google libraries
    implementation 'androidx.multidex:multidex:2.0.1'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'android.arch.persistence.room:runtime:1.1.1'
    annotationProcessor 'android.arch.persistence.room:compiler:1.1.1'
    // Logger
    implementation 'com.orhanobut:logger:2.1.1'
//    // Android Inventory Library
//    implementation 'org.flyve:inventory:1.4.0@aar'
    implementation project(':inventory')
    // Bug snag
    implementation 'com.bugsnag:bugsnag-android:4.1.3'
    implementation project(':policies')
}


import groovy.json.JsonSlurper

import java.util.regex.Pattern

task createAbout(type:Exec) {
    workingDir '../ci/scripts'
    commandLine './create_about_data.sh'
}

task updateVersionCode {
    doLast {

        // Usage example:
        // set manual version:
        // gradlew updateVersionCode -P vVersion=123
        // or autoincrement from AndroidManifest
        // gradlew updateVersionCode

        def manifestFile = file("src/main/AndroidManifest.xml")
        def pattern = Pattern.compile("versionCode=\"(\\d+)\"")
        def manifestText = manifestFile.getText()
        def matcher = pattern.matcher(manifestText)
        matcher.find()
        def versionCode = Integer.parseInt(matcher.group(1))

        if(project.hasProperty("vVersion")) {
            versionCode = vVersion
        } else {
            ++versionCode
        }

        def manifestContent = matcher.replaceAll("versionCode=\"" + versionCode + "\"")
        println "Version Code: " + versionCode
        manifestFile.write(manifestContent)
    }
}

preBuild.dependsOn createAbout

task updateVersionName {
    doLast {

        // Usage example:
        // set manual version:
        // gradlew updateVersionName -P vName=1.0.0
        // or get the version from package.json:
        // gradlew updateVersionName

        def versionName

        if (project.hasProperty("vName")) {
            versionName = vName
        } else {
            def packageFile = file('../package.json')
            def json = new JsonSlurper().parseText(packageFile.text)
            versionName = json.version
        }

        def manifestFile = file("src/main/AndroidManifest.xml")
        def patternVersionNumber = Pattern.compile("versionName=\"(\\d+)\\.(\\d+)\\.(\\d+)(?:\\S*)\"")
        def manifestText = manifestFile.getText()
        def matcherVersionNumber = patternVersionNumber.matcher(manifestText)
        matcherVersionNumber.find()

        def manifestContent = matcherVersionNumber.replaceAll("versionName=\"" + versionName + "\"")
        manifestFile.write(manifestContent)

        println "Version name: " + versionName
    }
}
```



```bash
cd ../
git clone https://github.com/glpi-project/android-inventory-library.git
```

```bash
vi android-inventory-librarry/inventory/build.gradle
```

```
plugins {
    id 'com.android.library'
}

android {
    compileSdk 34

    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 34

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled true
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
        debug {
            minifyEnabled false
            enableUnitTestCoverage true
            enableAndroidTestCoverage true
        }
    }

    buildFeatures {
        viewBinding true
    }
    namespace 'org.flyve.inventory'
}

configurations {
    javadocDeps
}


dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    androidTestImplementation('androidx.test.espresso:espresso-core:3.3.0', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    testImplementation 'androidx.test:runner:1.6.2' // Or latest version
    testImplementation 'androidx.test:core:1.3.0'

    implementation 'com.orhanobut:logger:2.2.0'
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'androidx.core:core:1.3.1'
    implementation 'androidx.multidex:multidex:2.0.1'

    javadocDeps 'com.orhanobut:logger:2.2.0'
    javadocDeps 'androidx.appcompat:appcompat:1.2.0'
    javadocDeps 'androidx.core:core:1.3.1'
    javadocDeps 'androidx.multidex:multidex:2.0.1'
}


Properties properties = new Properties()
if(project.rootProject.file('local.properties').exists()) {
    properties.load(project.rootProject.file('local.properties').newDataInputStream())
}

```



`settings.gradle`

```
include ':app', ':policies'
include ':inventory'
project(':inventory').projectDir = new File('../android-inventory-library/inventory')
```



````bash
vi policies/src/main/java/org/flyve/policies/utils/Helpers.java
````

```java
/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.policies.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import org.flyve.policies.R;


/**
 * This class content some helpers function
 */
public class Helpers {

	/**
	 * private construtor
	 */
	private Helpers() {
	}

	public static void sendToNotificationBar(Context context, int id, String title, String message, boolean isPersistence, Class<?> cls, String from) {

		Intent resultIntent = new Intent(context, cls);
		resultIntent.putExtra("From", from);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent piResult = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
				.setContentTitle(title)
				.setContentText(message)
				.setSound(defaultSoundUri)
				.setContentIntent(piResult)
				.setPriority(Notification.PRIORITY_HIGH);

		if(isPersistence) {
			builder.setOngoing(true);
		} else {
			builder.setAutoCancel(true);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Notification Channel
			String notificationChannelId = "1122";
			String channelName = "Flyve MDM Notifications";
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
			notificationChannel.enableLights(true);
 			notificationChannel.setLightColor(Color.GREEN);

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			try {
				notificationManager.createNotificationChannel(notificationChannel);
				builder.setChannelId(notificationChannelId);
			} catch (Exception ex) {
				FlyveLog.e(Helpers.class.getClass().getName() + ", sendToNotificationBar", ex.getMessage());
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			builder.setSmallIcon(R.drawable.ic_notification_white);
		} else {
			builder.setSmallIcon(R.drawable.icon);
		}

		builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		try {
			notificationManager.notify(id, builder.build());
		} catch (Exception ex) {
			FlyveLog.e(Helpers.class.getClass().getName() + ", deleteFolder", ex.getMessage());
		}
	}

	public static void sendToNotificationBar(Context context, String message, Class<?> mainActivity) {
		sendToNotificationBar(context, 1010, context.getResources().getString(R.string.app_name), message, false, mainActivity, "");
	}



}
```



```bash
vi policies/src/main/java/org/flyve/policies/manager/AndroidPolicies.java
```

```java
/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.policies.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;

import org.flyve.policies.utils.FlyveLog;
import org.flyve.policies.utils.Helpers;

import static android.app.admin.DevicePolicyManager.WIPE_EXTERNAL_STORAGE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;

public class AndroidPolicies {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    private Context context;

    public AndroidPolicies(Context context, Class<?> adminReceiver) {
        this.context = context;
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(context, adminReceiver);
    }

    @TargetApi(21)
    public void disableRoaming(boolean disable) {
        if(Build.VERSION.SDK_INT >= 21) {
            try {
                mDPM.setGlobalSetting(mDeviceAdmin, Settings.Global.DATA_ROAMING, disable ? "0" : "1");
            } catch (Exception ex) {
                FlyveLog.e(this.getClass().getName() + ", disableRoaming", ex.getMessage());
            }
        } else {
            FlyveLog.i("Disable roaming policy is available on devices with api equals or mayor than 21");
        }
    }

    @TargetApi(21)
    public void disableCaptureScreen(boolean disable) {
        if(Build.VERSION.SDK_INT >= 21) {
            try {
                mDPM.setScreenCaptureDisabled(mDeviceAdmin, disable);
            } catch (Exception ex) {
                FlyveLog.e(this.getClass().getName() + ", disableCaptureScreen", ex.getMessage());
            }
        } else {
            FlyveLog.i("Screen capture policy is available on devices with api equals or mayor than 21");
        }
    }

    @TargetApi(24)
    public void disableVPN(Boolean disable) {
        if(Build.VERSION.SDK_INT >= 24) {
            try {
                mDPM.setAlwaysOnVpnPackage(mDeviceAdmin, null, !disable);
            } catch (Exception ex) {
                FlyveLog.e(this.getClass().getName() + ", disableVPN", ex.getMessage());
            }
        } else {
            FlyveLog.i("VPN policy is available on devices with api equals or mayor than 24");
        }
    }

    @TargetApi(23)
    public void disableStatusBar(boolean disable) {
        if(Build.VERSION.SDK_INT >= 23) {
            try {
                mDPM.setStatusBarDisabled(mDeviceAdmin, disable);
            } catch (Exception ex) {
                FlyveLog.e(this.getClass().getName() + ", disableStatusBar", ex.getMessage());
            }
        }
    }

    public void enablePassword(boolean enable, String typeRecommended, Class<?> mainActivity) {
        if(enable) {
            DeviceLockedController pwd = new DeviceLockedController(context);
            if (pwd.isDeviceScreenLocked()) {
                try {
                    if (!mDPM.isActivePasswordSufficient()) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } catch (Exception ex) {
                    FlyveLog.e(this.getClass().getName() + ", enablePassword", ex.getMessage());
                }
            } else {
                Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                String type = typeRecommended.equals("PASSWORD_PASSWD") ? "Password" : "PIN Password";
                Helpers.sendToNotificationBar(context, 1009, "MDM Agent", "Please create a " + type, true, mainActivity, "PasswordPolicy");
            }
        }
    }

    public void reboot() {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
            pm.reboot(null);
        } catch (Exception ex) {
            FlyveLog.e(this.getClass().getName() + ", reboot", ex.getMessage());
        }
    }

    public void resetPassword(String newPassword) {
        mDPM.resetPassword(newPassword, 0);
    }

    /**
     * Erase all data of the device
     */
    public void wipe() {
        mDPM.wipeData(WIPE_EXTERNAL_STORAGE);
    }

    /**
     * Launch lock activity
     */
    public void lockScreen(Class<?> lockActivity, Context context) {
        if ( Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(context, lockActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * Lock the device now
     */
    public void lockDevice() {
        mDPM.lockNow();
    }

    /**
     * Lock the device now
     */
    public void unlockDevice() {
        PowerManager.WakeLock screenLock = ((PowerManager)context.getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "AndroidPolicies:unlock");
        screenLock.acquire();
        screenLock.release();
    }


    /**
     * Request to user encrypt files
     */
    public void storageEncryptionDeviceRequest() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_START_ENCRYPTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    /**
     * Encrypt Storage from dashboard
     * @param isEncryption boolean
     */
    public void storageEncryptionDevice(boolean isEncryption) {
        int status = mDPM.getStorageEncryptionStatus();
        FlyveLog.d("status: " + status);

        if(isEncryption && status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE) {
            // the data is already encrypted
            return;
        }

        if(isEncryption && status == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING) {
            // the encryption is working
            return;
        }

        if (status != DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
            // encrypt file mute
            // 3 = ENCRYPTION_STATUS_ACTIVE
            // 1 = ENCRYPTION_STATUS_INACTIVE
            // 5 = ENCRYPTION_STATUS_ACTIVE_PER_USER
            // 4 = ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY
            // 2 = ENCRYPTION_STATUS_ACTIVATING
            int isEncrypt = mDPM.setStorageEncryption(mDeviceAdmin, isEncryption);
            FlyveLog.d("setStorageEncryption: " + isEncrypt);
        } else {
            FlyveLog.d("Storage Encryption unsupported");
        }
    }

    /**
     * Disable the possibility to use the camera
     * @param disable boolean true | false
     */
    public void disableCamera(boolean disable) {
        mDPM.setCameraDisabled(mDeviceAdmin, disable);
    }

    /**
     * Set password length
     * @param length int
     */
    public void setPasswordLength(int length) {
        if(mDPM.getPasswordMinimumLength(mDeviceAdmin)!=length){
            FlyveLog.d("PasswordLength: " + length);
            mDPM.setPasswordMinimumLength(mDeviceAdmin, length);
        }
    }

    /**
     * Set password quality
     * @param quality String quality type
     */
    public void setPasswordQuality(String quality) {
         if("PASSWORD_QUALITY_NUMERIC".equalsIgnoreCase(quality)) {
             FlyveLog.d("switch: PASSWORD_QUALITY_NUMERIC");
             mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
         }

         if("PASSWORD_QUALITY_ALPHABETIC".equalsIgnoreCase(quality)) {
            FlyveLog.d("switch: PASSWORD_QUALITY_ALPHABETIC");
            mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC);
        }

        if("PASSWORD_QUALITY_ALPHANUMERIC".equalsIgnoreCase(quality)) {
            FlyveLog.d("switch: PASSWORD_QUALITY_ALPHANUMERIC");
            mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
        }

        if("PASSWORD_QUALITY_COMPLEX".equalsIgnoreCase(quality)) {
            FlyveLog.d("switch: PASSWORD_QUALITY_COMPLEX");
            mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
        }

        if("PASSWORD_QUALITY_SOMETHING".equalsIgnoreCase(quality)) {
            FlyveLog.d("switch: PASSWORD_QUALITY_SOMETHING");
            mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
        }

        if("PASSWORD_QUALITY_UNSPECIFIED".equalsIgnoreCase(quality)) {
            FlyveLog.d("switch: PASSWORD_QUALITY_UNSPECIFIED");
            mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
        }
    }

    /**
     * Set Password minumim letters
     * @param minLetters int
     */
    public void setPasswordMinimumLetters(int minLetters) {
        if(mDPM.getPasswordMinimumLetters(mDeviceAdmin)!=minLetters) {
            FlyveLog.d("PasswordMinimumLetters:  " + minLetters);

            if (mDPM.getPasswordQuality(mDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX) {
                mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
            }

            mDPM.setPasswordMinimumLetters(mDeviceAdmin, minLetters);
        }
    }

    /**
     * set Password Minimum Lower Case
     * @param minLowerCase int
     */
    public void setPasswordMinimumLowerCase(int minLowerCase) {
        if(mDPM.getPasswordMinimumLowerCase(mDeviceAdmin)!=minLowerCase) {
            FlyveLog.d("setPasswordMinimumLowerCase:  " + minLowerCase);

            if (mDPM.getPasswordQuality(mDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX) {
                mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
            }

            mDPM.setPasswordMinimumLowerCase(mDeviceAdmin, minLowerCase);
        }
    }

    /**
     * set Password Minimum Upper Case
     * @param minUpperCase int
     */
    public void setPasswordMinimumUpperCase(int minUpperCase) {
        if(mDPM.getPasswordMinimumUpperCase(mDeviceAdmin)!=minUpperCase) {
            FlyveLog.d("setPasswordMinimumUpperCase:  " + minUpperCase);

            if (mDPM.getPasswordQuality(mDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX) {
                mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
            }

            mDPM.setPasswordMinimumUpperCase(mDeviceAdmin, minUpperCase);
        }
    }

    /**
     * set Password Minimum Non Letter
     * @param minNonLetter int
     */
    public void setPasswordMinimumNonLetter(int minNonLetter) {
        if(mDPM.getPasswordMinimumNonLetter(mDeviceAdmin)!=minNonLetter) {
            FlyveLog.d("setPasswordMinimumNonLetter: " + minNonLetter);

            if (mDPM.getPasswordQuality(mDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX) {
                mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
            }

            mDPM.setPasswordMinimumNonLetter(mDeviceAdmin, minNonLetter);
        }
    }

    /**
     * set Password Minimum Numeric
     * @param minNumeric int
     */
    public void setPasswordMinimumNumeric(int minNumeric) {
        if(mDPM.getPasswordMinimumNumeric(mDeviceAdmin)!=minNumeric) {
            FlyveLog.d("setPasswordMinimumNumeric:  " + minNumeric);

            if (mDPM.getPasswordQuality(mDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX) {
                mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
            }

            mDPM.setPasswordMinimumNumeric(mDeviceAdmin, minNumeric);
        }
    }

    /**
     * set Password Minimum Symbols
     * @param minSymbols int
     */
    public void setPasswordMinimumSymbols(int minSymbols) {
        if(mDPM.getPasswordMinimumSymbols(mDeviceAdmin)!=minSymbols) {
            FlyveLog.d("setPasswordMinimumSymbols:  " + minSymbols);

            if (mDPM.getPasswordQuality(mDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX) {
                mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
            }

            mDPM.setPasswordMinimumSymbols(mDeviceAdmin, minSymbols);
        }
    }

    /**
     * set Maximum Failed Passwords For Wipe
     * @param maxFailed int
     */
    public void setMaximumFailedPasswordsForWipe(int maxFailed) {
        if(mDPM.getMaximumFailedPasswordsForWipe(mDeviceAdmin)!=maxFailed) {
            FlyveLog.d("setMaximumFailedPasswordsForWipe:  " + maxFailed);
            mDPM.setMaximumFailedPasswordsForWipe(mDeviceAdmin, maxFailed);
        }
    }

    /**
     * set Maximum Time To Lock
     * @param timeMs
     */
    public void setMaximumTimeToLock(long timeMs) {
        if(mDPM.getMaximumTimeToLock(mDeviceAdmin)!=timeMs) {
            FlyveLog.d("setMaximumTimeToLock:  " + timeMs);
            mDPM.setMaximumTimeToLock(mDeviceAdmin, timeMs);
        }
    }
}

```



```bash
vi policies/build.gradle
```

```
apply plugin: 'com.android.library'

android {
    namespace 'org.flyve.policies'
    compileSdkVersion 33

    defaultConfig {
        minSdkVersion 16
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

}

dependencies {
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test:runner:1.5.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    // Logger
    implementation 'androidx.core:core:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.orhanobut:logger:2.2.0'
}

```



```
vi build.gradle
```

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.0'
        //classpath 'com.google.gms:google-services:4.2.0'
        //classpath 'com.google.firebase:firebase-core:16.0.5' //need to prevent warning about missing dependencies on build
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id "org.sonarqube" version "2.5"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```



```bash
find app/src/main/java -type f -name "*.java" -print0 | xargs -0 sed -i \
  -e 's/import android\.arch\.persistence\.room\./import androidx.room./g'
```



```bash
find app/src/main/java -type f -name "*.java" -print0 | xargs -0 sed -i \
  -e 's/import android\.arch\.persistence\.room\./import androidx.room./g'
```

```bash
find app/src/main/java app/src/mqtt/java -type f -name "*.java" -print0 | xargs -0 sed -i \
  -e 's/import android\.support\.annotation\.Nullable;/import androidx.annotation.Nullable;/g' \
  -e 's/import android\.support\.annotation\.NonNull;/import androidx.annotation.NonNull;/g' \
  -e 's/import android\.support\.v7\.app\.AppCompatActivity;/import androidx.appcompat.app.AppCompatActivity;/g' \
  -e 's/import android\.support\.v7\.app\.AlertDialog;/import androidx.appcompat.app.AlertDialog;/g' \
  -e 's/import android\.support\.v7\.widget\.Toolbar;/import androidx.appcompat.widget.Toolbar;/g' \
  -e 's/import android\.support\.v4\.app\.Fragment;/import androidx.fragment.app.Fragment;/g' \
  -e 's/import android\.support\.v4\.app\.FragmentActivity;/import androidx.fragment.app.FragmentActivity;/g' \
  -e 's/import android\.support\.v4\.app\.FragmentManager;/import androidx.fragment.app.FragmentManager;/g' \
  -e 's/import android\.support\.v4\.app\.FragmentTransaction;/import androidx.fragment.app.FragmentTransaction;/g' \
  -e 's/import android\.support\.v4\.app\.FragmentPagerAdapter;/import androidx.fragment.app.FragmentPagerAdapter;/g' \
  -e 's/import android\.support\.v4\.app\.FragmentStatePagerAdapter;/import androidx.fragment.app.FragmentStatePagerAdapter;/g' \
  -e 's/import android\.support\.v4\.app\.ActivityCompat;/import androidx.core.app.ActivityCompat;/g' \
  -e 's/import android\.support\.v4\.app\.NotificationCompat;/import androidx.core.app.NotificationCompat;/g' \
  -e 's/import android\.support\.v4\.content\.ContextCompat;/import androidx.core.content.ContextCompat;/g' \
  -e 's/import android\.support\.v4\.content\.FileProvider;/import androidx.core.content.FileProvider;/g' \
  -e 's/import android\.support\.v4\.content\.LocalBroadcastManager;/import androidx.localbroadcastmanager.content.LocalBroadcastManager;/g' \
  -e 's/import android\.support\.v4\.widget\.DrawerLayout;/import androidx.drawerlayout.widget.DrawerLayout;/g' \
  -e 's/import android\.support\.v4\.widget\.SwipeRefreshLayout;/import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;/g' \
  -e 's/import android\.support\.v4\.view\.ViewPager;/import androidx.viewpager.widget.ViewPager;/g' \
  -e 's/import android\.support\.v4\.view\.PagerAdapter;/import androidx.viewpager.widget.PagerAdapter;/g' \
  -e 's/import android\.support\.design\.widget\.Snackbar;/import com.google.android.material.snackbar.Snackbar;/g' \
  -e 's/import android\.support\.design\.widget\.TabLayout;/import com.google.android.material.tabs.TabLayout;/g' \
  -e 's/import android\.support\.design\.widget\.FloatingActionButton;/import com.google.android.material.floatingactionbutton.FloatingActionButton;/g' \
  -e 's/import android\.support\.v7\.app\.ActionBarDrawerToggle;/import androidx.appcompat.app.ActionBarDrawerToggle;/g' \
  -e 's/import android\.support\.multidex\.MultiDex;/import androidx.multidex.MultiDex;/g'
```

```bash
find app/src/main/java app/src/mqtt/java -type f -name "*.java" -print0 | xargs -0 sed -i \
  -e 's/android\.support\.v7\.widget\.Toolbar/androidx.appcompat.widget.Toolbar/g' \
  -e 's/android\.support\.v7\.widget\.AppCompatImageView/androidx.appcompat.widget.AppCompatImageView/g' \
  -e 's/android\.support\.design\.R\.id\.snackbar_text/com.google.android.material.R.id.snackbar_text/g'
  
  
```

```bash
find app/src -name "*.xml" -exec sed -i \
's/android.support.v4.view.ViewPager/androidx.viewpager.widget.ViewPager/g' {} +

find app/src -name "*.xml" -exec sed -i \
's/android.support.v4.widget.SwipeRefreshLayout/androidx.swiperefreshlayout.widget.SwipeRefreshLayout/g' {} +

find app/src -name "*.xml" -exec sed -i \
's/android.support.v4.widget.DrawerLayout/androidx.drawerlayout.widget.DrawerLayout/g' {} +

find app/src -name "*.xml" -exec sed -i \
's/android.support.v7.widget.Toolbar/androidx.appcompat.widget.Toolbar/g' {} +

find app/src -name "*.xml" -exec sed -i \
's/android.support.design.widget.FloatingActionButton/com.google.android.material.floatingactionbutton.FloatingActionButton/g' {} +

find app/src -name "*.xml" -exec sed -i \
's/android.support.design.widget.TabLayout/com.google.android.material.tabs.TabLayout/g' {} +

find app/src -name "*.xml" -exec sed -i \
's/android.support.design.widget.NavigationView/com.google.android.material.navigation.NavigationView/g' {} +

```

```bash
find app/src -name "*.java" -exec sed -i \
's/import android.support.v7.app.AppCompatActivity;/import androidx.appcompat.app.AppCompatActivity;/g' {} +

# Replace Toolbar
find app/src -name "*.java" -exec sed -i \
's/import android.support.v7.widget.Toolbar;/import androidx.appcompat.widget.Toolbar;/g' {} +

# Replace Fragment
find app/src -name "*.java" -exec sed -i \
's/import android.support.v4.app.Fragment;/import androidx.fragment.app.Fragment;/g' {} +

# Replace NotificationCompat
find app/src -name "*.java" -exec sed -i \
's/import android.support.v4.app.NotificationCompat;/import androidx.core.app.NotificationCompat;/g' {} +

# Replace DrawerLayout
find app/src -name "*.java" -exec sed -i \
's/import android.support.v4.widget.DrawerLayout;/import androidx.drawerlayout.widget.DrawerLayout;/g' {} +

# Replace SwipeRefreshLayout
find app/src -name "*.java" -exec sed -i \
's/import android.support.v4.widget.SwipeRefreshLayout;/import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;/g' {} +

# Replace FloatingActionButton
find app/src -name "*.java" -exec sed -i \
's/import android.support.design.widget.FloatingActionButton;/import com.google.android.material.floatingactionbutton.FloatingActionButton;/g' {} +

# Replace TabLayout
find app/src -name "*.java" -exec sed -i \
's/import android.support.design.widget.TabLayout;/import com.google.android.material.tabs.TabLayout;/g' {} +

# Replace NavigationView
find app/src -name "*.java" -exec sed -i \
's/import android.support.design.widget.NavigationView;/import com.google.android.material.navigation.NavigationView;/g' {} +

# Replace annotations
find app/src -name "*.java" -exec sed -i \
's/import android.support.annotation.NonNull;/import androidx.annotation.NonNull;/g' {} +

# Replace MultiDex
find app/src -name "*.java" -exec sed -i \
's/import android.support.multidex.MultiDex;/import androidx.multidex.MultiDex;/g' {} +

# Replace Android Test imports
find app/src -name "*.java" -exec sed -i \
's/import android.support.test.InstrumentationRegistry;/import androidx.test.platform.app.InstrumentationRegistry;/g' {} +
find app/src -name "*.java" -exec sed -i \
's/import android.support.test.rule.ActivityTestRule;/import androidx.test.ext.junit.rules.ActivityScenarioRule;/g' {} +
find app/src -name "*.java" -exec sed -i \
's/import android.support.test.runner.AndroidJUnit4;/import androidx.test.ext.junit.runners.AndroidJUnit4;/g' {} +
```



```
vi gradle/wrapper/gradle-wrapper.properties
```

```
#Wed Oct 16 14:04:30 CEST 2019
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-all.zip
```



```
code editor
Settings
Navigate to:
Build, Execution, Deployment → Build Tools → Gradle
Look for “Gradle JDK”.
Download or select OpenJDK 19, Apply → OK.

sync project with gradle files
```



```bash
./gradlew clean assembleMqttDebug
adb install app/build/outputs/apk/mqtt/debug/org.flyve.mdm.agent_2026-03-11_123015-mqtt-debug.apk 
```

