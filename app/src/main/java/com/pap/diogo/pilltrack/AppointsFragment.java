package com.pap.diogo.pilltrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppointsFragment extends Fragment {
    private ImageButton add_appoint;
    private RecyclerView EAppoints;
    private FirebaseRecyclerAdapter<Appoint, EAppointsInfo> EAppointsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_appoints, container, false);

        EAppoints = mMainView.findViewById(R.id.eappointslist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        EAppoints.setLayoutManager(linearLayoutManager);

        add_appoint = mMainView.findViewById(R.id.add);

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseRecyclerOptions<Appoint> AccountQ = new FirebaseRecyclerOptions.Builder<Appoint>().setQuery(ref, Appoint.class).setLifecycleOwner(this).build();

        EAppointsAdapter = new FirebaseRecyclerAdapter<Appoint, EAppointsInfo>(AccountQ){

            @NonNull
            @Override
            public EAppointsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new EAppointsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.eappoints, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final EAppointsInfo holder, int position, @NonNull final Appoint model) {
                ref.child(userid).child("Appoints").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appoint Appoint = snapshot.getValue(Appoint.class);
                            final String name = Appoint.getName();
                            final String date = Appoint.getDate();
                            final String hospital = Appoint.getHospital();
                            holder.setName(name);
                            holder.setDate(date);
                            holder.setHospital(hospital);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        EAppoints.setAdapter(EAppointsAdapter);
        EAppointsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        EAppointsAdapter.stopListening();
    }

    public static class EAppointsInfo extends RecyclerView.ViewHolder{
        View EAppointsL;

        public EAppointsInfo(@NonNull View itemView) {
            super(itemView);

            EAppointsL = itemView;
        }

        public void setName(String name){
            TextView AppointName = EAppointsL.findViewById(R.id.EAppointName);
            AppointName.setText(name);
        }

        public void setDate(String date){
            TextView AppointDate = EAppointsL.findViewById(R.id.EAppointDate);
            AppointDate.setText(date);
        }

        public void setHospital(String hospital){
            TextView AppointHospital = EAppointsL.findViewById(R.id.EAppointHospital);
            AppointHospital.setText(hospital);
        }
    }
}
