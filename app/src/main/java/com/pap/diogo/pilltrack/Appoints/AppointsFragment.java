package com.pap.diogo.pilltrack.Appoints;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pap.diogo.pilltrack.MainActivity;
import com.pap.diogo.pilltrack.R;

public class AppointsFragment extends Fragment {
    private ImageButton add_appoint;
    private RecyclerView EAppoints;
    private FirebaseRecyclerAdapter<Appoint, EAppointsInfo> EAppointsAdapter;
    private String HospitalLocation, HName;
    private TextView NoEAppoints;

    private FirebaseAuth mAuth;
    DatabaseReference pRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_appoints, container, false);

        mAuth = FirebaseAuth.getInstance();

        String userid = mAuth.getCurrentUser().getUid();

        pRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);

        EAppoints = mMainView.findViewById(R.id.eappointslist);

        NoEAppoints = mMainView.findViewById(R.id.NoEAppoints);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        EAppoints.setLayoutManager(linearLayoutManager);

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

        FirebaseRecyclerOptions<Appoint> AppointQ = new FirebaseRecyclerOptions.Builder<Appoint>().setQuery(pRef, Appoint.class).setLifecycleOwner(this).build();

        EAppointsAdapter = new FirebaseRecyclerAdapter<Appoint, EAppointsInfo>(AppointQ) {

            @NonNull
            @Override
            public EAppointsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new EAppointsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.eappoints, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final EAppointsInfo holder, int position, @NonNull final Appoint model) {
                pRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NoEAppoints.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setDate(model.getDate());
                        holder.setHospital(model.getHospital());

                        holder.EAppointDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pRef.child(model.getName()).removeValue();
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

    private void EditAppoints(@NonNull final EAppointsInfo holder, @NonNull final Appoint model) {
        holder.AppointName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.AppointName.setVisibility(View.GONE);
                holder.EAppointName.setVisibility(View.VISIBLE);
                holder.EAppointName.requestFocus();

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EAppointName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            holder.AppointName.setVisibility(View.VISIBLE);
                            holder.EAppointName.setVisibility(View.GONE);

                            pRef.child(model.getName()).removeValue();

                            String newname = holder.EAppointName.getText().toString().trim();
                            String date = holder.AppointDate.getText().toString().trim();
                            String hospital = holder.AppointHospital.getText().toString().trim();

                            getHospitalLocation(holder);
                            AppointInfo AppointInfo = new AppointInfo(newname, hospital, date, HospitalLocation);
                            pRef.child(newname).setValue(AppointInfo);
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

                DialogFragment newFragment = new EditDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");

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

                                    getHospitalLocation(holder);
                                    pRef.child(model.getName()).child("hospital").setValue(newhospital);
                                    pRef.child(model.getName()).child("hlocation").setValue(HospitalLocation);
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

    public static class EAppointsInfo extends RecyclerView.ViewHolder {
        View EAppointsL;
        ImageButton EAppointDelete;
        TextView AppointName, AppointDate, AppointHospital;
        EditText EAppointName, EAppointDate;
        AutoCompleteTextView EAppointHospital;

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

    public void getHospitalLocation(@NonNull final EAppointsInfo holder) {
        if (holder.EAppointHospital.getText().toString().matches("Baixo Vouga")) {
            HospitalLocation = "40.633787, -8.654963";
        }
        if (holder.EAppointHospital.getText().toString().matches("Entre o Douro e Vouga")) {
            HospitalLocation = "40.930216, -8.547473";
        }
        if (holder.EAppointHospital.getText().toString().matches("Francisco Zagalo")) {
            HospitalLocation = "40.857527, -8.631336";
        }
        if (holder.EAppointHospital.getText().toString().matches("José Joaquim Fernandes")) {
            HospitalLocation = "38.014149, -7.869755";
        }
        if (holder.EAppointHospital.getText().toString().matches("Escala Brage")) {
            HospitalLocation = "41.567980, -8.399012";
        }
        if (holder.EAppointHospital.getText().toString().matches("Senhora Oliveira Guimarães")) {
            HospitalLocation = "41.441908, -8.305245";
        }
        if (holder.EAppointHospital.getText().toString().matches("Santa Maria Maior")) {
            HospitalLocation = "41.533183, -8.616388";
        }
        if (holder.EAppointHospital.getText().toString().matches("Nordeste")) {
            HospitalLocation = "41.802219, -6.768147";
        }
        if (holder.EAppointHospital.getText().toString().matches("Cova da Beira")) {
            HospitalLocation = "40.266136, -7.492287";
        }
        if (holder.EAppointHospital.getText().toString().matches("Castelo Branco")) {
            HospitalLocation = "39.822492, -7.499889";
        }
        if (holder.EAppointHospital.getText().toString().matches("Coimbra")) {
            HospitalLocation = "40.220665, -8.412978";
        }
        if (holder.EAppointHospital.getText().toString().matches("Figueira da Foz")) {
            HospitalLocation = "40.130862, -8.860094";
        }
        if (holder.EAppointHospital.getText().toString().matches("Coimbra Francisco Gentil")) {
            HospitalLocation = "40.217128, -8.409814";
        }
        if (holder.EAppointHospital.getText().toString().matches("Espírito Santo - Évora")) {
            HospitalLocation = "38.568572, -7.903106";
        }
        if (holder.EAppointHospital.getText().toString().matches("Algarve")) {
            HospitalLocation = "37.024569, -7.928985";
        }
        if (holder.EAppointHospital.getText().toString().matches("Guarda")) {
            HospitalLocation = "40.530903, -7.275523";
        }
        if (holder.EAppointHospital.getText().toString().matches("Leiria")) {
            HospitalLocation = "39.743314, -8.794250";
        }
        if (holder.EAppointHospital.getText().toString().matches("Oeste")) {
            HospitalLocation = "39.404620, -9.129661";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Central")) {
            HospitalLocation = "38.717123, -9.137085";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Norte")) {
            HospitalLocation = "38.765411, -9.159559";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Ocidental")) {
            HospitalLocation = "38.726995, -9.233875";
        }
        if (holder.EAppointHospital.getText().toString().matches("Psiquiátrico de Lisboa")) {
            HospitalLocation = "38.757685, -9.146433";
        }
        if (holder.EAppointHospital.getText().toString().matches("Vila Franca de Xira")) {
            HospitalLocation = "38.977198, -8.984925";
        }
        if (holder.EAppointHospital.getText().toString().matches("Beatriz Ângelo")) {
            HospitalLocation = "38.821554, -9.176333";
        }
        if (holder.EAppointHospital.getText().toString().matches("Cascais")) {
            HospitalLocation = "38.730010, -9.418145";
        }
        if (holder.EAppointHospital.getText().toString().matches("Professor Fernando Fonseca")) {
            HospitalLocation = "38.743577, -9.245854";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Francisco Gentil")) {
            HospitalLocation = "38.739869, -9.161362";
        }
        if (holder.EAppointHospital.getText().toString().matches("Norte Alentejano")) {
            HospitalLocation = "39.300215, -7.427454";
        }
        if (holder.EAppointHospital.getText().toString().matches("São João")) {
            HospitalLocation = "41.181343, -8.600669";
        }
        if (holder.EAppointHospital.getText().toString().matches("Eduardo Santos Silva")) {
            HospitalLocation = "41.106352, -8.592435";
        }
        if (holder.EAppointHospital.getText().toString().matches("Médio Ave")) {
            HospitalLocation = "41.412913, -8.521811";
        }
        if (holder.EAppointHospital.getText().toString().matches("Porto")) {
            HospitalLocation = "41.147228, -8.619534";
        }
        if (holder.EAppointHospital.getText().toString().matches("Tâmega e Sousa")) {
            HospitalLocation = "41.197027, -8.309523";
        }
        if (holder.EAppointHospital.getText().toString().matches("Vila do Conde")) {
            HospitalLocation = "41.382959, -8.758802";
        }
        if (holder.EAppointHospital.getText().toString().matches("Magalhães Lemos")) {
            HospitalLocation = "41.177631, -8.663650";
        }
        if (holder.EAppointHospital.getText().toString().matches("Porto Francisco Gentil")) {
            HospitalLocation = "41.182737, -8.604551";
        }
        if (holder.EAppointHospital.getText().toString().matches("Pedro Hispano")) {
            HospitalLocation = "41.181819, -8.663393";
        }
        if (holder.EAppointHospital.getText().toString().matches("Médio Tejo")) {
            HospitalLocation = "39.467919, -8.537029";
        }
        if (holder.EAppointHospital.getText().toString().matches("Santarém")) {
            HospitalLocation = "39.241077, -8.696647";
        }
        if (holder.EAppointHospital.getText().toString().matches("Barreiro Montijo")) {
            HospitalLocation = "38.654673, -9.058227";
        }
        if (holder.EAppointHospital.getText().toString().matches("Setúbal")) {
            HospitalLocation = "38.529196, -8.881083";
        }
        if (holder.EAppointHospital.getText().toString().matches("Garcia de Orta")) {
            HospitalLocation = "38.674072, -9.176839";
        }
        if (holder.EAppointHospital.getText().toString().matches("Litoral Alentejano")) {
            HospitalLocation = "38.040003, -8.732500";
        }
        if (holder.EAppointHospital.getText().toString().matches("Alto Minho")) {
            HospitalLocation = "41.697339, -8.832486";
        }
        if (holder.EAppointHospital.getText().toString().matches("Trás-os-montes e Alto Douro")) {
            HospitalLocation = "41.310163, -7.760095";
        }
        if (holder.EAppointHospital.getText().toString().matches("Tondela-Viseu")) {
            HospitalLocation = "40.650466, -7.905616";
        }
    }
}
