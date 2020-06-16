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
import com.mp2.baymax20.fragment.DoctorAppointmentFragment;
import com.mp2.baymax20.fragment.DoctorProfileFragment;
import com.mp2.baymax20.fragment.DoctorReportsFragment;

public class DashDoctor extends AppCompatActivity implements DialogDoctorFirstLogin.OnDoctorFirstLoginDialog {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private RelativeLayout rlActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_doctor);

        bottomNavigationView = findViewById(R.id.bottomNavigationDoctor);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        rlActivity = findViewById(R.id.rlDoctor);

        rlActivity.setVisibility(View.GONE);

        boolean isFirst = sharedPreferences.getBoolean("isFirst",false);

        if(isFirst){
            DialogFragment dialogFragment = new DialogDoctorFirstLogin(DashDoctor.this);
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(),"DoctorFirst");
        }else{
            loadActivity();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.dnav_profile){
                    openProfile();
                    return true;
                }else if(item.getItemId() == R.id.dnav_appoint){
                    openAppoint();
                    return true;
                }else{
                    openReport();
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
                .replace(R.id.frameLayoutDoctor,new DoctorProfileFragment()).commit();
    }

    public void openAppoint(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutDoctor,new DoctorAppointmentFragment()).commit();
    }

    public void openReport(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutDoctor,new DoctorReportsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutDoctor);
        if(fragment.getClass().equals(DoctorProfileFragment.class)){
            super.onBackPressed();
        }else{
            bottomNavigationView.setSelectedItemId(R.id.dnav_profile);
            openProfile();
        }
    }

    @Override
    public void onSaveDFDetails(String spe, String hospital) {
        Room.databaseBuilder(DashDoctor.this, UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().updateDoctorHospital(hospital,sharedPreferences.getString("mobile",""));
        Room.databaseBuilder(DashDoctor.this, UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().updateDoctorSpe(spe,sharedPreferences.getString("mobile",""));
        sharedPreferences.edit().putBoolean("isFirst",false).apply();
        sharedPreferences.edit().putString("spe",spe).apply();
        sharedPreferences.edit().putString("hospital",hospital).apply();
        loadActivity();
    }
}
