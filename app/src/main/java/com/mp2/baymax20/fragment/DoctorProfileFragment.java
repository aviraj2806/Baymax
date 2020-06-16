package com.mp2.baymax20.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mp2.baymax20.R;
import com.mp2.baymax20.activity.AuthActivity;
import com.mp2.baymax20.adapter.AppointAdapter;
import com.mp2.baymax20.adapter.NewAdapter;
import com.mp2.baymax20.databse.AppointDatabase;
import com.mp2.baymax20.databse.AppointEntity;
import com.mp2.baymax20.databse.HospitalEntity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DoctorProfileFragment extends Fragment implements NewAdapter.OnAcademyVideoClick, AppointAdapter.OnAppointStatusControl {

    public DoctorProfileFragment() {


    }

    private TextView txtName,txtMobile,txtEmail,txtHospital,txtSpe;
    private ImageView imgUser,imgLogOut;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private NewAdapter newAdapter;
    private ArrayList<String> newList = new ArrayList<>();
    private NestedScrollView nsvProfile;
    private CardView cvDetails;
    private SharedPreferences sharedPreferences;

    private RecyclerView recyclerUpcoming;
    private LinearLayoutManager layoutManagerUpcoming;
    private List<AppointEntity> itemListAppoint = new ArrayList<>();
    private AppointAdapter appointAdapter;
    private LinearLayout llNoUpcoming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        txtEmail = view.findViewById(R.id.txtDoctorEmail);
        txtName = view.findViewById(R.id.txtDoctorName);
        txtMobile = view.findViewById(R.id.txtDoctorMobile);
        txtHospital = view.findViewById(R.id.txtDoctorHospital);
        txtSpe = view.findViewById(R.id.txtDoctorSpe);
        imgUser = view.findViewById(R.id.imgDoctorProfile);
        imgLogOut = view.findViewById(R.id.imgDoctorOut);
        nsvProfile = view.findViewById(R.id.nsvDoctorProfile);
        cvDetails = view.findViewById(R.id.cvDetailsDoctor);
        llNoUpcoming = view.findViewById(R.id.llNoUpcoming);
        recyclerUpcoming = view.findViewById(R.id.recyclerUpcomingDoctor);
        layoutManagerUpcoming = new LinearLayoutManager(getActivity());
        layoutManagerUpcoming.setOrientation(LinearLayoutManager.HORIZONTAL);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerNew);
        layoutManager = new LinearLayoutManager(getActivity());
        nsvProfile.setVisibility(View.GONE);
        cvDetails.setVisibility(View.GONE);
        recyclerUpcoming.setVisibility(View.GONE);
        llNoUpcoming.setVisibility(View.GONE);

        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                getActivity().finish();
            }
        });

        txtName.setText(getSharedData("name"));
        txtMobile.setText(getSharedData("mobile"));
        txtEmail.setText(getSharedData("email"));
        txtSpe.setText("Specialization : "+getSharedData("spe"));
        txtHospital.setText("Hospital : "+getSharedData("hospital"));

        Picasso.get().load(getSharedData("image")).error(R.drawable.avatar).into(imgUser);

        newList.add("z2KpsZRpoZ8");
        newList.add("L7-RGz9mqiI");
        newList.add("7oqvFxOsQJg");
        newList.add("FAh07A2peOI");
        newList.add("MzMaH_J-UmI");
        newAdapter = new NewAdapter(getActivity(),this,newList);
        recyclerView.setAdapter(newAdapter);
        recyclerView.setLayoutManager(layoutManager);

        itemListAppoint = Room.databaseBuilder(getActivity(), AppointDatabase.class,"appoint")
                .allowMainThreadQueries().build().appointDao().getDoctorAppointmentByStatus(getSharedData("mobile"),"1");
        if(!itemListAppoint.isEmpty()){
            appointAdapter = new AppointAdapter(getActivity(),itemListAppoint,DoctorProfileFragment.this,"2");
            recyclerUpcoming.setAdapter(appointAdapter);
            recyclerUpcoming.setLayoutManager(layoutManagerUpcoming);
            recyclerUpcoming.setVisibility(View.VISIBLE);
        }else{
            llNoUpcoming.setVisibility(View.VISIBLE);
        }

        nsvProfile.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
        nsvProfile.setVisibility(View.VISIBLE);
        cvDetails.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_right_in));
        cvDetails.setVisibility(View.VISIBLE);

        return view;
    }

    private String getSharedData(String key){
        return sharedPreferences.getString(key,"");
    }

    @Override
    public void onAcademyVideoReady() {

    }

    @Override
    public void onAccept(@NotNull AppointEntity appointEntity) {

    }

    @Override
    public void onReject(@NotNull AppointEntity appointEntity) {

    }

    @Override
    public void onCancel(@NotNull final AppointEntity appointEntity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Are you sure you want to cancel the appointment?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Room.databaseBuilder(getActivity(),AppointDatabase.class,"appoint")
                        .allowMainThreadQueries().build().appointDao().updateAppointmentStatus("2",appointEntity.getTimeStamp());
                itemListAppoint.remove(appointEntity);
                appointAdapter.notifyDataSetChanged();
                SmsManager smsManager = (SmsManager) SmsManager.getDefault();
                smsManager.sendTextMessage(appointEntity.getAppoint_by(),null,
                        "Due to unavailability your appointment has been rejected by the doctor!\nHospital : "+appointEntity.getHospital_name()+"\nDoctor : "+sharedPreferences.getString("name","")+"\nSlot : "+appointEntity.getAppoint_time()+"\nPlease try again with some other time slot,\nTeam Baymax",
                        null,null);
                if(itemListAppoint.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    llNoUpcoming.setVisibility(View.VISIBLE);
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
    public void onDirections(@NotNull HospitalEntity hospitalEntity) {

    }

    @Override
    public void onContact(@NotNull HospitalEntity hospitalEntity) {

    }
}
