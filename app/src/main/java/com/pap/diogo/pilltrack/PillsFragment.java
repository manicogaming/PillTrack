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

public class PillsFragment extends Fragment {
    private ImageButton add_pill;
    private RecyclerView EPills;
    private FirebaseRecyclerAdapter<Pill, EPillsInfo> EAppointsAdapter;

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

        EAppointsAdapter = new FirebaseRecyclerAdapter<Pill, EPillsInfo>(PillQ){

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
                        holder.setName(model.getName());
                        holder.setPillFunc(model.getPillfunc());
                        holder.setInterval(model.getInterval());

                        holder.EPillDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pRef.child(model.getName()).removeValue();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        EPills.setAdapter(EAppointsAdapter);
    }

    public static class EPillsInfo extends RecyclerView.ViewHolder{
        View EPillsL;
        ImageButton EPillDelete;

        public EPillsInfo(@NonNull View itemView) {
            super(itemView);

            EPillsL = itemView;
            EPillDelete = EPillsL.findViewById(R.id.EPillDelete);
        }

        public void setName(String name){
            TextView EPillName = EPillsL.findViewById(R.id.EPillName);
            EPillName.setText(name);
        }

        public void setPillFunc(String pillfunc){
            TextView EPillFunc = EPillsL.findViewById(R.id.EPillFunc);
            EPillFunc.setText(pillfunc);
        }

        public void setInterval(String interval){
            TextView EPillInterval = EPillsL.findViewById(R.id.EPillInterval);
            EPillInterval.setText(interval);
        }
    }
}
