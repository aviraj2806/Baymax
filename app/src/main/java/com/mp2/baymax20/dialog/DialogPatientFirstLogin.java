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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.baymax20.R;
import com.mp2.baymax20.activity.AuthActivity;

import java.util.Objects;

public class DialogPatientFirstLogin extends DialogFragment {

    private OnPatientFirstLogin onPatientFirstLogin;
    public DialogPatientFirstLogin(OnPatientFirstLogin onPatientFirstLogin) {
        this.onPatientFirstLogin = onPatientFirstLogin;
    }

    private EditText etMobile;
    private TextView txtBack,txtSave;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_patient_first_login, container, false);

        etMobile = view.findViewById(R.id.etSosMobile);
        txtSave = view.findViewById(R.id.txtPFSave);
        txtBack = view.findViewById(R.id.txtPFBack);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

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
                String mobile = etMobile.getText().toString().trim();

                if(mobile.isEmpty()){
                    makeErrorToast("Enter Mobile Number",null,"");
                    etMobile.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(mobile.length()<10){
                    makeErrorToast("Invalid Mobile Number",etMobile,"Mobile No.(10 Digit)");
                }else{
                    onPatientFirstLogin.onSavePFDetails(mobile);
                    dismiss();
                }
            }
        });

        return view;
    }

    public interface OnPatientFirstLogin{
        void onSavePFDetails(String sosNumber);
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
}
