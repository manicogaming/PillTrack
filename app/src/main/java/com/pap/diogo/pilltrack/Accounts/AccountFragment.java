package com.pap.diogo.pilltrack.Accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pap.diogo.pilltrack.Launcher;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class AccountFragment extends Fragment {
    private ArrayAdapter<String> adapter;
    private TextView AccountName, AccountAge, AccountSex, AccountWeight, AccountHeight;
    private EditText EAccountName, EAccountAge, EAccountWeight, EAccountHeight;
    private Spinner EAccountSex;
    private Button btnSaveAccountInfos, btnEditAccountInfos, btnAccountChangePass, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.account, container, false);

        AccountName = mMainView.findViewById(R.id.AccountName);
        EAccountName = mMainView.findViewById(R.id.EAccountName);

        AccountAge = mMainView.findViewById(R.id.AccountAge);
        EAccountAge = mMainView.findViewById(R.id.EAccountAge);

        AccountSex = mMainView.findViewById(R.id.AccountSex);
        EAccountSex = mMainView.findViewById(R.id.EAccountSex);

        AccountWeight = mMainView.findViewById(R.id.AccountWeight);
        EAccountWeight = mMainView.findViewById(R.id.EAccountWeight);

        AccountHeight = mMainView.findViewById(R.id.AccountHeight);
        EAccountHeight = mMainView.findViewById(R.id.EAccountHeight);

        btnSaveAccountInfos = mMainView.findViewById(R.id.SaveAccountInfos);
        btnEditAccountInfos = mMainView.findViewById(R.id.EditAccountInfos);
        btnAccountChangePass = mMainView.findViewById(R.id.AccountChangePass);
        btnLogout = mMainView.findViewById(R.id.Logout);

        String[] arraySpinner = new String[]{
                "Masculino", "Feminino"
        };

        adapter = new ArrayAdapter(getContext(), R.layout.spinner_item_account, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        EAccountSex.setAdapter(adapter);

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue(String.class);
                String age = dataSnapshot.child("age").getValue(String.class);
                String sex = dataSnapshot.child("sex").getValue(String.class);
                String weight = dataSnapshot.child("weight").getValue(String.class);
                String height = dataSnapshot.child("height").getValue(String.class);

                AccountName.setText(name);
                EAccountName.setText(name);

                AccountAge.setText(age + " anos");
                EAccountAge.setText(age);

                AccountSex.setText(sex);

                AccountWeight.setText(weight + " kg");
                EAccountWeight.setText(weight);

                AccountHeight.setText(height + " cm");
                EAccountHeight.setText(height);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addListenerForSingleValueEvent(eventListener);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent Launcher = new Intent(getContext(), Launcher.class);
                Launcher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Launcher);
            }
        });

        btnAccountChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ChangePW = new Intent(getContext(), ChangePW.class);
                startActivity(ChangePW);
            }
        });

        btnEditAccountInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountName.setVisibility(View.GONE);
                AccountAge.setVisibility(View.GONE);
                AccountSex.setVisibility(View.GONE);
                AccountWeight.setVisibility(View.GONE);
                AccountHeight.setVisibility(View.GONE);

                EAccountName.setVisibility(View.VISIBLE);
                EAccountAge.setVisibility(View.VISIBLE);
                EAccountSex.setVisibility(View.VISIBLE);
                EAccountWeight.setVisibility(View.VISIBLE);
                EAccountHeight.setVisibility(View.VISIBLE);

                btnEditAccountInfos.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnAccountChangePass.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.SaveAccountInfos);
                btnAccountChangePass.setLayoutParams(params);

                btnSaveAccountInfos.setVisibility(View.VISIBLE);
            }
        });

        btnSaveAccountInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountName.setVisibility(View.VISIBLE);
                AccountAge.setVisibility(View.VISIBLE);
                AccountSex.setVisibility(View.VISIBLE);
                AccountWeight.setVisibility(View.VISIBLE);
                AccountHeight.setVisibility(View.VISIBLE);

                EAccountName.setVisibility(View.GONE);
                EAccountAge.setVisibility(View.GONE);
                EAccountSex.setVisibility(View.GONE);
                EAccountWeight.setVisibility(View.GONE);
                EAccountHeight.setVisibility(View.GONE);

                btnEditAccountInfos.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnAccountChangePass.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.EditAccountInfos);
                btnAccountChangePass.setLayoutParams(params);

                btnSaveAccountInfos.setVisibility(View.GONE);

                String Name = EAccountName.getText().toString().trim();
                String Age = EAccountAge.getText().toString().trim();
                String Sex = EAccountSex.getSelectedItem().toString().trim();
                String Weight = EAccountWeight.getText().toString().trim();
                String Height = EAccountHeight.getText().toString().trim();

                RegisterInfo userInformation = new RegisterInfo(Name, Age, Sex, Weight, Height);
                ref.setValue(userInformation);

                ((MainActivity) getActivity()).setNavItem(3);
            }
        });
    }
}

