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
    private View pills, appointments, add_pills, add_appoints, account;
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

        pills_layout.removeView(pills);
        appoint_layout.removeView(appointments);
        add_pills_layout.removeView(add_pills);
        add_appoints_layout.removeView(add_appoints);

        //account_layout.removeAllViews();
        //account_layout.addView(account);

        add_button.setVisibility(View.VISIBLE);
        getUserInfo();

    }


    private void Appointment() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.add_appoints);

        pills_layout.removeView(pills);
        appoint_layout.removeView(appointments);
        add_pills_layout.removeView(add_pills);
        //account_layout.removeView(account);

        add_appoints_layout.removeAllViews();
        add_appoints_layout.addView(add_appoints);

        add_button.setVisibility(View.VISIBLE);
    }

    private void Pills() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.add_pills);

        pills_layout.removeView(pills);
        appoint_layout.removeView(appointments);
        add_appoints_layout.removeView(add_appoints);
        //account_layout.removeView(account);

        add_pills_layout.removeAllViews();
        add_pills_layout.addView(add_pills);

        add_button.setVisibility(View.VISIBLE);
    }

    private void Home() {
        add_appoints_layout.removeView(add_appoints);
        add_pills_layout.removeView(add_pills);
        //account_layout.removeView(account);

        pills_layout.removeAllViews();
        pills_layout.addView(pills);

        appoint_layout.removeAllViews();
        appoint_layout.addView(appointments);

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
        //account_layout = findViewById(R.id.account);
        add_button = findViewById(R.id.add);
        AccountName0 = findViewById(R.id.AccountName0);
        AccountAge0 = findViewById(R.id.AccountAge0);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        pills = inflater.inflate(R.layout.pills, null);
        appointments = inflater.inflate(R.layout.appointments, null);
        add_pills = inflater.inflate(R.layout.add_pills, null);
        add_appoints = inflater.inflate(R.layout.add_appoint, null);
        account = inflater.inflate(R.layout.account, null);

        pills_layout.addView(pills);
        appoint_layout.addView(appointments);

        add_button.setVisibility(View.GONE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void getUserInfo(){
        RecyclerView accountUsers = account.findViewById(R.id.AccountList);

        if (accountUsers != null)
        {
            accountUsers.setHasFixedSize(true);
            accountUsers.setLayoutManager(new LinearLayoutManager(getBaseContext()));

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String userid = user.getUid();

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

            FirebaseRecyclerOptions<AccountLayout> AccountQ = new FirebaseRecyclerOptions.Builder<AccountLayout>().setQuery(ref, AccountLayout.class).setLifecycleOwner(this).build();

            FirebaseRecyclerAdapter<AccountLayout, AccountInfo> AccountAdapter = new FirebaseRecyclerAdapter<AccountLayout, AccountInfo>(AccountQ){

                @NonNull
                @Override
                public AccountInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    return new AccountInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_layout, viewGroup, false));
                }

                @Override
                protected void onBindViewHolder(@NonNull final AccountInfo holder, int position, @NonNull final AccountLayout model) {
                    ref.child(userid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String name = dataSnapshot.child("name").getValue().toString();
                            final String age = dataSnapshot.child("idade").getValue().toString();
                            holder.setName(name);
                            holder.setAge(age);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            };
            accountUsers.setAdapter(AccountAdapter);
        }
    }

    public static class AccountInfo extends RecyclerView.ViewHolder{
        View AccountL;

        public AccountInfo(@NonNull View itemView) {
            super(itemView);

            AccountL = itemView;
        }

        public void setName(String name){
            TextView AccountName = AccountL.findViewById(R.id.AccountName0);
            AccountName.setText(name);
        }

        public void setAge(String age){
            TextView AccountAge = AccountL.findViewById(R.id.AccountAge0);
            AccountAge.setText(age);
        }
    }
}
