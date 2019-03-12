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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pap.diogo.pilltrack.Appoints.Appoint;
import com.pap.diogo.pilltrack.Pills.Pill;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private RecyclerView Pills, Appoints;
    private FirebaseRecyclerAdapter<Pill, PillsInfo> PillsAdapter;
    private FirebaseRecyclerAdapter<Appoint, AppointsInfo> AppointsAdapter;

    private FirebaseAuth mAuth;
    DatabaseReference pRef, aRef;
    DateTime date;
    LocalDate cDate, mDate;

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

                        String dtStart = model.getDate();

                        DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                        mDate = org.joda.time.LocalDate.parse(dtStart, format);

                        cDate = new LocalDate();

                        int days = Days.daysBetween(cDate, mDate).getDays();

                        String rDays = String.valueOf(days);

                        holder.setDate("Faltam " + rDays + " dias.");

                        holder.EditAppoint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity)getActivity()).setNavItem(2);
                            }
                        });
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

                        holder.EditPill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity)getActivity()).setNavItem(1);
                            }
                        });
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
        ImageButton EditPill;
        public PillsInfo(@NonNull View itemView) {
            super(itemView);

            PillsL = itemView;
            EditPill = PillsL.findViewById(R.id.EditPill);
        }

        public void setName(String name){
            TextView pName = PillsL.findViewById(R.id.pName);
            pName.setText(name);
        }

        public void setPillFunc(String pillfunc){
            TextView pFunc = PillsL.findViewById(R.id.pFunc);
            pFunc.setText(pillfunc);
        }

        public void setInterval(String interval){
            TextView PillInterval = PillsL.findViewById(R.id.pNext);
            PillInterval.setText(interval);
        }
    }

    public static class AppointsInfo extends RecyclerView.ViewHolder{
        View AppointsL;
        ImageButton EditAppoint;
        Button MapsLoc;

        public AppointsInfo(@NonNull View itemView) {
            super(itemView);

            AppointsL = itemView;
            EditAppoint = AppointsL.findViewById(R.id.EditAppoint);
            MapsLoc = AppointsL.findViewById(R.id.MapsLoc);

            MapsLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppointsL.getContext().startActivity(new Intent(AppointsL.getContext(), MapsActivity.class));
                }
            });
        }

        public void setName(String name){
            TextView aName = AppointsL.findViewById(R.id.aName);
            aName.setText(name);
        }

        public void setDate(String date){
            TextView aDate = AppointsL.findViewById(R.id.aDate);
            aDate.setText(date);
        }
    }
}
