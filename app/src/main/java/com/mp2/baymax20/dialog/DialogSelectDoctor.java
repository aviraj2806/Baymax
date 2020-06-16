package com.mp2.baymax20.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.mp2.baymax20.R;
import com.mp2.baymax20.adapter.DoctorAdapter;
import com.mp2.baymax20.databse.UserDatabase;
import com.mp2.baymax20.databse.UserEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DialogSelectDoctor extends DialogFragment implements DoctorAdapter.OnDoctorSelectInterface, DialogSelectTime.OnSlotConfirmedInterface {

    private String spe,hospital;
    private OnRequestAppointment onRequestAppointment;
    public DialogSelectDoctor(String spe, String hospital, OnRequestAppointment onRequestAppointment) {
        this.spe = spe;
        this.hospital = hospital;
        this.onRequestAppointment = onRequestAppointment;
    }

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DoctorAdapter doctorAdapter;
    private List<UserEntity> itemList;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            int w = ViewGroup.LayoutParams.MATCH_PARENT;
            int h = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(w,h);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.notify_back));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_select_doctor, container, false);

        recyclerView = view.findViewById(R.id.recyclerDoctor);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setVisibility(View.GONE);

        itemList = Room.databaseBuilder(getActivity(), UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().getDoctorByHospitalAndSpe(spe,hospital);
        itemList.add(new UserEntity("dummy","Doctor","Nidhi Patel","dummy","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/doctor1.jpg?alt=media&token=a6e99b87-c83c-414a-8155-40d31da1111e",
                "dummy",spe,hospital,"dummy"));
        itemList.add(new UserEntity("dummy","Doctor","Suresh Verma","dummy","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/doctor2.jpg?alt=media&token=ca2dc9d1-9fdc-42c2-b82c-81182969877e",
                "dummy",spe,hospital,"dummy"));
        itemList.add(new UserEntity("dummy","Doctor","Rahul Joshi","dummy","https://firebasestorage.googleapis.com/v0/b/baymax-3f757.appspot.com/o/doctor3.jpg?alt=media&token=d241651c-2ec3-4e75-a67b-66d6494e6795",
                "dummy",spe,hospital,"dummy"));

        doctorAdapter = new DoctorAdapter(getActivity(),itemList,DialogSelectDoctor.this);
        recyclerView.setAdapter(doctorAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
        recyclerView.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onDoctorSelected(@NotNull UserEntity userEntity) {
        DialogFragment dialogFragment = new DialogSelectTime(userEntity,DialogSelectDoctor.this);
        dialogFragment.setCancelable(false);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"SelectSlot");
    }

    @Override
    public void onSlotSlected(UserEntity doctor, String slot) {
        onRequestAppointment.onRequestAppointment(doctor,slot);
        dismiss();
    }

    public interface OnRequestAppointment{
        void onRequestAppointment(UserEntity doctor, String slot);
    }
}
