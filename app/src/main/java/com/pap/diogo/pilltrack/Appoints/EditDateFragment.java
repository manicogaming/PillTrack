package com.pap.diogo.pilltrack.Appoints;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pap.diogo.pilltrack.R;

import java.util.Calendar;

public class EditDateFragment extends DialogFragment {
    DatabaseReference aRef, eRef;
    private FirebaseAuth mAuth;
    private boolean isAppoint;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        final int mYear, mMonth, mDay;
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                if (selectedyear == mYear && (selectedmonth + 1) == mMonth + 1) {
                    if (selectedday < mDay) {
                        Toast.makeText(getContext(), "Data inválida", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (isAppoint) {
                    EditText AppointDate = getActivity().findViewById(R.id.EAppointDate);
                    TextView AppointName = getActivity().findViewById(R.id.AppointName);

                    mAuth = FirebaseAuth.getInstance();
                    String userid = mAuth.getCurrentUser().getUid();
                    aRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);

                    AppointDate.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);

                    String name = AppointName.getText().toString().trim();
                    String newdate = AppointDate.getText().toString().trim();

                    aRef.child(name).child("date").setValue(newdate);
                } else {

                    EditText ExamDate = getActivity().findViewById(R.id.EExamDate);
                    TextView ExamName = getActivity().findViewById(R.id.ExamName);

                    mAuth = FirebaseAuth.getInstance();
                    String userid = mAuth.getCurrentUser().getUid();
                    eRef = FirebaseDatabase.getInstance().getReference().child("Exams").child(userid);

                    ExamDate.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);

                    String name = ExamName.getText().toString().trim();
                    String newdate = ExamDate.getText().toString().trim();

                    eRef.child(name).child("date").setValue(newdate);
                }

            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        if (!mDatePicker.isShowing()) {
            mDatePicker.show();
        }
        isAppoint = getArguments().getBoolean("isAppoint");
        return mDatePicker;
    }

}
