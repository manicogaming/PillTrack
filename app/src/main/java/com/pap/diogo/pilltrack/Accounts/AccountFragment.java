package com.pap.diogo.pilltrack.Accounts;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.pap.diogo.pilltrack.Launcher;
import com.pap.diogo.pilltrack.R;

import org.w3c.dom.Text;

public class AccountFragment extends Fragment {
    private RecyclerView AccountUsers;
    private FirebaseRecyclerAdapter<Account, AccountInfo> AccountAdapter;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_account, container, false);

        AccountUsers = mMainView.findViewById(R.id.accountlist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        AccountUsers.setLayoutManager(linearLayoutManager);

        String[] arraySpinner = new String[]{
                "Masculino", "Feminino"
        };

        adapter = new ArrayAdapter(getContext(), R.layout.spinner_item_account, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = user.getUid();

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseRecyclerOptions<Account> AccountQ = new FirebaseRecyclerOptions.Builder<Account>().setQuery(ref, Account.class).setLifecycleOwner(this).build();

        AccountAdapter = new FirebaseRecyclerAdapter<Account, AccountInfo>(AccountQ) {

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
                        holder.setName(model.getName());
                        holder.setAge(model.getAge());
                        holder.setSex(model.getSex());
                        holder.setWeight(model.getWeight());
                        holder.setHeight(model.getHeight());

                        holder.EAccountSex.setAdapter(adapter);

                        holder.Logout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseAuth.getInstance().signOut();
                                Intent Launcher = new Intent(getContext(), Launcher.class);
                                Launcher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(Launcher);
                            }
                        });

                        holder.btnEditInfos.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.AccountName.setVisibility(View.GONE);
                                holder.AccountAge.setVisibility(View.GONE);
                                holder.AccountSex.setVisibility(View.GONE);
                                holder.AccountWeight.setVisibility(View.GONE);
                                holder.AccountHeight.setVisibility(View.GONE);

                                holder.EAccountName.setVisibility(View.VISIBLE);
                                holder.EAccountAge.setVisibility(View.VISIBLE);
                                holder.EAccountSex.setVisibility(View.VISIBLE);
                                holder.EAccountWeight.setVisibility(View.VISIBLE);
                                holder.EAccountHeight.setVisibility(View.VISIBLE);

                                holder.btnEditInfos.setVisibility(View.GONE);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.btnChangePass.getLayoutParams();
                                params.addRule(RelativeLayout.BELOW, R.id.SaveAccountInfos);
                                holder.btnChangePass.setLayoutParams(params);

                                holder.btnSaveChanges.setVisibility(View.VISIBLE);
                            }
                        });

                        holder.btnSaveChanges.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.AccountName.setVisibility(View.VISIBLE);
                                holder.AccountAge.setVisibility(View.VISIBLE);
                                holder.AccountSex.setVisibility(View.VISIBLE);
                                holder.AccountWeight.setVisibility(View.VISIBLE);
                                holder.AccountHeight.setVisibility(View.VISIBLE);

                                holder.EAccountName.setVisibility(View.GONE);
                                holder.EAccountAge.setVisibility(View.GONE);
                                holder.EAccountSex.setVisibility(View.GONE);
                                holder.EAccountWeight.setVisibility(View.GONE);
                                holder.EAccountHeight.setVisibility(View.GONE);

                                holder.btnEditInfos.setVisibility(View.VISIBLE);

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.btnChangePass.getLayoutParams();
                                params.addRule(RelativeLayout.BELOW, R.id.EditAccountInfos);
                                holder.btnChangePass.setLayoutParams(params);

                                holder.btnSaveChanges.setVisibility(View.GONE);

                                String Name = holder.EAccountName.getText().toString().trim();
                                String Age = holder.EAccountAge.getText().toString().trim();
                                String Sex = holder.EAccountSex.getSelectedItem().toString().trim();
                                String Weight = holder.EAccountWeight.getText().toString().trim();
                                String Height = holder.EAccountHeight.getText().toString().trim();

                                RegisterInfo userInformation = new RegisterInfo(Name, Age, Sex, Weight, Height);
                                ref.child(userid).setValue(userInformation);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };
        AccountUsers.setAdapter(AccountAdapter);
    }

    public static class AccountInfo extends RecyclerView.ViewHolder {
        View AccountL;
        Button btnChangePass, btnEditInfos, btnSaveChanges, Logout;
        TextView AccountName, AccountAge, AccountSex, AccountWeight, AccountHeight;
        EditText EAccountName, EAccountAge, EAccountWeight, EAccountHeight;
        Spinner EAccountSex;

        public AccountInfo(@NonNull View itemView) {
            super(itemView);

            AccountL = itemView;
            btnChangePass = AccountL.findViewById(R.id.AccountChangePass);
            btnEditInfos = AccountL.findViewById(R.id.EditAccountInfos);
            btnSaveChanges = AccountL.findViewById(R.id.SaveAccountInfos);

            Logout = AccountL.findViewById(R.id.Logout);

            btnChangePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccountL.getContext().startActivity(new Intent(AccountL.getContext(), ChangePW.class));
                }
            });
        }

        public void setName(String name) {
            AccountName = AccountL.findViewById(R.id.AccountName);
            EAccountName = AccountL.findViewById(R.id.EAccountName);
            AccountName.setText(name);
            EAccountName.setText(name);
        }

        public void setAge(String age) {
            AccountAge = AccountL.findViewById(R.id.AccountAge);
            EAccountAge = AccountL.findViewById(R.id.EAccountAge);
            AccountAge.setText(age + " anos");
            EAccountAge.setText(age);
        }

        public void setSex(String sex) {
            AccountSex = AccountL.findViewById(R.id.AccountSex);
            EAccountSex = AccountL.findViewById(R.id.EAccountSex);
            AccountSex.setText(sex);
        }

        public void setWeight(String weight) {
            AccountWeight = AccountL.findViewById(R.id.AccountWeight);
            EAccountWeight = AccountL.findViewById(R.id.EAccountWeight);
            AccountWeight.setText(weight + " kg");
            EAccountWeight.setText(weight);
        }

        public void setHeight(String height) {
            AccountHeight = AccountL.findViewById(R.id.AccountHeight);
            EAccountHeight = AccountL.findViewById(R.id.EAccountHeight);
            AccountHeight.setText(height + " cm");
            EAccountHeight.setText(height);
        }
    }
}

