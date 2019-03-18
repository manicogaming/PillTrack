package com.pap.diogo.pilltrack.Accounts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private EditText txtEmail, txtPassword;
    private FirebaseAuth Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Login = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
    }

    @Override
    public void onClick(View v) {
        userLogin();
    }

    private void userLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        Login.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent Home = new Intent(Login.this, MainActivity.class);
                    Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Home);
                    finish(); // call this to finish the current activity
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
