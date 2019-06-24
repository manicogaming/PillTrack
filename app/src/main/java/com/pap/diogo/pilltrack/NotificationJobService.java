package com.pap.diogo.pilltrack;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_1_ID;
import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_2_ID;

public class NotificationJobService extends JobService {
    private static final String TAG = "NotificationJobService";
    private boolean jobCancelled;

    private String date, hour, hlocation;
    LocalDate cDate, mDate;
    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");

        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (jobCancelled) {
                    return;
                }

                getAppointsNotifications();
                getExamsNotifications();

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    private void getAppointsNotifications() {
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    date = snapshot.child("date").getValue(String.class);
                    hour = snapshot.child("hour").getValue(String.class);
                    hlocation = snapshot.child("hlocation").getValue(String.class);

                    String dtStart = date;
                    DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                    mDate = org.joda.time.LocalDate.parse(dtStart, format);
                    cDate = new LocalDate();
                    int days = Days.daysBetween(cDate, mDate).getDays();

                    if (days == 0) {
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        Intent activityIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + hlocation));
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_icon)
                                .setContentTitle("Consultas")
                                .setContentText("Tem uma consulta hoje ás " + hour + ".")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true)
                                .addAction(R.mipmap.ic_launcher, "Ver Localização", contentIntent)
                                .build();

                        notificationManager.notify(1, notification);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

    private void getExamsNotifications() {
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Exams").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    date = snapshot.child("date").getValue(String.class);
                    hour = snapshot.child("hour").getValue(String.class);
                    hlocation = snapshot.child("hlocation").getValue(String.class);

                    String dtStart = date;
                    DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                    mDate = org.joda.time.LocalDate.parse(dtStart, format);
                    cDate = new LocalDate();
                    int days = Days.daysBetween(cDate, mDate).getDays();

                    if (days == 0) {
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        Intent activityIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + hlocation));
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                                .setSmallIcon(R.drawable.ic_icon)
                                .setContentTitle("Exames")
                                .setContentText("Tem um exame hoje ás " + hour + ".")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true)
                                .addAction(R.mipmap.ic_launcher, "Ver Localização", contentIntent)
                                .build();

                        notificationManager.notify(2, notification);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }
}
