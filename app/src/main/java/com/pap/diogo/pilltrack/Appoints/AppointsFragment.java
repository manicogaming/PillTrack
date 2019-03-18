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
import com.pap.diogo.pilltrack.R;

import java.util.ArrayList;

public class AppointsFragment extends Fragment {
    private ImageButton add_appoint;
    private RecyclerView EAppoints;
    private FirebaseRecyclerAdapter<Appoint, EAppointsInfo> EAppointsAdapter;
    private String HospitalLocation;

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
                        holder.setName(model.getName());
                        holder.setDate(model.getDate());
                        holder.setHospital(model.getHospital());

                        holder.EAppointDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pRef.child(model.getName()).removeValue();
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

                String[] hospitals = {"Baixo Vouga", "Entre o Douro e Vouga", "Dr. Francisco Zagalo", "José Joaquim Fernandes", "Escala Braga", "Senhora Oliveira Guimarães", "Santa Maria Maior",
                        "Nordeste", "Cova da Beira", "Castelo Branco", "Coimbra", "Figueira da Foz", "Coimbra Francisco Gentil", "Espírito Santo - Évora", "Algarve", "Guarda", "Leiria",
                        "Oeste", "Lisboa Central", "Lisboa Norte", "Lisboa Ocidental", "Psiquiátrico de Lisboa", "Vila Franca de Xira", "Beatriz Ângelo", "Cascais",
                        "Prof. Dr. Fernando Fonseca", "Lisboa Francisco Gentil", "Norte Alentejano", "São João", "Eduardo Santos Silva", "Médio Ave", "Porto", "Tâmega e Sousa", "Vila do Conde",
                        "Magalhães Lemos", "Porto Francisco Gentil", "Pedro Hispano", "Médio Tejo", "Santarém", "Barreiro Montijo", "Setúbal", "Garcia de Orta", "Litoral Alentejano",
                        "Alto Minho", "Trás-os-montes e Alto Douro", "Tondela-Viseu"};

                ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (getActivity(), android.R.layout.select_dialog_item, hospitals);

                holder.EAppointHospital.setThreshold(1);
                holder.EAppointHospital.setAdapter(adapter);

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

                                    String newhospital = str.trim();

