package com.pap.diogo.pilltrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.pap.diogo.pilltrack.Appoints.Exam;
import com.pap.diogo.pilltrack.Pills.Pill;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    private RecyclerView Pills, Appoints, Exams;
    private FirebaseRecyclerAdapter<Pill, PillsInfo> PillsAdapter;
    private FirebaseRecyclerAdapter<Appoint, AppointsInfo> AppointsAdapter;
    private FirebaseRecyclerAdapter<Exam, ExamsInfo> ExamsAdapter;
    private FirebaseAuth mAuth;
    private String userid;
    private TextView NoAppoints, NoPills;
    DatabaseReference pRef, aRef, eRef;
    Date date;

    LocalDate cDate, mDate, sDate, eDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mMainView = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();

        userid = mAuth.getCurrentUser().getUid();

        pRef = FirebaseDatabase.getInstance().getReference().child("Pills").child(userid);
        aRef = FirebaseDatabase.getInstance().getReference().child("Appoints").child(userid);
        eRef = FirebaseDatabase.getInstance().getReference().child("Exams").child(userid);

        Appoints = mMainView.findViewById(R.id.appointslist);
        Exams = mMainView.findViewById(R.id.examslist);
        Pills = mMainView.findViewById(R.id.pillslist);

        NoAppoints = mMainView.findViewById(R.id.NoAppoints);
        NoPills = mMainView.findViewById(R.id.NoPills);

        LinearLayoutManager lAppoints = new LinearLayoutManager(getContext());
        LinearLayoutManager lExams = new LinearLayoutManager(getContext());
        LinearLayoutManager lPills = new LinearLayoutManager(getContext());
        Appoints.setLayoutManager(lAppoints);
        Exams.setLayoutManager(lExams);
        Pills.setLayoutManager(lPills);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        ShowPills();
        ShowAppoints();
        ShowExams();

    }

    private void ShowAppoints() {
        FirebaseRecyclerOptions<Appoint> AppointQ = new FirebaseRecyclerOptions.Builder<Appoint>().setQuery(aRef, Appoint.class).setLifecycleOwner(this).build();

        AppointsAdapter = new FirebaseRecyclerAdapter<Appoint, AppointsInfo>(AppointQ) {

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
                        NoAppoints.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setDate(model.getDate());

                        String dtStart = model.getDate();
                        DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                        mDate = org.joda.time.LocalDate.parse(dtStart, format);
                        cDate = new LocalDate();
                        int days = Days.daysBetween(cDate, mDate).getDays();

                        if (days < 0) {
                            aRef.child(model.getName()).removeValue();
                        } else if (days == 0) {
                            holder.setDate("A consulta é hoje.");

                        } else {
                            String rDays = String.valueOf(days);
                            holder.setDate("Faltam " + rDays + " dias.");
                        }

                        holder.MapsLoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr=" + model.getHlocation()));
                                startActivity(intent);
                            }
                        });

                        holder.EditAppoint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity) getActivity()).setNavItem(2);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        if (AppointsAdapter.getItemCount() == 0) {
            NoAppoints.setVisibility(View.VISIBLE);
        }

        Appoints.setAdapter(AppointsAdapter);
    }

    private void ShowExams() {
        FirebaseRecyclerOptions<Exam> ExamQ = new FirebaseRecyclerOptions.Builder<Exam>().setQuery(eRef, Exam.class).setLifecycleOwner(this).build();

        ExamsAdapter = new FirebaseRecyclerAdapter<Exam, ExamsInfo>(ExamQ) {

            @NonNull
            @Override
            public ExamsInfo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ExamsInfo(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exams, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final ExamsInfo holder, int position, @NonNull final Exam model) {
                eRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        NoAppoints.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setDate(model.getDate());
                        holder.setPrepMethod(model.getPrepmethod());

                        String dtStart = model.getDate();
                        DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                        mDate = org.joda.time.LocalDate.parse(dtStart, format);
                        cDate = new LocalDate();
                        int days = Days.daysBetween(cDate, mDate).getDays();

                        if (days < 0) {
                            aRef.child(model.getName()).removeValue();
                        } else if (days == 0) {
                            holder.setDate("O exame é hoje.");
                        } else {
                            String rDays = String.valueOf(days);
                            holder.setDate("Faltam " + rDays + " dias.");
                        }

                        holder.setPrep(model.getPrep());

                        holder.MapsLoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse("http://maps.google.com/maps?daddr=" + model.getHlocation()));
                                startActivity(intent);
                            }
                        });

                        holder.EditExam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity) getActivity()).setNavItem(2);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        if (ExamsAdapter.getItemCount() == 0) {
            NoAppoints.setVisibility(View.VISIBLE);
        }

        Exams.setAdapter(ExamsAdapter);
    }

    private void ShowPills() {

        FirebaseRecyclerOptions<Pill> PillQ = new FirebaseRecyclerOptions.Builder<Pill>().setQuery(pRef, Pill.class).setLifecycleOwner(this).build();

        PillsAdapter = new FirebaseRecyclerAdapter<Pill, PillsInfo>(PillQ) {

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
                        NoPills.setVisibility(View.GONE);

                        holder.setName(model.getName());
                        holder.setPillFunc(model.getPillfunc());

                        String dtNow = model.getPillstartdate();
                        DateTimeFormatter curr = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                        mDate = org.joda.time.LocalDate.parse(dtNow, curr);
                        cDate = new LocalDate();
                        int currdays = Days.daysBetween(cDate, mDate).getDays();

                        if (currdays < 0) {
                            pRef.child(model.getName()).removeValue();
                        } else if (currdays == 0) {
                            String dtStart = model.getPillstartdate();
                            String dtEnd = model.getPillenddate();
                            DateTimeFormatter format = org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy");
                            sDate = org.joda.time.LocalDate.parse(dtStart, format);
                            eDate = org.joda.time.LocalDate.parse(dtEnd, format);
                            int days = Days.daysBetween(sDate, eDate).getDays();

                            if (days < 0) {
                                pRef.child(model.getName()).removeValue();
                            } else if (days == 0) {
                                holder.setDuration("O tratamento acaba hoje.");
                            } else {
                                String rDays = String.valueOf(days);
                                holder.setDuration("Tratamento de " + rDays + " dias.");
                            }
                        } else {
                            String rDays = String.valueOf(currdays);
                            holder.setDuration("Faltam " + rDays + " dias.");
                        }

                        String interval = model.getInterval();
                        String hour = model.getPillhour();

                        if (interval.equals("4 em 4 horas")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                            try {
                                date = dateFormat.parse(hour);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.HOUR, 4);
                            String currentTime = checkDigit(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + checkDigit(calendar.get(Calendar.MINUTE));
                            holder.setInterval("Próxima medicação: " + currentTime);
                        }

                        holder.EditPill.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity) getActivity()).setNavItem(1);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        };

        if (PillsAdapter.getItemCount() == 0) {
            NoPills.setVisibility(View.VISIBLE);
        }

        Pills.setAdapter(PillsAdapter);
    }

    public static class PillsInfo extends RecyclerView.ViewHolder {
        View PillsL;
        ImageButton EditPill;

        public PillsInfo(@NonNull View itemView) {
            super(itemView);

            PillsL = itemView;
            EditPill = PillsL.findViewById(R.id.EditPill);
        }

        public void setName(String name) {
            TextView pName = PillsL.findViewById(R.id.pName);
            pName.setText(name);
        }

        public void setPillFunc(String pillfunc) {
            TextView pFunc = PillsL.findViewById(R.id.pFunc);
            pFunc.setText(pillfunc);
        }

        public void setInterval(String interval) {
            TextView PillInterval = PillsL.findViewById(R.id.pNext);
            PillInterval.setText(interval);
        }

        public void setDuration(String duration) {
            TextView PillDuration = PillsL.findViewById(R.id.pDuration);
            PillDuration.setText(duration);
        }
    }

    public static class AppointsInfo extends RecyclerView.ViewHolder {
        View AppointsL;
        ImageButton EditAppoint, EnableNotifications, DisableNotifications;
        Button MapsLoc;

        public AppointsInfo(@NonNull View itemView) {
            super(itemView);

            AppointsL = itemView;
            EditAppoint = AppointsL.findViewById(R.id.EditAppoint);
            MapsLoc = AppointsL.findViewById(R.id.MapsLoc);
        }

        public void setName(String name) {
            TextView aName = AppointsL.findViewById(R.id.aName);
            aName.setText(name);
        }

        public void setDate(String date) {
            TextView aDate = AppointsL.findViewById(R.id.aDate);
            aDate.setText(date);
        }
    }

    public static class ExamsInfo extends RecyclerView.ViewHolder {
        View ExamsL;
        ImageButton EditExam;
        Button MapsLoc;

        public ExamsInfo(@NonNull View itemView) {
            super(itemView);

            ExamsL = itemView;
            EditExam = ExamsL.findViewById(R.id.EditExam);
            MapsLoc = ExamsL.findViewById(R.id.MapsLoc);
        }

        public void setName(String name) {
            TextView eName = ExamsL.findViewById(R.id.eName);
            eName.setText(name);
        }

        public void setDate(String date) {
            TextView eDate = ExamsL.findViewById(R.id.eDate);
            eDate.setText(date);
        }

        public void setPrep(String prep) {
            TextView ePrep = ExamsL.findViewById(R.id.ePrep);
            ePrep.setText(prep + " Dias de Preparação.");
        }

        public void setPrepMethod(String prepmethod) {
            TextView ePrepMethod = ExamsL.findViewById(R.id.ePrepMethod);
            ePrepMethod.setText(prepmethod);
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
