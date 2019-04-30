package com.pap.diogo.pilltrack.Appoints;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAppoint extends AppCompatActivity implements View.OnClickListener {
    private EditText txtDate;
    private AutoCompleteTextView txtHospital, txtSpecialty;
    private Button btnAddAppoint;
    private FirebaseUser user;
    private DatabaseReference pRef;
    private String userid, HospitalLocation, HName, eName;

    /*TODO
    Adicionar a opção de ter exames ou consultas.
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addappoint);

        txtSpecialty = findViewById(R.id.txtSpecialty);
        txtHospital = findViewById(R.id.txtHospital);
        txtDate = findViewById(R.id.txtDate);
        btnAddAppoint = findViewById(R.id.btnAddAppoint);
        btnAddAppoint.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        pRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);

        DatabaseReference hRef = FirebaseDatabase.getInstance().getReference("Hospitals");
        final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(AddAppoint.this, android.R.layout.simple_list_item_1);
        hRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot hospitals : dataSnapshot.getChildren()) {
                    HName = hospitals.child("name").getValue(String.class);
                    autoComplete.add(HName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        txtHospital.setThreshold(1);
        txtHospital.setAdapter(autoComplete);

        DatabaseReference eRef = FirebaseDatabase.getInstance().getReference("Specialty");
        final ArrayAdapter<String> arraySpinner = new ArrayAdapter<>(AddAppoint.this, android.R.layout.simple_list_item_1);
        eRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot hospitals : dataSnapshot.getChildren()) {
                    eName = hospitals.child("name").getValue(String.class);
                    arraySpinner.add(eName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        txtSpecialty.setThreshold(1);
        txtSpecialty.setAdapter(arraySpinner);

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "PT"));

                txtDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddAppoint.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        txtHospital.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = txtHospital.getText().toString();

                    ListAdapter listAdapter = txtHospital.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    txtHospital.setText("");
                }
            }
        });

        txtSpecialty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = txtSpecialty.getText().toString();

                    ListAdapter listAdapter = txtSpecialty.getAdapter();
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        String temp = listAdapter.getItem(i).toString();
                        if (str.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    txtSpecialty.setText("");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        String name = txtSpecialty.getText().toString().trim();
        String hospital = txtHospital.getText().toString().trim();
        String txtdate = txtDate.getText().toString().trim();

        if ((TextUtils.isEmpty(name) && TextUtils.isEmpty(hospital) && TextUtils.isEmpty(txtdate))
                || (TextUtils.isEmpty(name) && TextUtils.isEmpty(hospital))
                || (TextUtils.isEmpty(name) && TextUtils.isEmpty(txtdate))
                || (TextUtils.isEmpty(hospital) && TextUtils.isEmpty(txtdate))) {
            Toast.makeText(this, "Os campos não podem ficar vazios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nome não pode ficar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(hospital)) {
            Toast.makeText(this, "Hospital não pode ficar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(txtdate)) {
            Toast.makeText(this, "Data não pode ficar vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        getHospitalLocation();
        AppointInfo AppointInfo = new AppointInfo(name, hospital, txtdate, HospitalLocation);
        pRef.child(name).setValue(AppointInfo);
        Toast.makeText(AddAppoint.this, "Consulta adicionada com sucesso!", Toast.LENGTH_SHORT).show();
        Intent Home = new Intent(AddAppoint.this, MainActivity.class);
        startActivity(Home);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void getHospitalLocation() {
        if (txtHospital.getText().toString().matches("Baixo Vouga")) {
            HospitalLocation = "40.633787, -8.654963";
        }
        if (txtHospital.getText().toString().matches("Entre o Douro e Vouga")) {
            HospitalLocation = "40.930216, -8.547473";
        }
        if (txtHospital.getText().toString().matches("Francisco Zagalo")) {
            HospitalLocation = "40.857527, -8.631336";
        }
        if (txtHospital.getText().toString().matches("José Joaquim Fernandes")) {
            HospitalLocation = "38.014149, -7.869755";
        }
        if (txtHospital.getText().toString().matches("Escala Brage")) {
            HospitalLocation = "41.567980, -8.399012";
        }
        if (txtHospital.getText().toString().matches("Senhora Oliveira Guimarães")) {
            HospitalLocation = "41.441908, -8.305245";
        }
        if (txtHospital.getText().toString().matches("Santa Maria Maior")) {
            HospitalLocation = "41.533183, -8.616388";
        }
        if (txtHospital.getText().toString().matches("Nordeste")) {
            HospitalLocation = "41.802219, -6.768147";
        }
        if (txtHospital.getText().toString().matches("Cova da Beira")) {
            HospitalLocation = "40.266136, -7.492287";
        }
        if (txtHospital.getText().toString().matches("Castelo Branco")) {
            HospitalLocation = "39.822492, -7.499889";
        }
        if (txtHospital.getText().toString().matches("Coimbra")) {
            HospitalLocation = "40.220665, -8.412978";
        }
        if (txtHospital.getText().toString().matches("Figueira da Foz")) {
            HospitalLocation = "40.130862, -8.860094";
        }
        if (txtHospital.getText().toString().matches("Coimbra Francisco Gentil")) {
            HospitalLocation = "40.217128, -8.409814";
        }
        if (txtHospital.getText().toString().matches("Espírito Santo - Évora")) {
            HospitalLocation = "38.568572, -7.903106";
        }
        if (txtHospital.getText().toString().matches("Algarve")) {
            HospitalLocation = "37.024569, -7.928985";
        }
        if (txtHospital.getText().toString().matches("Guarda")) {
            HospitalLocation = "40.530903, -7.275523";
        }
        if (txtHospital.getText().toString().matches("Leiria")) {
            HospitalLocation = "39.743314, -8.794250";
        }
        if (txtHospital.getText().toString().matches("Oeste")) {
            HospitalLocation = "39.404620, -9.129661";
        }
        if (txtHospital.getText().toString().matches("Lisboa Central")) {
            HospitalLocation = "38.717123, -9.137085";
        }
        if (txtHospital.getText().toString().matches("Lisboa Norte")) {
            HospitalLocation = "38.765411, -9.159559";
        }
        if (txtHospital.getText().toString().matches("Lisboa Ocidental")) {
            HospitalLocation = "38.726995, -9.233875";
        }
        if (txtHospital.getText().toString().matches("Psiquiátrico de Lisboa")) {
            HospitalLocation = "38.757685, -9.146433";
        }
        if (txtHospital.getText().toString().matches("Vila Franca de Xira")) {
            HospitalLocation = "38.977198, -8.984925";
        }
        if (txtHospital.getText().toString().matches("Beatriz Ângelo")) {
            HospitalLocation = "38.821554, -9.176333";
        }
        if (txtHospital.getText().toString().matches("Cascais")) {
            HospitalLocation = "38.730010, -9.418145";
        }
        if (txtHospital.getText().toString().matches("Professor Fernando Fonseca")) {
            HospitalLocation = "38.743577, -9.245854";
        }
        if (txtHospital.getText().toString().matches("Lisboa Francisco Gentil")) {
            HospitalLocation = "38.739869, -9.161362";
        }
        if (txtHospital.getText().toString().matches("Norte Alentejano")) {
            HospitalLocation = "39.300215, -7.427454";
        }
        if (txtHospital.getText().toString().matches("São João")) {
            HospitalLocation = "41.181343, -8.600669";
        }
        if (txtHospital.getText().toString().matches("Eduardo Santos Silva")) {
            HospitalLocation = "41.106352, -8.592435";
        }
        if (txtHospital.getText().toString().matches("Médio Ave")) {
            HospitalLocation = "41.412913, -8.521811";
        }
        if (txtHospital.getText().toString().matches("Porto")) {
            HospitalLocation = "41.147228, -8.619534";
        }
        if (txtHospital.getText().toString().matches("Tâmega e Sousa")) {
            HospitalLocation = "41.197027, -8.309523";
        }
        if (txtHospital.getText().toString().matches("Vila do Conde")) {
            HospitalLocation = "41.382959, -8.758802";
        }
        if (txtHospital.getText().toString().matches("Magalhães Lemos")) {
            HospitalLocation = "41.177631, -8.663650";
        }
        if (txtHospital.getText().toString().matches("Porto Francisco Gentil")) {
            HospitalLocation = "41.182737, -8.604551";
        }
        if (txtHospital.getText().toString().matches("Pedro Hispano")) {
            HospitalLocation = "41.181819, -8.663393";
        }
        if (txtHospital.getText().toString().matches("Médio Tejo")) {
            HospitalLocation = "39.467919, -8.537029";
        }
        if (txtHospital.getText().toString().matches("Santarém")) {
            HospitalLocation = "39.241077, -8.696647";
        }
        if (txtHospital.getText().toString().matches("Barreiro Montijo")) {
            HospitalLocation = "38.654673, -9.058227";
        }
        if (txtHospital.getText().toString().matches("Setúbal")) {
            HospitalLocation = "38.529196, -8.881083";
        }
        if (txtHospital.getText().toString().matches("Garcia de Orta")) {
            HospitalLocation = "38.674072, -9.176839";
        }
        if (txtHospital.getText().toString().matches("Litoral Alentejano")) {
            HospitalLocation = "38.040003, -8.732500";
        }
        if (txtHospital.getText().toString().matches("Alto Minho")) {
            HospitalLocation = "41.697339, -8.832486";
        }
        if (txtHospital.getText().toString().matches("Trás-os-montes e Alto Douro")) {
            HospitalLocation = "41.310163, -7.760095";
        }
        if (txtHospital.getText().toString().matches("Tondela-Viseu")) {
            HospitalLocation = "40.650466, -7.905616";
        }
    }
}