package com.pap.diogo.pilltrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAppoint extends AppCompatActivity implements View.OnClickListener{
    private EditText txtNameAppoint, txtHospital, txtDate;
    private Button btnAddAppoint;
    private FirebaseUser user;
    private DatabaseReference mDatabase, mUsers, pRef;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addappoint);

        txtNameAppoint = findViewById(R.id.txtNameAppoint);
        txtHospital = findViewById(R.id.txtHospital);
        txtDate = findViewById(R.id.txtDate);
        btnAddAppoint = findViewById(R.id.btnAddAppoint);
        btnAddAppoint.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsers = mDatabase.child("Users").child(userid);
        pRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);
    }

    @Override
    public void onClick(View v) {
        String name = txtNameAppoint.getText().toString().trim();
        String hostpial = txtHospital.getText().toString().trim();
        String date = txtDate.getText().toString().trim();

        AppointInfo AppointInfo = new AppointInfo(name, hostpial, date);
        pRef.child(name).setValue(AppointInfo);
        Toast.makeText(AddAppoint.this, "Consulta adicionada com sucesso!", Toast.LENGTH_SHORT).show();
        Intent Home = new Intent(AddAppoint.this, MainActivity.class);
        startActivity(Home);
    }
}
