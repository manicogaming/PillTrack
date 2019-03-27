package com.pap.diogo.pilltrack.Pills;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class AddPill extends AppCompatActivity implements View.OnClickListener {
    private EditText txtNamePill, txtPillFunc;
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

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(pillfunc)) {
            Toast.makeText(this, "Os campos não podem ficar vazios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nome não pode ficar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pillfunc)) {
            Toast.makeText(this, "Função não pode ficar vazia", Toast.LENGTH_SHORT).show();
            return;
        }

        PillInfo PillInfo = new PillInfo(name, pillfunc, interval);
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
}
