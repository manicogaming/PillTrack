package com.pap.diogo.pilltrack;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pap.diogo.pilltrack.Accounts.AccountFragment;
import com.pap.diogo.pilltrack.Appoints.AppointsFragment;
import com.pap.diogo.pilltrack.Maps.MapsActivity;
import com.pap.diogo.pilltrack.Pills.PillsFragment;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_1_ID;
import static com.pap.diogo.pilltrack.AppNotifications.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private String date, hour, hlocation;
    LocalDate cDate, mDate;
    private NotificationManagerCompat notificationManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navigation_pills:
                    selectedFragment = new PillsFragment();
                    break;
                case R.id.navigation_appointment:
                    selectedFragment = new AppointsFragment();
                    break;
                case R.id.navigation_account:
                    selectedFragment = new AccountFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent VerifyLogin = new Intent(MainActivity.this, Launcher.class);
            VerifyLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(VerifyLogin);
        }

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        getAppointsNotifications();
        getExamsNotifications();

    }

    private void getAppointsNotifications() {
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    date = snapshot.child("date").getValue(String.class);
                    hour = snapshot.child("hour").getValue(String.class);
                    hlocation = snapshot.child("hlocation").getValue(String.class);

                    String dtStart = date;
                    DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                    mDate = org.joda.time.LocalDate.parse(dtStart, format);
                    cDate = new LocalDate();
                    int days = Days.daysBetween(cDate, mDate).getDays();

                    if (days == 0)
                    {
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        Intent activityIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        activityIntent.putExtra("GPSLocation", hlocation);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
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
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    date = snapshot.child("date").getValue(String.class);
                    hour = snapshot.child("hour").getValue(String.class);
                    hlocation = snapshot.child("hlocation").getValue(String.class);

                    String dtStart = date;
                    DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                    mDate = org.joda.time.LocalDate.parse(dtStart, format);
                    cDate = new LocalDate();
                    int days = Days.daysBetween(cDate, mDate).getDays();

                    if (days == 0)
                    {
                        notificationManager = NotificationManagerCompat.from(getApplicationContext());

                        Intent activityIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        activityIntent.putExtra("GPSLocation", hlocation);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_2_ID)
                                .setSmallIcon(R.mipmap.ic_launcher_round)
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

    public void setNavItem(int item) {
        switch (item) {
            case 1:
                navigation.setSelectedItemId(R.id.navigation_pills);
                break;
            case 2:
                navigation.setSelectedItemId(R.id.navigation_appointment);
                break;
            case 3:
                navigation.setSelectedItemId(R.id.navigation_account);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
};