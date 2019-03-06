package com.pap.diogo.pilltrack;

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

public class HomeFragment extends Fragment {
    private RecyclerView Pills, Appoints;
    private FirebaseRecyclerAdapter<Pill, PillsInfo> PillsAdapter;
    private FirebaseRecyclerAdapter<Appoint, AppointsInfo> AppointsAdapter;

    private FirebaseAuth mAuth;
    DatabaseReference pRef, aRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();

        String userid = mAuth.getCurrentUser().getUid();

        pRef = FirebaseDatabase.getInstance().getReference().child("Pills").child(userid);
        aRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);

        Appoints = mMainView.findViewById(R.id.appointslist);
        Pills = mMainView.findViewById(R.id.pillslist);

        LinearLayoutManager lAppoints = new LinearLayoutManager(getContext());
        LinearLayoutManager lPills = new LinearLayoutManager(getContext());
        Appoints.setLayoutManager(lAppoints);
        Pills.setLayoutManager(lPills);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        ShowPills();
        ShowAppoints();

    }

    private void ShowAppoints() {
        FirebaseRecyclerOptions<Appoint> AppointQ = new FirebaseRecyclerOptions.Builder<Appoint>().setQuery(aRef, Appoint.class).setLifecycleOwner(this).build();

        AppointsAdapter = new FirebaseRecyclerAdapter<Appoint, AppointsInfo>(AppointQ){

            @NonNull
            @Override
            public AppointsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new AppointsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.appoints, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final AppointsInfo holder, int position, @NonNull final Appoint model) {
                aRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.setName(model.getName());
                        holder.setDate(model.getDate());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        Appoints.setAdapter(AppointsAdapter);
    }

    private void ShowPills() {
        FirebaseRecyclerOptions<Pill> PillQ = new FirebaseRecyclerOptions.Builder<Pill>().setQuery(pRef, Pill.class).setLifecycleOwner(this).build();

        PillsAdapter = new FirebaseRecyclerAdapter<Pill, PillsInfo>(PillQ){

            @NonNull
            @Override
            public PillsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new PillsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pills, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final PillsInfo holder, int position, @NonNull final Pill model) {
                pRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.setName(model.getName());
                        holder.setPillFunc(model.getPillfunc());
                        holder.setInterval(model.getInterval());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        Pills.setAdapter(PillsAdapter);
    }


    public static class PillsInfo extends RecyclerView.ViewHolder{
        View PillsL;

        public PillsInfo(@NonNull View itemView) {
            super(itemView);

            PillsL = itemView;
        }

        public void setName(String name){
            TextView PillName = PillsL.findViewById(R.id.PillName);
            PillName.setText(name);
        }

        public void setPillFunc(String pillfunc){
            TextView PillFunc = PillsL.findViewById(R.id.PillFunc);
            PillFunc.setText(pillfunc);
        }

        public void setInterval(String interval){
            TextView PillInterval = PillsL.findViewById(R.id.NextPill);
            PillInterval.setText(interval);
        }
    }

    public static class AppointsInfo extends RecyclerView.ViewHolder{
        View AppointsL;

        public AppointsInfo(@NonNull View itemView) {
            super(itemView);

            AppointsL = itemView;
        }

        public void setName(String name){
            TextView AppointName = AppointsL.findViewById(R.id.AppointName);
            AppointName.setText(name);
        }

        public void setDate(String date){
            TextView AppointDate = AppointsL.findViewById(R.id.AppointDate);
            AppointDate.setText(date);
        }
    }
}
