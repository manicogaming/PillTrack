package com.pap.diogo.pilltrack.Pills;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class AddPill extends AppCompatActivity implements View.OnClickListener{
    private EditText txtNamePill, txtPillFunc, txtInterval;
    private Button btnAddPill;
    private FirebaseUser user;
    private DatabaseReference mDatabase, pRef;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpill);

        txtNamePill = findViewById(R.id.txtNamePill);
        txtPillFunc = findViewById(R.id.txtPillFunc);
        txtInterval = findViewById(R.id.txtInterval);
        btnAddPill = findViewById(R.id.btnAddPill);
        btnAddPill.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pRef = FirebaseDatabase.getInstance().getReference().child("Pills").child(userid);
    }

    @Override
    public void onClick(View v) {
        String name = txtNamePill.getText().toString().trim();
        String pillfunc = txtPillFunc.getText().toString().trim();
        String interval = txtInterval.getText().toString().trim();

        PillInfo PillInfo = new PillInfo(name, pillfunc, interval);
        pRef.child(name).setValue(PillInfo);
        Toast.makeText(AddPill.this, "Medicamento adicionado com sucesso!", Toast.LENGTH_SHORT).show();
        Intent Home = new Intent(AddPill.this, MainActivity.class);
        startActivity(Home);
    }
}
