package com.mp2.baymax20.fragment;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.mp2.baymax20.R;
import com.mp2.baymax20.adapter.HospitalAdapter;
import com.mp2.baymax20.databse.AppointDatabase;
import com.mp2.baymax20.databse.AppointEntity;
import com.mp2.baymax20.databse.HospitalDatabase;
import com.mp2.baymax20.databse.HospitalEntity;
import com.mp2.baymax20.databse.UserEntity;
import com.mp2.baymax20.dialog.DialogSelectDoctor;
import com.mp2.baymax20.dialog.DialogSelectSpe;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class PatientAppointmentFragment extends Fragment implements HospitalAdapter.OnHospitalInterface, DialogSelectSpe.OnSpecializationInterface, DialogSelectDoctor.OnRequestAppointment {

    public PatientAppointmentFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private HospitalAdapter hospitalAdapter;
    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_appointment, container, false);

        recyclerView = view.findViewById(R.id.recyclerHospital);
        layoutManager = new LinearLayoutManager(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        recyclerView.setVisibility(View.GONE);

        if(sharedPreferences.getBoolean("hospitalEntry",true)){
            ArrayList<HospitalEntity> dummy = new ArrayList<>();
            dummy.add(new HospitalEntity("Noble Hospital","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/nobel.jpg?alt=media&token=a405de76-bd19-4652-ac20-7a2ddae349c6","Magarpatta, Pune",
                    "https://goo.gl/maps/wc4SNH3ihMCjBppf7","5.0/5.0","8007006611"));
            dummy.add(new HospitalEntity("Sahyadri Hospital","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/shayadri.jpg?alt=media&token=9845339e-59b6-49e6-a2ae-cd0469f04414","Hadapsar, Pune",
                    "https://goo.gl/maps/351AqCBN9BpFCBym8","4.5/5.0","8806252525"));
            dummy.add(new HospitalEntity("Vishwaraj Hospital","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/vishwaraj.jpg?alt=media&token=c52b658d-f7c0-4dcc-a0a1-65ee7c2b515b","Loni Kalbhor, Pune",
                    "https://goo.gl/maps/Z8b7PGdrTFYrbvJ58","4.1/5.0","02067606060"));
            dummy.add(new HospitalEntity("Ruby Hall Clinic","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/ruby.jpg?alt=media&token=c0635ff0-0823-4849-b786-09186da76284","Sangamwadi, Pune",
                    "https://goo.gl/maps/qGx7KmY51F6xLw4FA","3.5/5.0","02026163391"));
            dummy.add(new HospitalEntity("Jehangir Hospital","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/jehangir.webp?alt=media&token=cb5b106b-04a4-4715-9e19-d5a11b5000f2","Sangamwadi, Pune",
                    "https://g.page/Jehangir-Hospital-Pune?share","4.2/5.0","02066819999"));

            for(int i=0;i<5;i++){
                Room.databaseBuilder(getActivity(),HospitalDatabase.class,"hospital")
                        .allowMainThreadQueries().build().hospitalDao().insertHospital(dummy.get(i));
            }

            sharedPreferences.edit().putBoolean("hospitalEntry",false).apply();
        }

        List<HospitalEntity> hospitalEntities = Room.databaseBuilder(getActivity(),HospitalDatabase.class,"hospital")
                .allowMainThreadQueries().build().hospitalDao().getAllHospital();

        hospitalAdapter = new HospitalAdapter(getActivity(),hospitalEntities,PatientAppointmentFragment.this);
        recyclerView.setAdapter(hospitalAdapter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
        recyclerView.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onSelectHospital(@NotNull HospitalEntity hospitalEntity) {

        DialogFragment dialogFragment = new DialogSelectSpe(PatientAppointmentFragment.this,hospitalEntity.getName());
        dialogFragment.setCancelable(false);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"SelectSpe");

    }

    @Override
    public void onGetHospitalDirection(@NotNull HospitalEntity hospitalEntity) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                hospitalEntity.getDirections()
        ));
        startActivity(intent);
    }

    @Override
    public void onContactHospital(@NotNull HospitalEntity hospitalEntity) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(
                "tel:"+hospitalEntity.getContact()
        ));
        startActivity(intent);
    }

    @Override
    public void onSelected(String spe, String hospital) {
        DialogFragment dialogFragment = new DialogSelectDoctor(spe,hospital,PatientAppointmentFragment.this);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"SelectSpe");
    }

    @Override
    public void onRequestAppointment(UserEntity doctor, String slot) {
        if(!doctor.getMobile().equals("dummy")) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyYY-MM-dd HH:mm:ss");
            String timeStamp = simpleDateFormat.format(calendar.getTime());
            AppointEntity appointEntity = new AppointEntity(timeStamp, sharedPreferences.getString("mobile", ""),
                    doctor.getMobile(), "0", slot, doctor.getHospital());
            Room.databaseBuilder(getActivity(), AppointDatabase.class, "appoint")
                    .allowMainThreadQueries().build().appointDao().insertAppoint(appointEntity);
        }
        SmsManager smsManager = (SmsManager) SmsManager.getDefault();
        smsManager.sendTextMessage(sharedPreferences.getString("mobile",""),null,
                "Your Appointment has been successfully requested!\nHospital : "+doctor.getHospital()+"\nSlot : "+slot+"\nThank You,\nTeam Baymax",
                null,null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Your Appointment has been successfully requested! Please check out the profile section for Status.");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create();
        dialog.show();
    }
}
