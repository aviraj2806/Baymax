package com.mp2.baymax20.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.baymax20.R;
import com.mp2.baymax20.activity.AuthActivity;
import com.mp2.baymax20.activity.RegisterActivity;

import java.util.Objects;


public class DialogDoctorFirstLogin extends DialogFragment {

    private OnDoctorFirstLoginDialog onDoctorFirstLoginDialog;
    public DialogDoctorFirstLogin(OnDoctorFirstLoginDialog onDoctorFirstLoginDialog) {
        this.onDoctorFirstLoginDialog = onDoctorFirstLoginDialog;
    }

    private Spinner spnSpe,spnHospital;
    private ArrayAdapter adpSpe,adpHospital;
    private TextView txtSave,txtBack;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_doctor_first_login, container, false);

        spnHospital = view.findViewById(R.id.spnDFHospital);
        spnSpe = view.findViewById(R.id.spnDFSpe);
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        txtBack = view.findViewById(R.id.txtDFBack);
        txtSave = view.findViewById(R.id.txtDFSave);

        adpHospital = ArrayAdapter.createFromResource(getActivity(), R.array.hospital, R.layout.my_spinner_text);
        spnHospital.setAdapter(adpHospital);

        spnHospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnHospital.getSelectedItemPosition() == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.light));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adpSpe = ArrayAdapter.createFromResource(getActivity(), R.array.spe, R.layout.my_spinner_text);
        spnSpe.setAdapter(adpSpe);

        spnSpe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnSpe.getSelectedItemPosition() == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.light));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("isFirst",false)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setMessage("Are you sure?\nThis will result in Log Out.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), AuthActivity.class);
                            startActivity(intent);
                            Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            getActivity().finish();
                        }
                    });
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.create();
                    dialog.show();
                }else{
                    dismiss();
                }
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spe = spnSpe.getSelectedItem().toString();
                String hospital = spnHospital.getSelectedItem().toString();

                if(spnHospital.getSelectedItemPosition() == 0){
                    makeErrorToast("Select Hospital",null,"");
                    spnHospital.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(spnSpe.getSelectedItemPosition() == 0){
                    makeErrorToast("Select Specialization",null,"");
                    spnSpe.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else{
                    onDoctorFirstLoginDialog.onSaveDFDetails(spe,hospital);
                    dismiss();
                }
            }
        });

        return view;
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast, null);
        Toast toast = new Toast(getActivity());
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
            editText.clearFocus();
        }
    }

    public interface OnDoctorFirstLoginDialog{
        void onSaveDFDetails(String spe,String hospital);
    }
}
