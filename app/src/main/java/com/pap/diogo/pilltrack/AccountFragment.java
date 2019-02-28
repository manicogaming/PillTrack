package com.pap.diogo.pilltrack;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {
    private RecyclerView AccountUsers;
    private View mMainView;
    private FirebaseRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_account, container, false);

        AccountUsers = mMainView.findViewById(R.id.accountlist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        AccountUsers.setLayoutManager(linearLayoutManager);
        AccountUsers.setHasFixedSize(true);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseRecyclerOptions<Account> AccountQ = new FirebaseRecyclerOptions.Builder<Account>().setQuery(ref, Account.class).setLifecycleOwner(this).build();

        FirebaseRecyclerAdapter<Account, AccountInfo> AccountAdapter = new FirebaseRecyclerAdapter<Account, AccountInfo>(AccountQ){

            @NonNull
            @Override
            public AccountInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new AccountInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final AccountInfo holder, int position, @NonNull final Account model) {
                ref.child(userid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String age = dataSnapshot.child("idade").getValue().toString();
                        holder.setName(name);
                        holder.setAge(age);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        AccountUsers.setAdapter(AccountAdapter);
        AccountAdapter.startListening();
    }

    public static class AccountInfo extends RecyclerView.ViewHolder{
        View AccountL;

        public AccountInfo(@NonNull View itemView) {
            super(itemView);

            AccountL = itemView;
        }

        public void setName(String name){
            TextView AccountName = AccountL.findViewById(R.id.AccountName0);
            AccountName.setText(name);
        }

        public void setAge(String age){
            TextView AccountAge = AccountL.findViewById(R.id.AccountAge0);
            AccountAge.setText(age);
        }
    }
}

