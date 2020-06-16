package com.mp2.baymax20.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mp2.baymax20.R;
import com.mp2.baymax20.adapter.AppointAdapter;
import com.mp2.baymax20.adapter.ReportAdapter;
import com.mp2.baymax20.databse.AppointEntity;
import com.mp2.baymax20.databse.ReportDatabase;
import com.mp2.baymax20.databse.ReportEntity;
import com.mp2.baymax20.dialog.DialogAddReport;
import com.mp2.baymax20.dialog.ExpandImageDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class PatientReportFragment extends Fragment implements ReportAdapter.OnReportInterface, DialogAddReport.OnReportAdded {

    public PatientReportFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ReportAdapter reportAdapter;
    private List<ReportEntity> itemList;
    private SharedPreferences sharedPreferences;
    private LinearLayout llNoReport,llReport;
    private TextView txtNewReport;
    private boolean first = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_report, container, false);

        recyclerView = view.findViewById(R.id.recyclerReportPatient);
        layoutManager = new LinearLayoutManager(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        llNoReport = view.findViewById(R.id.llNoReportPatient);
        llReport = view.findViewById(R.id.llReportPatient);
        txtNewReport = view.findViewById(R.id.txtNewReport);

        llReport.setVisibility(View.GONE);
        llNoReport.setVisibility(View.GONE);

        loadReport();

        txtNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogAddReport(PatientReportFragment.this);
                dialogFragment.setCancelable(false);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"AddReport");
            }
        });

        return view;
    }

    private void loadReport() {

        itemList = Room.databaseBuilder(getActivity(), ReportDatabase.class,"report")
                .allowMainThreadQueries().build().reportDao().getAllReportByUser(sharedPreferences.getString("mobile",""));
        if(itemList.isEmpty()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    llNoReport.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                    llNoReport.setVisibility(View.VISIBLE);
                }
            },200);
            llReport.setVisibility(View.GONE);

        }else{
            reportAdapter = new ReportAdapter(getActivity(),itemList,PatientReportFragment.this,"1");
            recyclerView.setAdapter(reportAdapter);
            recyclerView.setLayoutManager(layoutManager);
            llNoReport.setVisibility(View.GONE);

            if(first) {
                first = false;
                recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in));
            }
            llReport.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReportClicked(@NotNull ReportEntity reportEntity) {
        DialogFragment dialogFragment = new ExpandImageDialog(reportEntity.getReport_link());
        dialogFragment.show(getActivity().getSupportFragmentManager(),"ImageExpand");
    }

    @Override
    public void onPresClicked(@NotNull ReportEntity reportEntity) {
        DialogFragment dialogFragment = new ExpandImageDialog(reportEntity.getPres_link());
        dialogFragment.show(getActivity().getSupportFragmentManager(),"ImageExpand");
    }

    @Override
    public void onReportAdded(ReportEntity reportEntity) {
        Room.databaseBuilder(getActivity(),ReportDatabase.class,"report")
                .allowMainThreadQueries().build().reportDao().insertReport(reportEntity);
        loadReport();
    }
}
