package com.pap.diogo.pilltrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout pills_layout, appoint_layout, add_pills_layout, add_appoints_layout, account_layout, add_button;
    private TextView AccountName0, AccountAge0;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private View view;

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
        //account_layout.setVisibility(View.VISIBLE);

        add_button.setVisibility(View.VISIBLE);

        /*Button accountChangePass = findViewById(R.id.AccountChangePass);

        accountChangePass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ChangePW = new Intent(MainActivity.this, ChangePW.class);
                startActivity(ChangePW);
            }
        });*/
    }


    private void Appointment() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.add_appoints);

        pills_layout.setVisibility(View.GONE);
        appoint_layout.setVisibility(View.GONE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.VISIBLE);
        //account_layout.setVisibility(View.GONE);

        add_button.setVisibility(View.VISIBLE);
    }

    private void Pills() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) add_button.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.add_pills);

        pills_layout.setVisibility(View.GONE);
        appoint_layout.setVisibility(View.GONE);
        add_pills_layout.setVisibility(View.VISIBLE);
        add_appoints_layout.setVisibility(View.GONE);
        //account_layout.setVisibility(View.GONE);

        add_button.setVisibility(View.VISIBLE);
    }

    private void Home() {
        pills_layout.setVisibility(View.VISIBLE);
        appoint_layout.setVisibility(View.VISIBLE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.GONE);
        //account_layout.setVisibility(View.GONE);

        add_button.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent VerifyLogin = new Intent(MainActivity.this, Launcher.class);
            VerifyLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(VerifyLogin);
        }

        pills_layout = findViewById(R.id.pills_layout);
        appoint_layout = findViewById(R.id.appoint_layout);
        add_pills_layout = findViewById(R.id.add_pills);
        add_appoints_layout = findViewById(R.id.add_appoints);
        //account_layout = findViewById(R.id.accountlist);

        AccountName0 = findViewById(R.id.AccountName0);
        AccountAge0 = findViewById(R.id.AccountAge0);

        add_button = findViewById(R.id.add);

        pills_layout.setVisibility(View.VISIBLE);
        appoint_layout.setVisibility(View.VISIBLE);
        add_pills_layout.setVisibility(View.GONE);
        add_appoints_layout.setVisibility(View.GONE);
        //account_layout.setVisibility(View.GONE);
        add_button.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.accountlist);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout root;
        public TextView txtTitle;
        public TextView txtDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.account);
            txtTitle = itemView.findViewById(R.id.AccountName0);
            txtDesc = itemView.findViewById(R.id.AccountAge0);
        }

        public void setTxtTitle(String string) {
            txtTitle.setText(string);
        }


        public void setTxtDesc(String string) {
            txtDesc.setText(string);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        FirebaseRecyclerOptions<Account> options =
                new FirebaseRecyclerOptions.Builder<Account>()
                        .setQuery(query, new SnapshotParser<Account>() {
                            @NonNull
                            @Override
                            public Account parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Account(snapshot.child("name").getValue().toString(),
                                        snapshot.child("idade").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Account, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.account, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Account model) {
                holder.setTxtTitle(model.getName());
                holder.setTxtDesc(model.getIdade());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        FirebaseRecyclerOptions<Account> options =
                new FirebaseRecyclerOptions.Builder<Account>()
                        .setQuery(query, new SnapshotParser<Account>() {
                            @NonNull
                            @Override
                            public Account parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Account(snapshot.child("name").getValue().toString(),
                                        snapshot.child("idade").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Account, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.account, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Account model) {
                holder.setTxtTitle(model.getName());
                holder.setTxtDesc(model.getIdade());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.stopListening();
    }
};