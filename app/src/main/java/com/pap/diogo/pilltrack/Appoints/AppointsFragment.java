package com.pap.diogo.pilltrack.Appoints;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

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

public class AppointsFragment extends Fragment {
    private ImageButton add_appoint;
    private RecyclerView EAppoints, EExams;
    private FirebaseRecyclerAdapter<Appoint, EAppointsInfo> EAppointsAdapter;
    private FirebaseRecyclerAdapter<Exam, EExamsInfo> EExamsAdapter;
    private String HospitalLocation, HName, SName;
    private TextView NoEAppoints;

    private FirebaseAuth mAuth;
    DatabaseReference aRef, eRef;

    private boolean isAppoint = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_appoints, container, false);

        mAuth = FirebaseAuth.getInstance();

        String userid = mAuth.getCurrentUser().getUid();

        aRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);
        eRef = FirebaseDatabase.getInstance().getReference().child("Exams").child(userid);

        EAppoints = mMainView.findViewById(R.id.eappointslist);
        EExams = mMainView.findViewById(R.id.eexamslist);

        NoEAppoints = mMainView.findViewById(R.id.NoEAppoints);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        EAppoints.setLayoutManager(linearLayoutManager1);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        EExams.setLayoutManager(linearLayoutManager2);

        add_appoint = mMainView.findViewById(R.id.add_appoint);


        add_appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddAppoint = new Intent(getActivity(), AddAppoint.class);
                startActivity(AddAppoint);
            }
        });

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        ShowAppoints();
        ShowExams();
    }

    private void ShowAppoints() {
        FirebaseRecyclerOptions<Appoint> AppointQ = new FirebaseRecyclerOptions.Builder<Appoint>().setQuery(aRef, Appoint.class).setLifecycleOwner(this).build();

        EAppointsAdapter = new FirebaseRecyclerAdapter<Appoint, EAppointsInfo>(AppointQ) {

            @NonNull
            @Override
            public EAppointsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new EAppointsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.eappoints, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final EAppointsInfo holder, int position, @NonNull final Appoint model) {
                aRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NoEAppoints.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setDate(model.getDate());
                        holder.setHospital(model.getHospital());

                        holder.EAppointDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                aRef.child(model.getName()).removeValue();
                                ((MainActivity) getActivity()).setNavItem(2);
                            }
                        });

                        EditAppoints(holder, model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        if (EAppointsAdapter.getItemCount() == 0) {
            NoEAppoints.setVisibility(View.VISIBLE);
        }

        EAppoints.setAdapter(EAppointsAdapter);
    }

    private void ShowExams() {
        FirebaseRecyclerOptions<Exam> ExamQ = new FirebaseRecyclerOptions.Builder<Exam>().setQuery(eRef, Exam.class).setLifecycleOwner(this).build();

        EExamsAdapter = new FirebaseRecyclerAdapter<Exam, EExamsInfo>(ExamQ) {

            @NonNull
            @Override
            public EExamsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new EExamsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.eexams, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final EExamsInfo holder, int position, @NonNull final Exam model) {
                eRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NoEAppoints.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setDate(model.getDate());
                        holder.setHospital(model.getHospital());
                        holder.setPrep(model.getPrep());

                        holder.EExamDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eRef.child(model.getName()).removeValue();
                                ((MainActivity) getActivity()).setNavItem(2);
                            }
                        });

                        EditExams(holder, model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        if (EExamsAdapter.getItemCount() == 0) {
            NoEAppoints.setVisibility(View.VISIBLE);
        }

        EExams.setAdapter(EExamsAdapter);
    }

    private void EditAppoints(@NonNull final EAppointsInfo holder, @NonNull final Appoint model) {
        holder.AppointName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.AppointName.setVisibility(View.GONE);
                holder.EAppointName.setVisibility(View.VISIBLE);
                holder.EAppointName.requestFocus();

                final String currname = holder.AppointName.getText().toString();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Specialty");
                final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot specialty : dataSnapshot.getChildren()) {
                            SName = specialty.child("name").getValue(String.class);
                            autoComplete.add(SName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.EAppointName.setThreshold(1);
                holder.EAppointName.setAdapter(autoComplete);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EAppointName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String str = holder.EAppointName.getText().toString();

                            ListAdapter listAdapter = holder.EAppointName.getAdapter();
                            for (int i = 0; i < listAdapter.getCount(); i++) {
                                String temp = listAdapter.getItem(i).toString();
                                if (str.compareTo(temp) == 0) {
                                    holder.AppointName.setVisibility(View.VISIBLE);
                                    holder.EAppointName.setVisibility(View.GONE);

                                    final String newname = str.trim();

                                    aRef.child(model.getName()).removeValue();

                                    String date = holder.AppointDate.getText().toString().trim();
                                    String hospital = holder.AppointHospital.getText().toString().trim();

                                    getHospitalLocation(hospital);
                                    AppointInfo AppointInfo = new AppointInfo(newname, hospital, date, HospitalLocation);
                                    aRef.child(newname).setValue(AppointInfo);
                                }
                            }
                            holder.EAppointName.setText(currname);
                            holder.AppointName.setVisibility(View.VISIBLE);
                            holder.EAppointName.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        holder.AppointDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.AppointDate.setVisibility(View.GONE);
                holder.EAppointDate.setVisibility(View.VISIBLE);
                holder.EAppointDate.requestFocus();

                newInstance();

                holder.AppointDate.setVisibility(View.VISIBLE);
                holder.EAppointDate.setVisibility(View.GONE);
            }
        });

        holder.AppointHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.AppointHospital.setVisibility(View.GONE);
                holder.EAppointHospital.setVisibility(View.VISIBLE);
                holder.EAppointHospital.requestFocus();

                final String currhospital = holder.AppointHospital.getText().toString();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Hospitals");
                final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
                database.addValueEventListener(new ValueEventListener() {
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

                holder.EAppointHospital.setThreshold(1);
                holder.EAppointHospital.setAdapter(autoComplete);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EAppointHospital.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String str = holder.EAppointHospital.getText().toString();

                            ListAdapter listAdapter = holder.EAppointHospital.getAdapter();
                            for (int i = 0; i < listAdapter.getCount(); i++) {
                                String temp = listAdapter.getItem(i).toString();
                                if (str.compareTo(temp) == 0) {
                                    holder.AppointHospital.setVisibility(View.VISIBLE);
                                    holder.EAppointHospital.setVisibility(View.GONE);

                                    final String newhospital = str.trim();

                                    getHospitalLocation(newhospital);
                                    aRef.child(model.getName()).child("hospital").setValue(newhospital);
                                    aRef.child(model.getName()).child("hlocation").setValue(HospitalLocation);
                                }
                            }
                            holder.EAppointHospital.setText(currhospital);
                            holder.AppointHospital.setVisibility(View.VISIBLE);
                            holder.EAppointHospital.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    private void EditExams(@NonNull final EExamsInfo holder, @NonNull final Exam model) {
        holder.ExamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ExamName.setVisibility(View.GONE);
                holder.EExamName.setVisibility(View.VISIBLE);
                holder.EExamName.requestFocus();

                final String currname = holder.ExamName.getText().toString();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference("ExamType");
                final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot specialty : dataSnapshot.getChildren()) {
                            SName = specialty.child("name").getValue(String.class);
                            autoComplete.add(SName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.EExamName.setThreshold(1);
                holder.EExamName.setAdapter(autoComplete);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EExamName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String str = holder.EExamName.getText().toString();

                            ListAdapter listAdapter = holder.EExamName.getAdapter();
                            for (int i = 0; i < listAdapter.getCount(); i++) {
                                String temp = listAdapter.getItem(i).toString();
                                if (str.compareTo(temp) == 0) {
                                    holder.ExamName.setVisibility(View.VISIBLE);
                                    holder.EExamName.setVisibility(View.GONE);

                                    final String newname = str.trim();

                                    eRef.child(model.getName()).removeValue();

                                    String date = holder.ExamDate.getText().toString().trim();
                                    String hospital = holder.ExamHospital.getText().toString().trim();
                                    String prep = holder.ExamPrep.getText().toString().trim();

                                    getHospitalLocation(hospital);
                                    ExamInfo ExamInfo = new ExamInfo(newname, hospital, prep, date, HospitalLocation);
                                    eRef.child(newname).setValue(ExamInfo);
                                }
                            }
                            holder.EExamName.setText(currname);
                            holder.ExamName.setVisibility(View.VISIBLE);
                            holder.EExamName.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        holder.ExamDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ExamDate.setVisibility(View.GONE);
                holder.EExamDate.setVisibility(View.VISIBLE);
                holder.EExamDate.requestFocus();

                isAppoint = false;
                newInstance();

                holder.ExamDate.setVisibility(View.VISIBLE);
                holder.EExamDate.setVisibility(View.GONE);
            }
        });

        holder.ExamHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ExamHospital.setVisibility(View.GONE);
                holder.EExamHospital.setVisibility(View.VISIBLE);
                holder.EExamHospital.requestFocus();

                final String currhospital = holder.ExamHospital.getText().toString();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference("Hospitals");
                final ArrayAdapter<String> autoComplete = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
                database.addValueEventListener(new ValueEventListener() {
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

                holder.EExamHospital.setThreshold(1);
                holder.EExamHospital.setAdapter(autoComplete);

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EExamHospital.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String str = holder.EExamHospital.getText().toString();

                            ListAdapter listAdapter = holder.EExamHospital.getAdapter();
                            for (int i = 0; i < listAdapter.getCount(); i++) {
                                String temp = listAdapter.getItem(i).toString();
                                if (str.compareTo(temp) == 0) {
                                    holder.ExamHospital.setVisibility(View.VISIBLE);
                                    holder.EExamHospital.setVisibility(View.GONE);

                                    final String newhospital = str.trim();

                                    getHospitalLocation(newhospital);
                                    eRef.child(model.getName()).child("hospital").setValue(newhospital);
                                    eRef.child(model.getName()).child("hlocation").setValue(HospitalLocation);
                                }
                            }
                            holder.EExamHospital.setText(currhospital);
                            holder.ExamHospital.setVisibility(View.VISIBLE);
                            holder.EExamHospital.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        holder.ExamPrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ExamPrep.setVisibility(View.GONE);
                holder.EExamPrep.setVisibility(View.VISIBLE);
                holder.EExamPrep.requestFocus();

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EExamPrep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            String prepdays = holder.EExamPrep.getText().toString().trim();

                            eRef.child(model.getName()).child("prep").setValue(prepdays);

                            holder.ExamPrep.setVisibility(View.VISIBLE);
                            holder.EExamPrep.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public DialogFragment newInstance() {
        DialogFragment f = new EditDateFragment();
        f.show(getFragmentManager(), "DatePicker");

        Bundle args = new Bundle();
        args.putBoolean("isAppoint", isAppoint);
        f.setArguments(args);

        return f;
    }

    public static class EAppointsInfo extends RecyclerView.ViewHolder {
        View EAppointsL;
        ImageButton EAppointDelete;
        TextView AppointName, AppointDate, AppointHospital;
        EditText EAppointDate;
        AutoCompleteTextView EAppointName, EAppointHospital;

        public EAppointsInfo(@NonNull View itemView) {
            super(itemView);

            EAppointsL = itemView;
            EAppointDelete = EAppointsL.findViewById(R.id.EAppointDelete);
        }

        public void setName(String name) {
            AppointName = EAppointsL.findViewById(R.id.AppointName);
            EAppointName = EAppointsL.findViewById(R.id.EAppointName);
            AppointName.setText(name);
            EAppointName.setText(name);
        }

        public void setDate(String date) {
            AppointDate = EAppointsL.findViewById(R.id.AppointDate);
            EAppointDate = EAppointsL.findViewById(R.id.EAppointDate);
            AppointDate.setText(date);
            EAppointDate.setText(date);
        }

        public void setHospital(String hospital) {
            AppointHospital = EAppointsL.findViewById(R.id.AppointHospital);
            EAppointHospital = EAppointsL.findViewById(R.id.EAppointHospital);
            AppointHospital.setText(hospital);
            EAppointHospital.setText(hospital);
        }
    }

    public static class EExamsInfo extends RecyclerView.ViewHolder {
        View EExamsL;
        ImageButton EExamDelete;
        TextView ExamName, ExamDate, ExamHospital, ExamPrep;
        EditText EExamDate, EExamPrep;
        AutoCompleteTextView EExamHospital, EExamName;

        public EExamsInfo(@NonNull View itemView) {
            super(itemView);

            EExamsL = itemView;
            EExamDelete = EExamsL.findViewById(R.id.EExamDelete);
        }

        public void setName(String name) {
            ExamName = EExamsL.findViewById(R.id.ExamName);
            EExamName = EExamsL.findViewById(R.id.EExamName);
            ExamName.setText(name);
            EExamName.setText(name);
        }

        public void setDate(String date) {
            ExamDate = EExamsL.findViewById(R.id.ExamDate);
            EExamDate = EExamsL.findViewById(R.id.EExamDate);
            ExamDate.setText(date);
            EExamDate.setText(date);
        }

        public void setHospital(String hospital) {
            ExamHospital = EExamsL.findViewById(R.id.ExamHospital);
            EExamHospital = EExamsL.findViewById(R.id.EExamHospital);
            ExamHospital.setText(hospital);
            EExamHospital.setText(hospital);
        }

        public void setPrep(String prep) {
            ExamPrep = EExamsL.findViewById(R.id.ExamPrep);
            EExamPrep = EExamsL.findViewById(R.id.EExamPrep);
            ExamPrep.setText(prep + " Dias");
            EExamPrep.setText(prep);
        }
    }

    public void getHospitalLocation(String hospital) {
        if (hospital.matches("Baixo Vouga")) {
            HospitalLocation = "40.633787, -8.654963";
        }
        if (hospital.matches("Entre o Douro e Vouga")) {
            HospitalLocation = "40.930216, -8.547473";
        }
        if (hospital.matches("Francisco Zagalo")) {
            HospitalLocation = "40.857527, -8.631336";
        }
        if (hospital.matches("José Joaquim Fernandes")) {
            HospitalLocation = "38.014149, -7.869755";
        }
        if (hospital.matches("Escala Brage")) {
            HospitalLocation = "41.567980, -8.399012";
        }
        if (hospital.matches("Senhora Oliveira Guimarães")) {
            HospitalLocation = "41.441908, -8.305245";
        }
        if (hospital.matches("Santa Maria Maior")) {
            HospitalLocation = "41.533183, -8.616388";
        }
        if (hospital.matches("Nordeste")) {
            HospitalLocation = "41.802219, -6.768147";
        }
        if (hospital.matches("Cova da Beira")) {
            HospitalLocation = "40.266136, -7.492287";
        }
        if (hospital.matches("Castelo Branco")) {
            HospitalLocation = "39.822492, -7.499889";
        }
        if (hospital.matches("Coimbra")) {
            HospitalLocation = "40.220665, -8.412978";
        }
        if (hospital.matches("Figueira da Foz")) {
            HospitalLocation = "40.130862, -8.860094";
        }
        if (hospital.matches("Coimbra Francisco Gentil")) {
            HospitalLocation = "40.217128, -8.409814";
        }
        if (hospital.matches("Espírito Santo - Évora")) {
            HospitalLocation = "38.568572, -7.903106";
        }
        if (hospital.matches("Algarve")) {
            HospitalLocation = "37.024569, -7.928985";
        }
        if (hospital.matches("Guarda")) {
            HospitalLocation = "40.530903, -7.275523";
        }
        if (hospital.matches("Leiria")) {
            HospitalLocation = "39.743314, -8.794250";
        }
        if (hospital.matches("Oeste")) {
            HospitalLocation = "39.404620, -9.129661";
        }
        if (hospital.matches("Lisboa Central")) {
            HospitalLocation = "38.717123, -9.137085";
        }
        if (hospital.matches("Lisboa Norte")) {
            HospitalLocation = "38.765411, -9.159559";
        }
        if (hospital.matches("Lisboa Ocidental")) {
            HospitalLocation = "38.726995, -9.233875";
        }
        if (hospital.matches("Psiquiátrico de Lisboa")) {
            HospitalLocation = "38.757685, -9.146433";
        }
        if (hospital.matches("Vila Franca de Xira")) {
            HospitalLocation = "38.977198, -8.984925";
        }
        if (hospital.matches("Beatriz Ângelo")) {
            HospitalLocation = "38.821554, -9.176333";
        }
        if (hospital.matches("Cascais")) {
            HospitalLocation = "38.730010, -9.418145";
        }
        if (hospital.matches("Professor Fernando Fonseca")) {
            HospitalLocation = "38.743577, -9.245854";
        }
        if (hospital.matches("Lisboa Francisco Gentil")) {
            HospitalLocation = "38.739869, -9.161362";
        }
        if (hospital.matches("Norte Alentejano")) {
            HospitalLocation = "39.300215, -7.427454";
        }
        if (hospital.matches("São João")) {
            HospitalLocation = "41.181343, -8.600669";
        }
        if (hospital.matches("Eduardo Santos Silva")) {
            HospitalLocation = "41.106352, -8.592435";
        }
        if (hospital.matches("Médio Ave")) {
            HospitalLocation = "41.412913, -8.521811";
        }
        if (hospital.matches("Porto")) {
            HospitalLocation = "41.147228, -8.619534";
        }
        if (hospital.matches("Tâmega e Sousa")) {
            HospitalLocation = "41.197027, -8.309523";
        }
        if (hospital.matches("Vila do Conde")) {
            HospitalLocation = "41.382959, -8.758802";
        }
        if (hospital.matches("Magalhães Lemos")) {
            HospitalLocation = "41.177631, -8.663650";
        }
        if (hospital.matches("Porto Francisco Gentil")) {
            HospitalLocation = "41.182737, -8.604551";
        }
        if (hospital.matches("Pedro Hispano")) {
            HospitalLocation = "41.181819, -8.663393";
        }
        if (hospital.matches("Médio Tejo")) {
            HospitalLocation = "39.467919, -8.537029";
        }
        if (hospital.matches("Santarém")) {
            HospitalLocation = "39.241077, -8.696647";
        }
        if (hospital.matches("Barreiro Montijo")) {
            HospitalLocation = "38.654673, -9.058227";
        }
        if (hospital.matches("Setúbal")) {
            HospitalLocation = "38.529196, -8.881083";
        }
        if (hospital.matches("Garcia de Orta")) {
            HospitalLocation = "38.674072, -9.176839";
        }
        if (hospital.matches("Litoral Alentejano")) {
            HospitalLocation = "38.040003, -8.732500";
        }
        if (hospital.matches("Alto Minho")) {
            HospitalLocation = "41.697339, -8.832486";
        }
        if (hospital.matches("Trás-os-montes e Alto Douro")) {
            HospitalLocation = "41.310163, -7.760095";
        }
        if (hospital.matches("Tondela-Viseu")) {
            HospitalLocation = "40.650466, -7.905616";
        }
    }
}
