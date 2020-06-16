package com.mp2.baymax20.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mp2.baymax20.R;
import com.mp2.baymax20.databse.UserDatabase;
import com.mp2.baymax20.dialog.DialogDoctorFirstLogin;
import com.mp2.baymax20.dialog.DialogPatientFirstLogin;
import com.mp2.baymax20.fragment.DoctorAppointmentFragment;
import com.mp2.baymax20.fragment.DoctorProfileFragment;
import com.mp2.baymax20.fragment.DoctorReportsFragment;
import com.mp2.baymax20.fragment.PatientAppointmentFragment;
import com.mp2.baymax20.fragment.PatientProfileFragment;
import com.mp2.baymax20.fragment.PatientReportFragment;
import com.mp2.baymax20.fragment.PatientSosFragment;

public class DashPatient extends AppCompatActivity implements DialogPatientFirstLogin.OnPatientFirstLogin {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private RelativeLayout rlActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_patient);

        bottomNavigationView = findViewById(R.id.bottomNavigationPatient);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        rlActivity = findViewById(R.id.rlPatient);

        rlActivity.setVisibility(View.GONE);

        boolean isFirst = sharedPreferences.getBoolean("isFirst",false);

        if(isFirst){
            DialogFragment dialogFragment = new DialogPatientFirstLogin(DashPatient.this);
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(),"DoctorFirst");
        }else{
            loadActivity();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.pnav_profile){
                    openProfile();
                    return true;
                }else if(item.getItemId() == R.id.pnav_appoint){
                    openAppoint();
                    return true;
                }else if(item.getItemId() == R.id.pnav_report){
                    openReport();
                    return true;
                }else{
                    openSos();
                    return true;
                }
            }
        });

    }

    private void loadActivity() {
        rlActivity.setVisibility(View.VISIBLE);
        openProfile();
    }

    public void openProfile(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutPatient,new PatientProfileFragment()).commit();
    }

    public void openAppoint(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutPatient,new PatientAppointmentFragment()).commit();
    }

    public void openReport(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutPatient,new PatientReportFragment()).commit();
    }

    public void openSos(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutPatient,new PatientSosFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutPatient);
        if(fragment.getClass().equals(PatientProfileFragment.class)){
            super.onBackPressed();
        }else{
            bottomNavigationView.setSelectedItemId(R.id.pnav_profile);
            openProfile();
        }
    }

    @Override
    public void onSavePFDetails(String sosNumber) {
        Room.databaseBuilder(DashPatient.this,UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().updatePatientSos(sosNumber,sharedPreferences.getString("mobile",""));
        sharedPreferences.edit().putString("sos",sosNumber).apply();
        sharedPreferences.edit().putBoolean("isFirst",false).apply();
        loadActivity();
    }
}
