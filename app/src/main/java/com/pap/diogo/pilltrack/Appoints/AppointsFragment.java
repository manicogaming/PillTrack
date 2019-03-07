package com.pap.diogo.pilltrack.Appoints;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
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

public class AppointsFragment extends Fragment {
    private ImageButton add_appoint;
    private RecyclerView EAppoints;
    private FirebaseRecyclerAdapter<Appoint, EAppointsInfo> EAppointsAdapter;

    private FirebaseAuth mAuth;
    DatabaseReference  pRef;

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

        EAppointsAdapter = new FirebaseRecyclerAdapter<Appoint, EAppointsInfo>(AppointQ){

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
                        if(!hasFocus)
                        {
                            holder.AppointName.setVisibility(View.VISIBLE);
                            holder.EAppointName.setVisibility(View.GONE);

                            pRef.child(model.getName()).removeValue();

                            String newname = holder.EAppointName.getText().toString().trim();
                            String date = holder.AppointDate.getText().toString().trim();
                            String hospital = holder.AppointHospital.getText().toString().trim();

                            AppointInfo AppointInfo = new AppointInfo(newname, date, hospital);
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

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EAppointDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus)
                        {
                            holder.AppointDate.setVisibility(View.VISIBLE);
                            holder.EAppointDate.setVisibility(View.GONE);

                            String newdate = holder.EAppointDate.getText().toString().trim();

                            pRef.child(model.getName()).child("date").setValue(newdate);
                        }
                    }
                });
            }
        });

        holder.AppointHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.AppointHospital.setVisibility(View.GONE);
                holder.EAppointHospital.setVisibility(View.VISIBLE);
                holder.EAppointHospital.requestFocus();

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EAppointHospital.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus)
                        {
                            holder.AppointHospital.setVisibility(View.VISIBLE);
                            holder.EAppointHospital.setVisibility(View.GONE);

                            String newhospital = holder.EAppointHospital.getText().toString().trim();

                            pRef.child(model.getName()).child("hospital").setValue(newhospital);
                        }
                    }
                });
            }
        });
    }

    public static class EAppointsInfo extends RecyclerView.ViewHolder{
        View EAppointsL;
        ImageButton EAppointDelete;
        TextView AppointName, AppointDate, AppointHospital;
        EditText EAppointName, EAppointDate, EAppointHospital;

        public EAppointsInfo(@NonNull View itemView) {
            super(itemView);

            EAppointsL = itemView;
            EAppointDelete = EAppointsL.findViewById(R.id.EAppointDelete);
        }

        public void setName(String name){
            AppointName = EAppointsL.findViewById(R.id.AppointName);
            EAppointName = EAppointsL.findViewById(R.id.EAppointName);
            AppointName.setText(name);
            EAppointName.setText(name);
        }

        public void setDate(String date){
            AppointDate = EAppointsL.findViewById(R.id.AppointDate);
            EAppointDate = EAppointsL.findViewById(R.id.EAppointDate);
            AppointDate.setText(date);
            EAppointDate.setText(date);
        }

        public void setHospital(String hospital){
            AppointHospital = EAppointsL.findViewById(R.id.AppointHospital);
            EAppointHospital = EAppointsL.findViewById(R.id.EAppointHospital);
            AppointHospital.setText(hospital);
            EAppointHospital.setText(hospital);
        }
    }
}
