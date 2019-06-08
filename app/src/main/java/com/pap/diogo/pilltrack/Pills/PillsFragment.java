package com.pap.diogo.pilltrack.Pills;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PillsFragment extends Fragment {
    private ImageButton add_pill;
    private RecyclerView EPills;
    private FirebaseRecyclerAdapter<Pill, EPillsInfo> EPillsAdapter;
    private TextView NoEPills;
    private Date d;
    private boolean isSpinnerInitial = true;

    private FirebaseAuth mAuth;
    DatabaseReference pRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_pills, container, false);

        mAuth = FirebaseAuth.getInstance();

        String userid = mAuth.getCurrentUser().getUid();

        pRef = FirebaseDatabase.getInstance().getReference().child("Pills").child(userid);

        EPills = mMainView.findViewById(R.id.epillslist);

        NoEPills = mMainView.findViewById(R.id.NoEPills);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        EPills.setLayoutManager(linearLayoutManager);

        add_pill = mMainView.findViewById(R.id.add_pill);


        add_pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddPill = new Intent(getActivity(), AddPill.class);
                startActivity(AddPill);
            }
        });

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Pill> PillQ = new FirebaseRecyclerOptions.Builder<Pill>().setQuery(pRef, Pill.class).setLifecycleOwner(this).build();

        EPillsAdapter = new FirebaseRecyclerAdapter<Pill, EPillsInfo>(PillQ) {

            @NonNull
            @Override
            public EPillsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new EPillsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.epills, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final EPillsInfo holder, int position, @NonNull final Pill model) {
                pRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NoEPills.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setPillFunc(model.getPillfunc());
                        holder.setInterval(model.getInterval());
                        holder.setPillType(model.getPilltype());
                        holder.setStartDate(model.getPillstartdate());
                        holder.setEndDate(model.getPillenddate());

                        holder.EPillDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pRef.child(model.getName()).removeValue();
                                ((MainActivity) getActivity()).setNavItem(1);
                            }
                        });

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        d = null;
                        try {
                            d = sdf.parse(holder.PillStartDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        EditPills(holder, model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };

        if (EPillsAdapter.getItemCount() == 0) {
            NoEPills.setVisibility(View.VISIBLE);
        }

        EPills.setAdapter(EPillsAdapter);
    }

    private void EditPills(@NonNull final EPillsInfo holder, @NonNull final Pill model) {
        holder.PillName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.PillName.setVisibility(View.GONE);
                holder.EPillName.setVisibility(View.VISIBLE);
                holder.EPillName.requestFocus();
                holder.ConfirmChanges.setVisibility(View.VISIBLE);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.ConfirmChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.PillName.setVisibility(View.VISIBLE);
                        holder.EPillName.setVisibility(View.GONE);
                        holder.PillFunc.setVisibility(View.VISIBLE);
                        holder.EPillFunc.setVisibility(View.GONE);
                        holder.PillInterval.setVisibility(View.VISIBLE);
                        holder.EPillInterval.setVisibility(View.GONE);
                        holder.PillType.setVisibility(View.VISIBLE);
                        holder.EPillType.setVisibility(View.GONE);

                        pRef.child(model.getName()).removeValue();

                        String name = holder.EPillName.getText().toString().trim();
                        String pillfunc = holder.EPillFunc.getText().toString().trim();
                        String pillstartdate = holder.PillStartDate.getText().toString().trim();
                        String pillenddate = holder.PillEndDate.getText().toString().trim();
                        String interval;
                        String pilltype;

                        if (holder.EPillInterval.getSelectedItem() == null) {
                            interval = holder.PillInterval.getText().toString().trim();
                        } else {
                            interval = holder.EPillInterval.getSelectedItem().toString().trim();
                        }

                        if (holder.EPillType.getSelectedItem() == null) {
                            pilltype = holder.PillType.getText().toString().trim();
                        } else {
                            pilltype = holder.EPillType.getSelectedItem().toString().trim();
                        }

                        PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pilltype, model.getPillhour(), pillstartdate, pillenddate);
                        pRef.child(name).setValue(PillInfo);
                        holder.ConfirmChanges.setVisibility(View.GONE);
                    }
                });
            }
        });

        holder.PillFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.PillFunc.setVisibility(View.GONE);
                holder.EPillFunc.setVisibility(View.VISIBLE);
                holder.EPillFunc.requestFocus();
                holder.ConfirmChanges.setVisibility(View.VISIBLE);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.ConfirmChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.PillName.setVisibility(View.VISIBLE);
                        holder.EPillName.setVisibility(View.GONE);
                        holder.PillFunc.setVisibility(View.VISIBLE);
                        holder.EPillFunc.setVisibility(View.GONE);
                        holder.PillInterval.setVisibility(View.VISIBLE);
                        holder.EPillInterval.setVisibility(View.GONE);
                        holder.PillType.setVisibility(View.VISIBLE);
                        holder.EPillType.setVisibility(View.GONE);

                        pRef.child(model.getName()).removeValue();

                        String name = holder.EPillName.getText().toString().trim();
                        String pillfunc = holder.EPillFunc.getText().toString().trim();
                        String pillstartdate = holder.PillStartDate.getText().toString().trim();
                        String pillenddate = holder.PillEndDate.getText().toString().trim();
                        String interval;
                        String pilltype;

                        if (holder.EPillInterval.getSelectedItem() == null) {
                            interval = holder.PillInterval.getText().toString().trim();
                        } else {
                            interval = holder.EPillInterval.getSelectedItem().toString().trim();
                        }

                        if (holder.EPillType.getSelectedItem() == null) {
                            pilltype = holder.PillType.getText().toString().trim();
                        } else {
                            pilltype = holder.EPillType.getSelectedItem().toString().trim();
                        }

                        PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pilltype, model.getPillhour(), pillstartdate, pillenddate);
                        pRef.child(name).setValue(PillInfo);
                        holder.ConfirmChanges.setVisibility(View.GONE);
                    }
                });
            }
        });

        holder.PillInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.PillInterval.setVisibility(View.GONE);
                holder.EPillInterval.setVisibility(View.VISIBLE);
                holder.ConfirmChanges.setVisibility(View.VISIBLE);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_edit, holder.arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.EPillInterval.setAdapter(adapter);

                holder.EPillInterval.setSelection(getIndex(holder.EPillInterval, model.getInterval()));

                holder.ConfirmChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.PillName.setVisibility(View.VISIBLE);
                        holder.EPillName.setVisibility(View.GONE);
                        holder.PillFunc.setVisibility(View.VISIBLE);
                        holder.EPillFunc.setVisibility(View.GONE);
                        holder.PillInterval.setVisibility(View.VISIBLE);
                        holder.EPillInterval.setVisibility(View.GONE);
                        holder.PillType.setVisibility(View.VISIBLE);
                        holder.EPillType.setVisibility(View.GONE);

                        pRef.child(model.getName()).removeValue();

                        String name = holder.EPillName.getText().toString().trim();
                        String pillfunc = holder.EPillFunc.getText().toString().trim();
                        String pillstartdate = holder.PillStartDate.getText().toString().trim();
                        String pillenddate = holder.PillEndDate.getText().toString().trim();
                        String interval;
                        String pilltype;

                        if (holder.EPillInterval.getSelectedItem() == null) {
                            interval = holder.PillInterval.getText().toString().trim();
                        } else {
                            interval = holder.EPillInterval.getSelectedItem().toString().trim();
                        }

                        if (holder.EPillType.getSelectedItem() == null) {
                            pilltype = holder.PillType.getText().toString().trim();
                        } else {
                            pilltype = holder.EPillType.getSelectedItem().toString().trim();
                        }

                        PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pilltype, model.getPillhour(), pillstartdate, pillenddate);
                        pRef.child(name).setValue(PillInfo);
                        holder.ConfirmChanges.setVisibility(View.GONE);
                    }
                });
            }
        });

        holder.PillType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.PillType.setVisibility(View.GONE);
                holder.EPillType.setVisibility(View.VISIBLE);
                holder.ConfirmChanges.setVisibility(View.VISIBLE);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_edit, holder.arraySpinner1);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.EPillType.setAdapter(adapter);

                holder.EPillType.setSelection(getIndex(holder.EPillType, model.getPilltype()));

                holder.ConfirmChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.PillName.setVisibility(View.VISIBLE);
                        holder.EPillName.setVisibility(View.GONE);
                        holder.PillFunc.setVisibility(View.VISIBLE);
                        holder.EPillFunc.setVisibility(View.GONE);
                        holder.PillInterval.setVisibility(View.VISIBLE);
                        holder.EPillInterval.setVisibility(View.GONE);
                        holder.PillType.setVisibility(View.VISIBLE);
                        holder.EPillType.setVisibility(View.GONE);

                        pRef.child(model.getName()).removeValue();

                        String name = holder.EPillName.getText().toString().trim();
                        String pillfunc = holder.EPillFunc.getText().toString().trim();
                        String pillstartdate = holder.PillStartDate.getText().toString().trim();
                        String pillenddate = holder.PillEndDate.getText().toString().trim();
                        String interval;
                        String pilltype;

                        if (holder.EPillInterval.getSelectedItem() == null) {
                            interval = holder.PillInterval.getText().toString().trim();
                        } else {
                            interval = holder.EPillInterval.getSelectedItem().toString().trim();
                        }

                        if (holder.EPillType.getSelectedItem() == null) {
                            pilltype = holder.PillType.getText().toString().trim();
                        } else {
                            pilltype = holder.EPillType.getSelectedItem().toString().trim();
                        }

                        PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pilltype, model.getPillhour(), pillstartdate, pillenddate);
                        pRef.child(name).setValue(PillInfo);
                        holder.ConfirmChanges.setVisibility(View.GONE);
                    }
                });
            }
        });

        holder.PillStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ConfirmChanges.setVisibility(View.VISIBLE);

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
                        holder.PillStartDate.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        d = null;
                        try {
                            d = sdf.parse(holder.PillStartDate.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        holder.ConfirmChanges.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.PillName.setVisibility(View.VISIBLE);
                                holder.EPillName.setVisibility(View.GONE);
                                holder.PillFunc.setVisibility(View.VISIBLE);
                                holder.EPillFunc.setVisibility(View.GONE);
                                holder.PillInterval.setVisibility(View.VISIBLE);
                                holder.EPillInterval.setVisibility(View.GONE);
                                holder.PillType.setVisibility(View.VISIBLE);
                                holder.EPillType.setVisibility(View.GONE);

                                pRef.child(model.getName()).removeValue();

                                String name = holder.EPillName.getText().toString().trim();
                                String pillfunc = holder.EPillFunc.getText().toString().trim();
                                String pillstartdate = holder.PillStartDate.getText().toString().trim();
                                String pillenddate = holder.PillEndDate.getText().toString().trim();
                                String interval;
                                String pilltype;

                                if (holder.EPillInterval.getSelectedItem() == null) {
                                    interval = holder.PillInterval.getText().toString().trim();
                                } else {
                                    interval = holder.EPillInterval.getSelectedItem().toString().trim();
                                }

                                if (holder.EPillType.getSelectedItem() == null) {
                                    pilltype = holder.PillType.getText().toString().trim();
                                } else {
                                    pilltype = holder.EPillType.getSelectedItem().toString().trim();
                                }

                                PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pilltype, model.getPillhour(), pillstartdate, pillenddate);
                                pRef.child(name).setValue(PillInfo);

                                holder.ConfirmChanges.setVisibility(View.GONE);
                            }
                        });
                    }
                }, mYear, mMonth, mDay);

                mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if ((holder.EPillName.getVisibility() == View.VISIBLE) || (holder.EPillFunc.getVisibility() == View.VISIBLE) || (!model.getPillenddate().matches(holder.PillEndDate.getText().toString()))) {
                            holder.ConfirmChanges.setVisibility(View.VISIBLE);
                        } else {
                            holder.ConfirmChanges.setVisibility(View.GONE);
                        }
                    }
                });

                mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                if (!mDatePicker.isShowing()) {
                    mDatePicker.show();
                }
            }
        });

        holder.PillEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ConfirmChanges.setVisibility(View.VISIBLE);

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
                        holder.PillEndDate.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);

                        holder.ConfirmChanges.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.PillName.setVisibility(View.VISIBLE);
                                holder.EPillName.setVisibility(View.GONE);
                                holder.PillFunc.setVisibility(View.VISIBLE);
                                holder.EPillFunc.setVisibility(View.GONE);
                                holder.PillInterval.setVisibility(View.VISIBLE);
                                holder.EPillInterval.setVisibility(View.GONE);
                                holder.PillType.setVisibility(View.VISIBLE);
                                holder.EPillType.setVisibility(View.GONE);

                                pRef.child(model.getName()).removeValue();

                                String name = holder.EPillName.getText().toString().trim();
                                String pillfunc = holder.EPillFunc.getText().toString().trim();
                                String pillstartdate = holder.PillStartDate.getText().toString().trim();
                                String pillenddate = holder.PillEndDate.getText().toString().trim();
                                String interval;
                                String pilltype;

                                if (holder.EPillInterval.getSelectedItem() == null) {
                                    interval = holder.PillInterval.getText().toString().trim();
                                } else {
                                    interval = holder.EPillInterval.getSelectedItem().toString().trim();
                                }

                                if (holder.EPillType.getSelectedItem() == null) {
                                    pilltype = holder.PillType.getText().toString().trim();
                                } else {
                                    pilltype = holder.EPillType.getSelectedItem().toString().trim();
                                }

                                PillInfo PillInfo = new PillInfo(name, pillfunc, interval, pilltype, model.getPillhour(), pillstartdate, pillenddate);
                                pRef.child(name).setValue(PillInfo);
                                holder.ConfirmChanges.setVisibility(View.GONE);
                            }
                        });
                    }
                }, mYear, mMonth, mDay);

                mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if ((holder.EPillName.getVisibility() == View.VISIBLE) || (holder.EPillFunc.getVisibility() == View.VISIBLE) || (!model.getPillstartdate().matches(holder.PillStartDate.getText().toString()))) {
                            holder.ConfirmChanges.setVisibility(View.VISIBLE);
                        } else {
                            holder.ConfirmChanges.setVisibility(View.GONE);
                        }
                    }
                });

                mDatePicker.getDatePicker().setMinDate(d.getTime());
                if (!mDatePicker.isShowing()) {
                    mDatePicker.show();
                }
            }
        });
    }

    private int getIndex(Spinner s1, String dbInterval) {

        int index = 0;

        for (int i = 0; i < s1.getCount(); i++) {
            if (s1.getItemAtPosition(i).equals(dbInterval)) {
                index = i;
            }
        }
        return index;
    }

    public static class EPillsInfo extends RecyclerView.ViewHolder {
        View EPillsL;
        ImageButton EPillDelete;
        TextView PillName, PillFunc, PillInterval, PillType, PillStartDate, PillEndDate;
        EditText EPillName, EPillFunc;
        Spinner EPillInterval, EPillType;
        Button ConfirmChanges;

        String[] arraySpinner = new String[]{
                "4 em 4 horas", "8 em 8 horas", "12 em 12 horas", "De manhã e à noite", "De manhã", "À noite"
        };

        String[] arraySpinner1 = new String[]{
                "Comprimido", "Cápsula", "Drágea", "Pó", "Granulado", "Solução", "Gotas", "Xarope", "Suspensão", "Elixir"
        };

        public EPillsInfo(@NonNull View itemView) {
            super(itemView);

            EPillsL = itemView;
            EPillDelete = EPillsL.findViewById(R.id.EPillDelete);
            EPillInterval = EPillsL.findViewById(R.id.EPillInterval);
            ConfirmChanges = EPillsL.findViewById(R.id.ConfirmChanges);
        }

        public void setName(String name) {
            PillName = EPillsL.findViewById(R.id.PillName);
            EPillName = EPillsL.findViewById(R.id.EPillName);
            PillName.setText(name);
            EPillName.setText(name);
        }

        public void setPillFunc(String pillfunc) {
            PillFunc = EPillsL.findViewById(R.id.PillFunc);
            EPillFunc = EPillsL.findViewById(R.id.EPillFunc);
            PillFunc.setText(pillfunc);
            EPillFunc.setText(pillfunc);
        }

        public void setInterval(String interval) {
            PillInterval = EPillsL.findViewById(R.id.PillInterval);
            EPillInterval = EPillsL.findViewById(R.id.EPillInterval);
            PillInterval.setText(interval);
        }

        public void setPillType(String pilltype) {
            PillType = EPillsL.findViewById(R.id.PillType);
            EPillType = EPillsL.findViewById(R.id.EPillType);
            PillType.setText(pilltype);
        }

        public void setStartDate(String pillstartdate) {
            PillStartDate = EPillsL.findViewById(R.id.PillStartDate);
            PillStartDate.setText(pillstartdate);
        }

        public void setEndDate(String pillenddate) {
            PillEndDate = EPillsL.findViewById(R.id.PillEndDate);
            PillEndDate.setText(pillenddate);
        }
    }
}
