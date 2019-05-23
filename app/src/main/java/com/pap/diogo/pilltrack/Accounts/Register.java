package com.pap.diogo.pilltrack.Accounts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button btnNext;
    private EditText txtName, txtEmail, txtPassword;
    private String Email, Password, Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_pageone);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Register.this, Register_Second.class);
        Bundle extras = new Bundle();

        Email = txtEmail.getText().toString().trim();
        Password = txtPassword.getText().toString().trim();
        Name = txtName.getText().toString().trim();

        if ((TextUtils.isEmpty(Email) && TextUtils.isEmpty(Name) && TextUtils.isEmpty(Password))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Name))
                || (TextUtils.isEmpty(Name) && TextUtils.isEmpty(Password))) {
            Toast.makeText(this, "Os campos não podem ficar vazios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(this, "Email não pode ficar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(this, "Palavra-Passe não pode ficar vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "Nome não pode ficar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        extras.putString("Email", Email);
        extras.putString("Password", Password);
        extras.putString("Name", Name);
        i.putExtras(extras);
        startActivity(i);
    }

   /* private void userRegister() {

    }*/
}
