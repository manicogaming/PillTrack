package com.pap.diogo.pilltrack;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_1_ID;
import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_2_ID;
import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_3_ID;

public class NotificationJobService extends JobService {
    private static final String TAG = "NotificationJobService";
    private boolean jobCancelled;

    private String date, hour, hlocation, interval, pillhour, pillstartdate, pillenddate, name, specialty;
    private Date pDate;
    LocalDate cDate, mDate, sDate, eDate;
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
                getPillNotifications();

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    date = snapshot.child("date").getValue(String.class);
                    hour = snapshot.child("hour").getValue(String.class);
                    hlocation = snapshot.child("hlocation").getValue(String.class);
                    specialty = snapshot.child("name").getValue(String.class);

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
                                .setContentText("Tem uma consulta hoje ás " + hour + " de " + specialty + ".")
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Exams").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    date = snapshot.child("date").getValue(String.class);
                    hour = snapshot.child("hour").getValue(String.class);
                    hlocation = snapshot.child("hlocation").getValue(String.class);
                    specialty = snapshot.child("name").getValue(String.class);

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
                                .setContentText("Tem uma consulta hoje ás " + hour + " de " + specialty + ".")
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

    private void getPillNotifications() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Pills").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    name = snapshot.child("name").getValue(String.class);
                    pillhour = snapshot.child("pillhour").getValue(String.class);
                    interval = snapshot.child("interval").getValue(String.class);
                    pillstartdate = snapshot.child("pillstartdate").getValue(String.class);
                    pillenddate = snapshot.child("pillenddate").getValue(String.class);

                    pillNextIntake(name, pillhour, interval, pillstartdate, ref);

                    String dtStart = pillstartdate;
                    String dtEnd = pillenddate;
                    DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                    sDate = org.joda.time.LocalDate.parse(dtStart, format);
                    eDate = org.joda.time.LocalDate.parse(dtEnd, format);
                    int days = Days.daysBetween(sDate, eDate).getDays();

                    if (days < 0) {
                        ref.child(name).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addListenerForSingleValueEvent(eventListener);
    }

    public void pillNextIntake(String name, String pillhour, String interval, String pillstartdate, DatabaseReference ref) {
        String dtNow = pillstartdate;
        DateTimeFormatter curr = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
        mDate = org.joda.time.LocalDate.parse(dtNow, curr);
        cDate = new LocalDate();
        int currdays = Days.daysBetween(cDate, mDate).getDays();

        if (currdays == 0) {
            if (interval.equals("4 em 4 horas")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                try {
                    pDate = dateFormat.parse(pillhour);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(pDate);
                calendar.add(Calendar.HOUR, 4);

                String nextPill = checkDigit(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + checkDigit(calendar.get(Calendar.MINUTE));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getCurrentTime = sdf.format(c.getTime());

                if (getCurrentTime.compareTo(nextPill) == 0) {
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_3_ID)
                            .setSmallIcon(R.drawable.ic_icon)
                            .setContentTitle("Medicamentos")
                            .setContentText("Tem uma medicação a tomar agora.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(true)
                            .build();

                    notificationManager.notify(3, notification);
                    ref.child(name).child("pillhour").setValue(nextPill);
                }
            }

            if (interval.equals("8 em 8 horas")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                try {
                    pDate = dateFormat.parse(pillhour);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(pDate);
                calendar.add(Calendar.HOUR, 8);

                String nextPill = checkDigit(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + checkDigit(calendar.get(Calendar.MINUTE));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getCurrentTime = sdf.format(c.getTime());

                if (getCurrentTime.compareTo(nextPill) == 0) {
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_3_ID)
                            .setSmallIcon(R.drawable.ic_icon)
                            .setContentTitle("Medicamentos")
                            .setContentText("Tem uma medicação a tomar agora.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(true)
                            .build();

                    notificationManager.notify(3, notification);
                    ref.child(name).child("pillhour").setValue(nextPill);
                }
            }

            if (interval.equals("12 em 12 horas")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                try {
                    pDate = dateFormat.parse(pillhour);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(pDate);
                calendar.add(Calendar.HOUR, 12);

                String nextPill = checkDigit(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + checkDigit(calendar.get(Calendar.MINUTE));

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String getCurrentTime = sdf.format(c.getTime());

                if (getCurrentTime.compareTo(nextPill) == 0) {
                    notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_3_ID)
                            .setSmallIcon(R.drawable.ic_icon)
                            .setContentTitle("Medicamentos")
                            .setContentText("Tem uma medicação a tomar agora.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(true)
                            .build();

                    notificationManager.notify(3, notification);
                    ref.child(name).child("pillhour").setValue(nextPill);
                }
            }
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
