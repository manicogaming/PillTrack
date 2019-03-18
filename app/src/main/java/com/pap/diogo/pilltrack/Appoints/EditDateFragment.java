package com.pap.diogo.pilltrack.Appoints;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.R;

import java.util.Calendar;

public class EditDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatabaseReference pRef;
    private FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm + 1, dd);
    }

    public void populateSetDate(int year, int month, int day) {
        EditText AppointDate = getActivity().findViewById(R.id.EAppointDate);
        TextView AppointName = getActivity().findViewById(R.id.AppointName);

        mAuth = FirebaseAuth.getInstance();
        String userid = mAuth.getCurrentUser().getUid();
        pRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);

        AppointDate.setText(day + "/" + month + "/" + year);

        String name = AppointName.getText().toString().trim();
        String newdate = AppointDate.getText().toString().trim();

        pRef.child(name).child("date").setValue(newdate);
    }

}