                                    pRef.child(model.getName()).child("hospital").setValue(newhospital);
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
            HospitalLocation = "40.6336036,-8.6572487";
        }
        if (holder.EAppointHospital.getText().toString().matches("Entre o Douro e Vouga")) {
            HospitalLocation = "40.9302165,-8.5496593";
        }
        if (holder.EAppointHospital.getText().toString().matches("Dr. Francisco Zagalo")) {
            HospitalLocation = "40.857524,-8.6335287";
        }
        if (holder.EAppointHospital.getText().toString().matches("José Joaquim Fernandes")) {
            HospitalLocation = "38.0141498,-7.8719438";
        }
        if (holder.EAppointHospital.getText().toString().matches("Escala Brage")) {
            HospitalLocation = "41.5679778,-8.4012003";
        }
        if (holder.EAppointHospital.getText().toString().matches("Senhora Oliveira Guimarães")) {
            HospitalLocation = "41.44191,-8.3074347";
        }
        if (holder.EAppointHospital.getText().toString().matches("Santa Maria Maior")) {
            HospitalLocation = "41.533184,-8.6185787";
        }
        if (holder.EAppointHospital.getText().toString().matches("Nordeste")) {
            HospitalLocation = "41.8019098,-6.7688308";
        }
        if (holder.EAppointHospital.getText().toString().matches("Cova da Beira")) {
            HospitalLocation = "40.2662442,-7.4955216";
        }
        if (holder.EAppointHospital.getText().toString().matches("Castelo Branco")) {
            HospitalLocation = "39.821828,-7.500954";
        }
        if (holder.EAppointHospital.getText().toString().matches("Coimbra")) {
            HospitalLocation = "40.220667,-8.4151661";
        }
        if (holder.EAppointHospital.getText().toString().matches("Figueira da Foz")) {
            HospitalLocation = "40.130863,-8.8622816";
        }
        if (holder.EAppointHospital.getText().toString().matches("Coimbra Francisco Gentil")) {
            HospitalLocation = "40.2171298,-8.4120016";
        }
        if (holder.EAppointHospital.getText().toString().matches("Espírito Santo - Évora")) {
            HospitalLocation = "38.5685733,-7.9052961";
        }
        if (holder.EAppointHospital.getText().toString().matches("Algarve")) {
            HospitalLocation = "37.0245733,-7.931173";
        }
        if (holder.EAppointHospital.getText().toString().matches("Guarda")) {
            HospitalLocation = "40.5309052,-7.2777112";
        }
        if (holder.EAppointHospital.getText().toString().matches("Leiria")) {
            HospitalLocation = "39.7433166,-8.7964385";
        }
        if (holder.EAppointHospital.getText().toString().matches("Oeste")) {
            HospitalLocation = "39.4046223,-9.1318501";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Central")) {
            HospitalLocation = "38.7170859,-9.1379516";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Norte")) {
            HospitalLocation = "38.765411,-9.1617467";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Ocidental")) {
            HospitalLocation = "38.7654768,-9.1945774";
        }
        if (holder.EAppointHospital.getText().toString().matches("Psiquiátrico de Lisboa")) {
            HospitalLocation = "38.7576872,-9.14862";
        }
        if (holder.EAppointHospital.getText().toString().matches("Vila Franca de Xira")) {
            HospitalLocation = "38.9771976,-8.9871135";
        }
        if (holder.EAppointHospital.getText().toString().matches("Beatriz Ângelo")) {
            HospitalLocation = "38.8215556,-9.1785221";
        }
        if (holder.EAppointHospital.getText().toString().matches("Cascais")) {
            HospitalLocation = "38.7300133,-9.4203311";
        }
        if (holder.EAppointHospital.getText().toString().matches("Prof. Dr. Fernando Fonseca")) {
            HospitalLocation = "38.7435637,-9.2480437";
        }
        if (holder.EAppointHospital.getText().toString().matches("Lisboa Francisco Gentil")) {
            HospitalLocation = "38.7398702,-9.1635497";
        }
        if (holder.EAppointHospital.getText().toString().matches("Norte Alentejano")) {
            HospitalLocation = "39.3002158,-7.4296425";
        }
        if (holder.EAppointHospital.getText().toString().matches("São João")) {
            HospitalLocation = "41.1814421,-8.6032293";
        }
        if (holder.EAppointHospital.getText().toString().matches("Eduardo Santos Silva")) {
            HospitalLocation = "41.1815541,-8.6710803";
        }
        if (holder.EAppointHospital.getText().toString().matches("Médio Ave")) {
            HospitalLocation = "41.377521,-8.5360971";
        }
        if (holder.EAppointHospital.getText().toString().matches("Porto")) {
            HospitalLocation = "41.1472309,-8.6217242";
        }
        if (holder.EAppointHospital.getText().toString().matches("Tâmega e Sousa")) {
            HospitalLocation = "41.1970225,-8.3117171";
        }
        if (holder.EAppointHospital.getText().toString().matches("Vila do Conde")) {
            HospitalLocation = "41.3689037,-8.7692049";
        }
        if (holder.EAppointHospital.getText().toString().matches("Magalhães Lemos")) {
            HospitalLocation = "41.1775305,-8.6685679";
        }
        if (holder.EAppointHospital.getText().toString().matches("Porto Francisco Gentil")) {
            HospitalLocation = "41.1823645,-8.6080161";
        }
        if (holder.EAppointHospital.getText().toString().matches("Pedro Hispano")) {
            HospitalLocation = "41.1818182,-8.6655801";
        }
        if (holder.EAppointHospital.getText().toString().matches("Médio Tejo")) {
            HospitalLocation = "39.5388913,-8.536345";
        }
        if (holder.EAppointHospital.getText().toString().matches("Santarém")) {
            HospitalLocation = "39.2410796,-8.6988352";
        }
        if (holder.EAppointHospital.getText().toString().matches("Barreiro Montijo")) {
            HospitalLocation = "38.6546747,-9.0604176";
        }
        if (holder.EAppointHospital.getText().toString().matches("Setúbal")) {
            HospitalLocation = "38.5090454,-8.9251669";
        }
        if (holder.EAppointHospital.getText().toString().matches("Garcia de Orta")) {
            HospitalLocation = "38.6740734,-9.179027";
        }
        if (holder.EAppointHospital.getText().toString().matches("Litoral Alentejano")) {
            HospitalLocation = "38.0400042,-8.7346887";
        }
        if (holder.EAppointHospital.getText().toString().matches("Alto Minho")) {
            HospitalLocation = "41.6877095,-8.8249634";
        }
        if (holder.EAppointHospital.getText().toString().matches("Trás-os-montes e Alto Douro")) {
            HospitalLocation = "41.3101655,-7.7622813";
        }
        if (holder.EAppointHospital.getText().toString().matches("Tondela-Viseu")) {
            HospitalLocation = "40.6505725,-7.9075104";
        }

    }
}
