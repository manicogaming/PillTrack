package com.pap.diogo.pilltrack;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout pills_layout, appoint_layout, add_pills_layout, add_appoints_layout, account_layout, add_button;
    private TextView AccountName0, AccountAge0;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Home();
                    return true;
                case R.id.navigation_pills:
                    Pills();
                    return true;
                case R.id.navigation_appointment:
                    Appointment();
                    return true;
                case R.id.navigation_account:
                    Account();
                    return true;
            }
            return false;
        }
    };

    public MainActivity() {
    }

    private void Account(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.account);

        pills_layout.setVisibility(View.GONE);
        appoint_layout.setVisibility(View.GONE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.GONE);
        account_layout.setVisibility(View.VISIBLE);

        add_button.setVisibility(View.VISIBLE);

    }


    private void Appointment() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.add_appoints);

        pills_layout.setVisibility(View.GONE);
        appoint_layout.setVisibility(View.GONE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.VISIBLE);
        account_layout.setVisibility(View.GONE);

        add_button.setVisibility(View.VISIBLE);
    }

    private void Pills() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.add_pills);

        pills_layout.setVisibility(View.GONE);
        appoint_layout.setVisibility(View.GONE);
        add_pills_layout.setVisibility(View.VISIBLE);
        add_appoints_layout.setVisibility(View.GONE);
        account_layout.setVisibility(View.GONE);

        add_button.setVisibility(View.VISIBLE);
    }

    private void Home() {
        pills_layout.setVisibility(View.VISIBLE);
        appoint_layout.setVisibility(View.VISIBLE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.GONE);
        account_layout.setVisibility(View.GONE);

        add_button.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pills_layout = findViewById(R.id.pills_layout);
        appoint_layout = findViewById(R.id.appoint_layout);
        add_pills_layout = findViewById(R.id.add_pills);
        add_appoints_layout = findViewById(R.id.add_appoints);
        account_layout = findViewById(R.id.account);

        AccountName0 = findViewById(R.id.AccountName0);
        AccountAge0 = findViewById(R.id.AccountAge0);

        add_button = findViewById(R.id.add);

        pills_layout.setVisibility(View.VISIBLE);
        appoint_layout.setVisibility(View.VISIBLE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.GONE);
        account_layout.setVisibility(View.GONE);
        add_button.setVisibility(View.GONE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String name = dataSnapshot.child("name").getValue().toString();
                final String age = dataSnapshot.child("idade").getValue().toString();
                AccountName0.setText(name);
                AccountAge0.setText(age + " anos");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
};