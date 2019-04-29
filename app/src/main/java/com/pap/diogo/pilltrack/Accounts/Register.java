package com.pap.diogo.pilltrack.Accounts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

import org.w3c.dom.Text;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText txtName, txtEmail, txtPassword, txtAge;
    private FirebaseAuth Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Register = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        txtName = findViewById(R.id.txtname);
        txtEmail = findViewById(R.id.txtemail);
        txtPassword = findViewById(R.id.txtpassword);
        txtAge = findViewById(R.id.txtage);
    }

    @Override
    public void onClick(View v) {
        userRegister();
    }

    private void userRegister() {
        final String Email = txtEmail.getText().toString().trim();
        final String Password = txtPassword.getText().toString().trim();
        final String Name = txtName.getText().toString().trim();
        final String Age = txtAge.getText().toString().trim();

        if ((TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password) && TextUtils.isEmpty(Name) && TextUtils.isEmpty(Age))
                || (TextUtils.isEmpty(Password) && TextUtils.isEmpty(Name) && TextUtils.isEmpty(Age))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Name) && TextUtils.isEmpty(Age))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password) && TextUtils.isEmpty(Age))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Name) && TextUtils.isEmpty(Password))
                || (TextUtils.isEmpty(Name) && TextUtils.isEmpty(Age))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Name))
                || (TextUtils.isEmpty(Age) && TextUtils.isEmpty(Password))
                || (TextUtils.isEmpty(Name) && TextUtils.isEmpty(Password))
                || (TextUtils.isEmpty(Email) && TextUtils.isEmpty(Age))) {
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

        if (TextUtils.isEmpty(Age)) {
            Toast.makeText(this, "Idade não pode ficar vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        Register.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            RegisterInfo userInformation = new RegisterInfo(Name, Age);

                            FirebaseDatabase.getInstance().getReference("Users").child(Register.getCurrentUser().getUid()).setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registo com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent home = new Intent(Register.this, MainActivity.class);
                                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(home);
                                        finish(); // call this to finish the current activity
                                    }
                                }
                            });
                        } else {
                            //If user already exists show a message
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register.this, "E-mail já registado.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
