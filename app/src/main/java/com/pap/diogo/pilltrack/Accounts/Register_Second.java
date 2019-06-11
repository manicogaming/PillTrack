package com.pap.diogo.pilltrack.Accounts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;

public class Register_Second extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText txtDoB, txtWeight, txtHeight;
    private Spinner txtSex;
    private FirebaseAuth Register;
    private String Email, Password, Name, DoB, Weight, Height, Sex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_pagetwo);

        Register = FirebaseAuth.getInstance();

        txtDoB = findViewById(R.id.txtDoB);
        txtWeight = findViewById(R.id.txtWeight);
        txtHeight = findViewById(R.id.txtHeight);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        String[] arraySpinner = new String[]{
                "Masculino", "Feminino"
        };

        txtSex = findViewById(R.id.txtSex);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        txtSex.setAdapter(adapter);

        txtDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int mYear, mMonth, mDay;
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog mDatePicker = new DatePickerDialog(Register_Second.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        if (selectedyear == mYear && (selectedmonth + 1) == mMonth + 1) {
                            if (selectedday < mDay) {
                                Toast.makeText(Register_Second.this, "Data inválida", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        txtDoB.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);

                    }
                }, mYear, mMonth, mDay);

                if (!mDatePicker.isShowing()) {
                    mDatePicker.show();
                }
            }
        });

        Bundle extras = getIntent().getExtras();

        Email = extras.getString("Email");
        Password = extras.getString("Password");
        Name = extras.getString("Name");
    }

    @Override
    public void onClick(View v) {
        DoB = txtDoB.getText().toString().trim();
        Sex = txtSex.getSelectedItem().toString().trim();
        Weight = txtWeight.getText().toString().trim();
        Height = txtHeight.getText().toString().trim();

        if ((TextUtils.isEmpty(DoB) && TextUtils.isEmpty(Weight) && TextUtils.isEmpty(Height))
                || (TextUtils.isEmpty(DoB) && TextUtils.isEmpty(Weight))
                || (TextUtils.isEmpty(DoB) && TextUtils.isEmpty(Height))
                || (TextUtils.isEmpty(Weight) && TextUtils.isEmpty(Height))) {
            Toast.makeText(this, "Os campos não podem ficar vazios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(DoB)) {
            Toast.makeText(this, "Data de nascimento não pode ficar vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Weight)) {
            Toast.makeText(this, "Peso não pode ficar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Height)) {
            Toast.makeText(this, "Altura não pode ficar vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        Register.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            RegisterInfo userInformation = new RegisterInfo(Name, DoB, Sex, Weight, Height);

                            FirebaseDatabase.getInstance().getReference("Users").child(Register.getCurrentUser().getUid()).setValue(userInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register_Second.this, "Registo com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent home = new Intent(Register_Second.this, MainActivity.class);
                                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(home);
                                        finish(); // call this to finish the current activity
                                    }
                                }
                            });
                        } else {
                            //If user already exists show a message
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register_Second.this, "E-mail já registado.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register_Second.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
