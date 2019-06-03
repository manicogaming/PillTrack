package com.pap.diogo.pilltrack.Pills;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddPill extends AppCompatActivity implements View.OnClickListener {
    private EditText txtNamePill, txtPillFunc, txtPillHour, txtStartDate, txtEndDate;
    private Spinner txtInterval;
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
        txtPillHour = findViewById(R.id.txtPillHour);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        btnAddPill = findViewById(R.id.btnAddPill);
        btnAddPill.setOnClickListener(this);

        String[] arraySpinner = new String[]{
                "4 em 4 horas", "8 em 8 horas", "12 em 12 horas", "De manhã e à noite", "De manhã", "À noite"
        };
        txtInterval = findViewById(R.id.txtInterval);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtInterval.setAdapter(adapter);

        txtPillHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddPill.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txtPillHour.setText(checkDigit(selectedHour) + ":" + checkDigit(selectedMinute));
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Selecione uma Hora");
                mTimePicker.show();
            }
        });

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int mYear, mMonth, mDay;
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog mDatePicker = new DatePickerDialog(AddPill.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        if (selectedyear == mYear && (selectedmonth + 1) == mMonth + 1) {
                            if (selectedday < mDay) {
                                Toast.makeText(AddPill.this, "Data inválida", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        txtStartDate.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);
                        TextInputLayout pillenddate;
                        pillenddate = findViewById(R.id.pillenddate);
                        pillenddate.setVisibility(View.VISIBLE);
                    }
                }, mYear, mMonth, mDay);

                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                if (!mDatePicker.isShowing()) {
                    mDatePicker.show();
                }
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int mYear, mMonth, mDay;
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog mDatePicker = new DatePickerDialog(AddPill.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        if (selectedyear == mYear && (selectedmonth + 1) == mMonth + 1) {
                            if (selectedday < mDay) {
                                Toast.makeText(AddPill.this, "Data inválida", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        txtEndDate.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);

                    }
                }, mYear, mMonth, mDay);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = null;
                try {
                    d = sdf.parse(txtStartDate.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDatePicker.getDatePicker().setMinDate(d.getTime());
                if (!mDatePicker.isShowing()) {
                    mDatePicker.show();
                }
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pRef = mDatabase.child("Pills").child(userid);
    }

    @Override
    public void onClick(View v) {
        String name = txtNamePill.getText().toString().trim();
        String pillfunc = txtPillFunc.getText().toString().trim();
        String interval = txtInterval.getSelectedItem().toString().trim();
        String pillhour = txtPillHour.getText().toString().trim();
        String pillstartdate = txtStartDate.getText().toString().trim();
        String pillenddate = txtEndDate.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pillfunc) && TextUtils.isEmpty(pillhour) && TextUtils.isEmpty(pillstartdate) && TextUtils.isEmpty(pillenddate)) {
            txtNamePill.setError("Nome não pode ficar vazio");
            txtPillFunc.setError("Função não pode ficar vazia");
            txtPillHour.setError("Hora não pode ficar vazia");
            txtStartDate.setError("Data início não pode ficar vazia");
            txtEndDate.setError("Data fim não pode ficar vazia");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            txtNamePill.setError("Nome não pode ficar vazio");
            return;
        }

        if (TextUtils.isEmpty(pillfunc)) {
            txtPillFunc.setError("Função não pode ficar vazia");
            return;
        }

        if (TextUtils.isEmpty(pillhour)) {
            txtPillHour.setError("Hora não pode ficar vazia");
            return;
        }

        if (TextUtils.isEmpty(pillstartdate)) {
            txtStartDate.setError("Data início não pode ficar vazia");
            return;
        }

        if (TextUtils.isEmpty(pillenddate)) {
            txtEndDate.setError("Data fim não pode ficar vazia");
            return;
        }

        PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pillhour, pillstartdate, pillenddate);
        pRef.child(name).setValue(PillInfo);
        Toast.makeText(AddPill.this, "Medicamento adicionado com sucesso!", Toast.LENGTH_SHORT).show();
        Intent Home = new Intent(AddPill.this, MainActivity.class);
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

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
