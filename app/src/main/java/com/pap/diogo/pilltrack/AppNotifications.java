package com.pap.diogo.pilltrack;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppNotifications extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    public static final String CHANNEL_3_ID = "channel3";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Consultas", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("Canal das Consultas");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID, "Exames", NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("Canal dos Exames");

            NotificationChannel channal3 = new NotificationChannel(CHANNEL_3_ID,"Medicamentos",NotificationManager.IMPORTANCE_DEFAULT);
            channal3.setDescription("Canal dos Medicamentos");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channal3);
        }
    }
}
