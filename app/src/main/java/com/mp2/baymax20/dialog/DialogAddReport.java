package com.mp2.baymax20.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.icu.util.ValueIterator;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mp2.baymax20.R;
import com.mp2.baymax20.databse.ReportEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import static android.app.Activity.RESULT_OK;


public class DialogAddReport extends DialogFragment {

    private OnReportAdded onReportAdded;
    public DialogAddReport(OnReportAdded onReportAdded) {
        this.onReportAdded = onReportAdded;
    }

    private TextView txtBack,txtSave;
    private ImageView imgReport,imgPres;
    private EditText etHistory;
    boolean isReport = false;
    boolean isPres = false;
    String reportUrl = "noImage";
    String presUrl = "noImage";
    private SharedPreferences sharedPreferences;
    private String pictureFilePath;
    private String deviceIdentifier;
    private String uploadedImage = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_report, container, false);

        txtBack = view.findViewById(R.id.txtAddReportBack);
        txtSave = view.findViewById(R.id.txtNewSave);
        etHistory = view.findViewById(R.id.etNewHistory);
        imgPres = view.findViewById(R.id.imgNewPrescription);
        imgReport = view.findViewById(R.id.imgNewReport);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String history = etHistory.getText().toString().trim();
                if(!isReport){
                    makeErrorToast("Select Report",null,"");
                    imgReport.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(!isPres){
                    makeErrorToast("Select Prescription",null,"");
                    imgPres.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.shake));
                }else if(history.isEmpty()){
                    makeErrorToast("Enter History",etHistory,"History cannot be empty.");
                }else{
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyYY-MM-dd HH:mm:ss");
                    String timeStamp = simpleDateFormat.format(calendar.getTime());
                    ReportEntity reportEntity = new ReportEntity(timeStamp,reportUrl,presUrl,sharedPreferences.getString("mobile",""),history);
                    onReportAdded.onReportAdded(reportEntity);
                    dismiss();
                }
            }
        });

        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        imgPres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        return view;
    }

    public interface OnReportAdded{
        void onReportAdded(ReportEntity reportEntity);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pictureFilePath = cursor.getString(columnIndex);
            cursor.close();
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                imgReport.setImageURI(Uri.fromFile(imgFile));
                addToCloudStorage(saveBitmapToFile(imgFile),"1");
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pictureFilePath = cursor.getString(columnIndex);
            cursor.close();
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                imgPres.setImageURI(Uri.fromFile(imgFile));
                addToCloudStorage(saveBitmapToFile(imgFile),"2");
            }
        }
    }

    private void addToCloudStorage(File f, final String file) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = deviceIdentifier + picUri.getLastPathSegment();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference uploadeRef = storageRef.child(cloudFilePath);

        uploadeRef.putFile(picUri).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                makeErrorToast("Image Upload Failed.", null, "");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        makeErrorToast("Image uploaded.", null, "");
                        progressDialog.dismiss();
                        uploadedImage = uri.toString();
                        if(file.equals("1")){
                            reportUrl = uploadedImage;
                            isReport = true;
                        }else{
                            presUrl = uploadedImage;
                            isPres = true;
                        }
                    }
                });
            }
        });
    }

    public File saveBitmapToFile(File file) {
        try {

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            final int REQUIRED_SIZE = 75;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


}
