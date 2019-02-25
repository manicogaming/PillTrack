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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class ChangePW extends AppCompatActivity implements View.OnClickListener{
    private EditText txtPassword;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepw);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        txtPassword = findViewById(R.id.txtPassword);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        String newPassword = txtPassword.getText().toString().trim();

        user.updatePassword(newPassword)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePW.this, "Palavra-Passe atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent Home = new Intent(ChangePW.this, MainActivity.class);
                        Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Home);
                        finish(); // call this to finish the current activity
                    }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        Toast.makeText(ChangePW.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
