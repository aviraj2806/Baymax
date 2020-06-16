package com.mp2.baymax20.dialog;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.baymax20.R;
import com.mp2.baymax20.databse.UserEntity;

import java.text.DateFormat;
import java.util.Date;


public class DialogSelectTime extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private UserEntity userEntity;
    private OnSlotConfirmedInterface onSlotConfirmedInterface;
    public DialogSelectTime(UserEntity userEntity, OnSlotConfirmedInterface onSlotConfirmedInterface) {
        this.userEntity = userEntity;
        this.onSlotConfirmedInterface = onSlotConfirmedInterface;
    }

    private TextView txtBack,txtConfirm,txtDate;
    private Spinner spnSlot;
    private ArrayAdapter adpSlot;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_select_time, container, false);

        txtBack = view.findViewById(R.id.txtSlotBack);
        txtConfirm = view.findViewById(R.id.txtSlotConfirm);
        txtDate = view.findViewById(R.id.txtSlotDate);
        spnSlot = view.findViewById(R.id.spnSlotTime);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        adpSlot = ArrayAdapter.createFromResource(getActivity(), R.array.time, R.layout.my_spinner_text);
        spnSlot.setAdapter(adpSlot);

        spnSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnSlot.getSelectedItemPosition() == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.light));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });

        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtDate.getText().toString().equals("Select Date")){
                    makeErrorToast("Select Date",null,"");
                    txtDate.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(spnSlot.getSelectedItemPosition() == 0){
                    makeErrorToast("Select Time",null,"");
                    spnSlot.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else{
                    String slot = txtDate.getText().toString()+" "+spnSlot.getSelectedItem().toString()+":00";
                    onSlotConfirmedInterface.onSlotSlected(userEntity,slot);
                    dismiss();
                }
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),DialogSelectTime.this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date d = new Date(year, month, dayOfMonth);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "yyYY-MM-dd");
        String currentDateString = dateFormatter.format(d);
        txtDate.setText(currentDateString);
        txtDate.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    public interface OnSlotConfirmedInterface{
        void onSlotSlected(UserEntity doctor,String slot);
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
