package com.pap.diogo.pilltrack.Appoints;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAppoint extends AppCompatActivity implements View.OnClickListener {
    private EditText txtNameAppoint, txtDate;
    private AutoCompleteTextView txtHospital;
    private Button btnAddAppoint;
    private FirebaseUser user;
    private DatabaseReference pRef;
    private String userid, HospitalLocation;

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
        pRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);

        String[] hospitals = {"Baixo Vouga", "Entre o Douro e Vouga", "Dr. Francisco Zagalo", "José Joaquim Fernandes", "Escala Braga", "Senhora Oliveira Guimarães", "Santa Maria Maior",
                "Nordeste", "Cova da Beira", "Castelo Branco", "Coimbra", "Figueira da Foz", "Coimbra Francisco Gentil", "Espírito Santo - Évora", "Algarve", "Guarda", "Leiria",
                "Oeste", "Lisboa Central", "Lisboa Norte", "Lisboa Ocidental", "Psiquiátrico de Lisboa", "Vila Franca de Xira", "Beatriz Ângelo", "Cascais",
                "Prof. Dr. Fernando Fonseca", "Lisboa Francisco Gentil", "Norte Alentejano", "São João", "Eduardo Santos Silva", "Médio Ave", "Porto", "Tâmega e Sousa", "Vila do Conde",
                "Magalhães Lemos", "Porto Francisco Gentil", "Pedro Hispano", "Médio Tejo", "Santarém", "Barreiro Montijo", "Setúbal", "Garcia de Orta", "Litoral Alentejano",
                "Alto Minho", "Trás-os-montes e Alto Douro", "Tondela-Viseu"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, hospitals);

        txtHospital.setThreshold(1);
        txtHospital.setAdapter(adapter);

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
                new DatePickerDialog(AddAppoint.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
    }

    @Override
    public void onClick(View v) {
        String name = txtNameAppoint.getText().toString().trim();
        String hospital = txtHospital.getText().toString().trim();
        String txtdate = txtDate.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(hospital) && TextUtils.isEmpty(txtdate)) {
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
            HospitalLocation = "40.6336036,-8.6572487";
        }
        if (txtHospital.getText().toString().matches("Entre o Douro e Vouga")) {
            HospitalLocation = "40.9302165,-8.5496593";
        }
        if (txtHospital.getText().toString().matches("Dr. Francisco Zagalo")) {
            HospitalLocation = "40.857524,-8.6335287";
        }
        if (txtHospital.getText().toString().matches("José Joaquim Fernandes")) {
            HospitalLocation = "38.0141498,-7.8719438";
        }
        if (txtHospital.getText().toString().matches("Escala Brage")) {
            HospitalLocation = "41.5679778,-8.4012003";
        }
        if (txtHospital.getText().toString().matches("Senhora Oliveira Guimarães")) {
            HospitalLocation = "41.44191,-8.3074347";
        }
        if (txtHospital.getText().toString().matches("Santa Maria Maior")) {
            HospitalLocation = "41.533184,-8.6185787";
        }
        if (txtHospital.getText().toString().matches("Nordeste")) {
            HospitalLocation = "41.8019098,-6.7688308";
        }
        if (txtHospital.getText().toString().matches("Cova da Beira")) {
            HospitalLocation = "40.2662442,-7.4955216";
        }
        if (txtHospital.getText().toString().matches("Castelo Branco")) {
            HospitalLocation = "39.821828,-7.500954";
        }
        if (txtHospital.getText().toString().matches("Coimbra")) {
            HospitalLocation = "40.220667,-8.4151661";
        }
        if (txtHospital.getText().toString().matches("Figueira da Foz")) {
            HospitalLocation = "40.130863,-8.8622816";
        }
        if (txtHospital.getText().toString().matches("Coimbra Francisco Gentil")) {
            HospitalLocation = "40.2171298,-8.4120016";
        }
        if (txtHospital.getText().toString().matches("Espírito Santo - Évora")) {
            HospitalLocation = "38.5685733,-7.9052961";
        }
        if (txtHospital.getText().toString().matches("Algarve")) {
            HospitalLocation = "37.0245733,-7.931173";
        }
        if (txtHospital.getText().toString().matches("Guarda")) {
            HospitalLocation = "40.5309052,-7.2777112";
        }
        if (txtHospital.getText().toString().matches("Leiria")) {
            HospitalLocation = "39.7433166,-8.7964385";
        }
        if (txtHospital.getText().toString().matches("Oeste")) {
            HospitalLocation = "39.4046223,-9.1318501";
        }
        if (txtHospital.getText().toString().matches("Lisboa Central")) {
            HospitalLocation = "38.7170859,-9.1379516";
        }
        if (txtHospital.getText().toString().matches("Lisboa Norte")) {
            HospitalLocation = "38.765411,-9.1617467";
        }
        if (txtHospital.getText().toString().matches("Lisboa Ocidental")) {
            HospitalLocation = "38.7654768,-9.1945774";
        }
        if (txtHospital.getText().toString().matches("Psiquiátrico de Lisboa")) {
            HospitalLocation = "38.7576872,-9.14862";
        }
        if (txtHospital.getText().toString().matches("Vila Franca de Xira")) {
            HospitalLocation = "38.9771976,-8.9871135";
        }
        if (txtHospital.getText().toString().matches("Beatriz Ângelo")) {
            HospitalLocation = "38.8215556,-9.1785221";
        }
        if (txtHospital.getText().toString().matches("Cascais")) {
            HospitalLocation = "38.7300133,-9.4203311";
        }
        if (txtHospital.getText().toString().matches("Prof. Dr. Fernando Fonseca")) {
            HospitalLocation = "38.7435637,-9.2480437";
        }
        if (txtHospital.getText().toString().matches("Lisboa Francisco Gentil")) {
            HospitalLocation = "38.7398702,-9.1635497";
        }
        if (txtHospital.getText().toString().matches("Norte Alentejano")) {
            HospitalLocation = "39.3002158,-7.4296425";
        }
        if (txtHospital.getText().toString().matches("São João")) {
            HospitalLocation = "41.1814421,-8.6032293";
        }
        if (txtHospital.getText().toString().matches("Eduardo Santos Silva")) {
            HospitalLocation = "41.1815541,-8.6710803";
        }
        if (txtHospital.getText().toString().matches("Médio Ave")) {
            HospitalLocation = "41.377521,-8.5360971";
        }
        if (txtHospital.getText().toString().matches("Porto")) {
            HospitalLocation = "41.1472309,-8.6217242";
        }
        if (txtHospital.getText().toString().matches("Tâmega e Sousa")) {
            HospitalLocation = "41.1970225,-8.3117171";
        }
        if (txtHospital.getText().toString().matches("Vila do Conde")) {
            HospitalLocation = "41.3689037,-8.7692049";
        }
        if (txtHospital.getText().toString().matches("Magalhães Lemos")) {
            HospitalLocation = "41.1775305,-8.6685679";
        }
        if (txtHospital.getText().toString().matches("Porto Francisco Gentil")) {
            HospitalLocation = "41.1823645,-8.6080161";
        }
        if (txtHospital.getText().toString().matches("Pedro Hispano")) {
            HospitalLocation = "41.1818182,-8.6655801";
        }
        if (txtHospital.getText().toString().matches("Médio Tejo")) {
            HospitalLocation = "39.5388913,-8.536345";
        }
        if (txtHospital.getText().toString().matches("Santarém")) {
            HospitalLocation = "39.2410796,-8.6988352";
        }
        if (txtHospital.getText().toString().matches("Barreiro Montijo")) {
            HospitalLocation = "38.6546747,-9.0604176";
        }
        if (txtHospital.getText().toString().matches("Setúbal")) {
            HospitalLocation = "38.5090454,-8.9251669";
        }
        if (txtHospital.getText().toString().matches("Garcia de Orta")) {
            HospitalLocation = "38.6740734,-9.179027";
        }
        if (txtHospital.getText().toString().matches("Litoral Alentejano")) {
            HospitalLocation = "38.0400042,-8.7346887";
        }
        if (txtHospital.getText().toString().matches("Alto Minho")) {
            HospitalLocation = "41.6877095,-8.8249634";
        }
        if (txtHospital.getText().toString().matches("Trás-os-montes e Alto Douro")) {
            HospitalLocation = "41.3101655,-7.7622813";
        }
        if (txtHospital.getText().toString().matches("Tondela-Viseu")) {
            HospitalLocation = "40.6505725,-7.9075104";
        }
    }
}