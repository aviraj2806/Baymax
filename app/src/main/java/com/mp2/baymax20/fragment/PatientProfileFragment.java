package com.mp2.baymax20.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
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


public class PatientProfileFragment extends Fragment implements NewAdapter.OnAcademyVideoClick, AppointAdapter.OnAppointStatusControl {

    public PatientProfileFragment() {


    }

    private TextView txtName,txtMobile,txtEmail,txtSos;
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
        View view = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        txtEmail = view.findViewById(R.id.txtPatientEmail);
        txtName = view.findViewById(R.id.txtPatientName);
        txtMobile = view.findViewById(R.id.txtPatientMobile);
        txtSos = view.findViewById(R.id.txtPatientSos);
        imgUser = view.findViewById(R.id.imgPatientProfile);
        imgLogOut = view.findViewById(R.id.imgPatientOut);
        nsvProfile = view.findViewById(R.id.nsvPatientProfile);
        cvDetails = view.findViewById(R.id.cvDetailsPatient);
        llNoUpcoming = view.findViewById(R.id.llNoPatientAppoint);
        recyclerUpcoming = view.findViewById(R.id.recyclerUpcomingPatient);
        layoutManagerUpcoming = new LinearLayoutManager(getActivity());
        layoutManagerUpcoming.setOrientation(LinearLayoutManager.HORIZONTAL);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerNewPatient);
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
        txtSos.setText("SOS : "+getSharedData("sos"));

        Picasso.get().load(getSharedData("image")).error(R.drawable.avatar).into(imgUser);

        newList.add("lMr6TXWN5Pk");
        newList.add("iXfJlrbNzQ4");
        newList.add("82_ZOSX9VtM");
        newList.add("0ovmGjcULBc");
        newList.add("O9Zyrd_vLqo");
        newAdapter = new NewAdapter(getActivity(),this,newList);
        recyclerView.setAdapter(newAdapter);
        recyclerView.setLayoutManager(layoutManager);

        itemListAppoint = Room.databaseBuilder(getActivity(), AppointDatabase.class,"appoint")
                .allowMainThreadQueries().build().appointDao().getPatientAppointmentByStatus(getSharedData("mobile"),"1");
        itemListAppoint.addAll(Room.databaseBuilder(getActivity(), AppointDatabase.class,"appoint")
                .allowMainThreadQueries().build().appointDao().getPatientAppointmentByStatus(getSharedData("mobile"),"0"));
        if(!itemListAppoint.isEmpty()){
            appointAdapter = new AppointAdapter(getActivity(),itemListAppoint,PatientProfileFragment.this,"1");
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
                smsManager.sendTextMessage(sharedPreferences.getString("mobile",""),null,
                        "Your Appointment has been successfully canceled!\nHospital : "+appointEntity.getHospital_name()+"\nSlot : "+appointEntity.getAppoint_time()+"\nThank You,\nTeam Baymax",
                        null,null);
                if(itemListAppoint.isEmpty()){
                    recyclerUpcoming.setVisibility(View.GONE);
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
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                hospitalEntity.getDirections()
        ));
        startActivity(intent);
    }

    @Override
    public void onContact(@NotNull HospitalEntity hospitalEntity) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(
                "tel:"+hospitalEntity.getContact()
        ));
        startActivity(intent);
    }
}
