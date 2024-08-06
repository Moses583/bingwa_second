package com.ujuzi.moses.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;

import androidx.annotation.Nullable;

import com.ujuzi.moses.SmsReceiver;

public class MyService extends Service {

    SmsReceiver smsReceiver;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        smsReceiver = new SmsReceiver();

        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, filter);

        final String CHANNEL_ID = "Foreground service";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                    .setContentText("Foreground service is running")
                    .setContentTitle("FROM SAMPLE SERVICE APP");
            startForeground(1001, notification.build());

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}