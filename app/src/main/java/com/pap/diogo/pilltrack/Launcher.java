package com.pap.diogo.pilltrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pap.diogo.pilltrack.Accounts.Login;
import com.pap.diogo.pilltrack.Accounts.Register;

public class Launcher extends AppCompatActivity implements View.OnClickListener {
    private Button btnNovo;
    private Button btnExistente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        btnNovo = findViewById(R.id.btnNovo);
        btnNovo.setOnClickListener(this);
        btnExistente = findViewById(R.id.btnExistente);
        btnExistente.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent VerifyLogin = new Intent(Launcher.this, MainActivity.class);
            VerifyLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(VerifyLogin);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnNovo) {
            Intent register = new Intent(this, Register.class);
            startActivity(register);
        }
        if (v == btnExistente) {
            Intent login = new Intent(Launcher.this, Login.class);
            startActivity(login);
        }
    }
}
