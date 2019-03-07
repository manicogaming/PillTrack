package com.pap.diogo.pilltrack.Pills;

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

                        EditPills(holder, model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        EPills.setAdapter(EAppointsAdapter);
    }

    private void EditPills(@NonNull final EPillsInfo holder, @NonNull final Pill model) {
        holder.PillName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.PillName.setVisibility(View.GONE);
                holder.EPillName.setVisibility(View.VISIBLE);
                holder.EPillName.requestFocus();

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EPillName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus)
                        {
                            holder.PillName.setVisibility(View.VISIBLE);
                            holder.EPillName.setVisibility(View.GONE);

                            pRef.child(model.getName()).removeValue();

                            String newname = holder.EPillName.getText().toString().trim();
                            String pillfunc = holder.PillFunc.getText().toString().trim();
                            String interval = holder.PillInterval.getText().toString().trim();

                            PillInfo PillInfo = new PillInfo(newname, pillfunc, interval);
                            pRef.child(newname).setValue(PillInfo);
                        }
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

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EPillFunc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus)
                        {
                            holder.PillFunc.setVisibility(View.VISIBLE);
                            holder.EPillFunc.setVisibility(View.GONE);

                            String newpillfunc = holder.EPillFunc.getText().toString().trim();

                            pRef.child(model.getName()).child("pillfunc").setValue(newpillfunc);
                        }
                    }
                });
            }
        });

        holder.PillInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.PillInterval.setVisibility(View.GONE);
                holder.EPillInterval.setVisibility(View.VISIBLE);
                holder.EPillInterval.requestFocus();

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                holder.EPillInterval.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(!hasFocus)
                        {
                            holder.PillInterval.setVisibility(View.VISIBLE);
                            holder.EPillInterval.setVisibility(View.GONE);

                            String newinterval = holder.EPillInterval.getText().toString().trim();

                            pRef.child(model.getName()).child("interval").setValue(newinterval);
                        }
                    }
                });
            }
        });
    }

    public static class EPillsInfo extends RecyclerView.ViewHolder{
        View EPillsL;
        ImageButton EPillDelete;
        TextView PillName, PillFunc, PillInterval;
        EditText EPillName, EPillFunc, EPillInterval;

        public EPillsInfo(@NonNull View itemView) {
            super(itemView);

            EPillsL = itemView;
            EPillDelete = EPillsL.findViewById(R.id.EPillDelete);
        }

        public void setName(String name){
            PillName = EPillsL.findViewById(R.id.PillName);
            EPillName = EPillsL.findViewById(R.id.EPillName);
            PillName.setText(name);
            EPillName.setText(name);
        }

        public void setPillFunc(String pillfunc){
            PillFunc = EPillsL.findViewById(R.id.PillFunc);
            EPillFunc = EPillsL.findViewById(R.id.EPillFunc);
            PillFunc.setText(pillfunc);
            EPillFunc.setText(pillfunc);
        }

        public void setInterval(String interval){
            PillInterval = EPillsL.findViewById(R.id.PillInterval);
            EPillInterval = EPillsL.findViewById(R.id.EPillInterval);
            PillInterval.setText(interval);
            EPillInterval.setText(interval);
        }
    }
}
