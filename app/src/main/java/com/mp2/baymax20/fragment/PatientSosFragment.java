package com.mp2.baymax20.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mp2.baymax20.R;


public class PatientSosFragment extends Fragment {

    public PatientSosFragment() {
        // Required empty public constructor
    }

    private LinearLayout llSos,llSosSend;
    private ImageView imgSos;
    private TextView txtSosNum;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_sos, container, false);

        llSos = view.findViewById(R.id.llSendSos);
        llSosSend = view.findViewById(R.id.llSosDone);
        imgSos = view.findViewById(R.id.imgSos);
        txtSosNum = view.findViewById(R.id.txtSosNum);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        llSosSend.setVisibility(View.GONE);

        imgSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSosNum.setText("SOS number : "+sharedPreferences.getString("sos","9420484497"));
                llSos.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_right_out));
                llSos.setVisibility(View.GONE);
                llSosSend.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
                llSosSend.setVisibility(View.VISIBLE);

                SmsManager smsManager = (SmsManager)SmsManager.getDefault();
                smsManager.sendTextMessage(sharedPreferences.getString("sos","9420484497"),null,
                        "Hello,\nSeems like "+sharedPreferences.getString("name","")+" needs your help.\n" +
                                "Please contact this number soon : "+sharedPreferences.getString("mobile","")+"\nTeam Baymax",null,null);
            }
        });

        return view;
    }
}
