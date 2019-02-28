package com.pap.diogo.pilltrack;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AccountFragment extends Fragment {
    private RecyclerView recyclerView;
    private Query query;
    private View mMainView;
    private FirebaseRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_account, container, false);

        recyclerView = mMainView.findViewById(R.id.accountlist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Account> options =
                new FirebaseRecyclerOptions.Builder<Account>()
                        .setQuery(query, new SnapshotParser<Account>() {
                            @NonNull
                            @Override
                            public Account parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Account(snapshot.child("name").getValue().toString(),
                                        snapshot.child("idade").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Account, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.account, parent, false));
            }


            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Account model) {
                holder.setTxtTitle(model.getName());
                holder.setTxtDesc(model.getIdade());
            }

        };
        recyclerView.setAdapter(adapter);

        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtAge;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.AccountName0);
            txtAge = itemView.findViewById(R.id.AccountAge0);
        }

        public void setTxtTitle(String string) {
            txtName.setText(string);
        }


        public void setTxtDesc(String string) {
            txtName.setText(string);
        }
    }
}

