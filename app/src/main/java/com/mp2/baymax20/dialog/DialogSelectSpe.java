package com.mp2.baymax20.dialog;

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


public class DialogSelectSpe extends DialogFragment {

    private OnSpecializationInterface onSpecializationInterface;
    private String hospital;
    public DialogSelectSpe(OnSpecializationInterface onSpecializationInterface, String hospital) {
        this.onSpecializationInterface = onSpecializationInterface;
        this.hospital = hospital;
    }

    private Spinner spnSpe;
    private ArrayAdapter adpSpe;
    private TextView txtBack,txtSelect;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_select_spe, container, false);

        spnSpe = view.findViewById(R.id.spnSSSpe);
        txtBack = view.findViewById(R.id.txtSSBack);
        txtSelect = view.findViewById(R.id.txtSSSelect);

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
                dismiss();
            }
        });

        txtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spe = spnSpe.getSelectedItem().toString();

                if(spnSpe.getSelectedItemPosition() == 0){
                    makeErrorToast("Select Specialization",null,"");
                    spnSpe.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else{
                    onSpecializationInterface.onSelected(spe,hospital);
                    dismiss();
                }
            }
        });


        return view;
    }

    public interface OnSpecializationInterface{
        void onSelected(String spe,String hospital);
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
