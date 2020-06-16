package com.mp2.baymax20.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.mp2.baymax20.R;
import com.mp2.baymax20.adapter.AppointAdapter;
import com.mp2.baymax20.databse.AppointDatabase;
import com.mp2.baymax20.databse.AppointEntity;
import com.mp2.baymax20.databse.HospitalEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentFragment extends Fragment implements AppointAdapter.OnAppointStatusControl {

    public DoctorAppointmentFragment() {

    }

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private AppointAdapter appointAdapter;
    private List<AppointEntity> itemList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private LinearLayout llNoAppointment,llAppoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_appointment, container, false);

        recyclerView = view.findViewById(R.id.recyclerDoctorAppoint);
        layoutManager = new LinearLayoutManager(getActivity());
        llNoAppointment = view.findViewById(R.id.llNoAppointDoctor);
        llAppoint = view.findViewById(R.id.llAppointDoctor);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        recyclerView.setVisibility(View.GONE);
        llNoAppointment.setVisibility(View.GONE);
        llAppoint.setVisibility(View.GONE);

        itemList = Room.databaseBuilder(getActivity(), AppointDatabase.class, "appoint")
                .allowMainThreadQueries().build().appointDao().getDoctorAppointmentByStatus(sharedPreferences.getString("mobile", ""), "0");

        if (!itemList.isEmpty()) {
            appointAdapter = new AppointAdapter(getActivity(), itemList, DoctorAppointmentFragment.this, "2");
            recyclerView.setAdapter(appointAdapter);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in));
            recyclerView.setVisibility(View.VISIBLE);
            llAppoint.setVisibility(View.VISIBLE);
            llNoAppointment.setVisibility(View.GONE);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    llNoAppointment.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                    llNoAppointment.setVisibility(View.VISIBLE);
                    llAppoint.setVisibility(View.GONE);
                }
            },200);
        }

        return view;
    }

    @Override
    public void onAccept(@NotNull final AppointEntity appointEntity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Are you sure you want to accept the appointment?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Room.databaseBuilder(getActivity(),AppointDatabase.class,"appoint")
                        .allowMainThreadQueries().build().appointDao().updateAppointmentStatus("1",appointEntity.getTimeStamp());
                itemList.remove(appointEntity);
                appointAdapter.notifyDataSetChanged();
                SmsManager smsManager = (SmsManager) SmsManager.getDefault();
                smsManager.sendTextMessage(appointEntity.getAppoint_by(),null,
                        "Your Appointment has been successfully accepted!\nHospital : "+appointEntity.getHospital_name()+"\nDoctor : "+sharedPreferences.getString("name","")+"\nSlot : "+appointEntity.getAppoint_time()+"\nThank You,\nTeam Baymax",
                        null,null);
                if(itemList.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    llNoAppointment.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    public void onReject(@NotNull final AppointEntity appointEntity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Are you sure you want to reject the appointment?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Room.databaseBuilder(getActivity(),AppointDatabase.class,"appoint")
                        .allowMainThreadQueries().build().appointDao().updateAppointmentStatus("2",appointEntity.getTimeStamp());
                itemList.remove(appointEntity);
                appointAdapter.notifyDataSetChanged();
                SmsManager smsManager = (SmsManager) SmsManager.getDefault();
                smsManager.sendTextMessage(appointEntity.getAppoint_by(),null,
                        "Due to unavailability your appointment has been rejected by the doctor!\nHospital : "+appointEntity.getHospital_name()+"\nDoctor : "+sharedPreferences.getString("name","")+"\nSlot : "+appointEntity.getAppoint_time()+"\nPlease try again with some other time slot,\nTeam Baymax",
                        null,null);
                if(itemList.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    llNoAppointment.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    public void onCancel(@NotNull AppointEntity appointEntity) {

    }

    @Override
    public void onDirections(@NotNull HospitalEntity hospitalEntity) {

    }

    @Override
    public void onContact(@NotNull HospitalEntity hospitalEntity) {

    }
}
