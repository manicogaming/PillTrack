package com.pap.diogo.pilltrack.Accounts;

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
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class ChangePW extends AppCompatActivity implements View.OnClickListener {
    private EditText txtOldPassword, txtNewPassword, txtEmailCHPW;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepw);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        txtEmailCHPW = findViewById(R.id.txtEmailCHPW);
        txtOldPassword = findViewById(R.id.txtOldPassword);
        txtNewPassword = findViewById(R.id.txtNewPassword);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        final String newPassword = txtNewPassword.getText().toString().trim();
        final String oldPassword = txtOldPassword.getText().toString().trim();
        final String email = txtEmailCHPW.getText().toString().trim();
        String userEmail = user.getEmail();

        if (email.equals(userEmail)) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, oldPassword);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePW.this, "Palavra-Passe mudada com sucesso!", Toast.LENGTH_SHORT).show();
                                            Intent Home = new Intent(ChangePW.this, MainActivity.class);
                                            Home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(Home);
                                            finish(); // call this to finish the current activity
                                        }
                                    }
                                });
                            }
                        }
                    });
        } else {
            Toast.makeText(ChangePW.this, "Este Email não pertence à conta!", Toast.LENGTH_SHORT).show();
        }
    }
}
