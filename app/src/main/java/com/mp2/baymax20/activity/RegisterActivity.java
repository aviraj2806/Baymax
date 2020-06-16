package com.mp2.baymax20.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.AutoScrollHelper;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.mp2.baymax20.R;
import com.mp2.baymax20.activity.AuthActivity;
import com.mp2.baymax20.databse.UserDatabase;
import com.mp2.baymax20.databse.UserEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextView txtBack, txtReg;
    private ScrollView svReg;
    private EditText etName, etPass, etCPass, etEmail, etMobile;
    private Spinner spnType;
    private ArrayAdapter adpType;
    private ImageView imgUser, imgEdit;
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private String emailPattern = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
    private String pictureFilePath;
    private String deviceIdentifier;
    private String uploadedImage = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtBack = findViewById(R.id.txtRegBack);
        svReg = findViewById(R.id.svReg);
        etName = findViewById(R.id.etRegName);
        etEmail = findViewById(R.id.etRegEmail);
        etMobile = findViewById(R.id.etRegMobile);
        etPass = findViewById(R.id.etRegPass);
        etCPass = findViewById(R.id.etRegCPass);
        imgUser = findViewById(R.id.imgReg);
        imgEdit = findViewById(R.id.imgRegEdit);
        txtReg = findViewById(R.id.txtReg);
        spnType = findViewById(R.id.spnRegType);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        txtBack.setVisibility(View.GONE);
        svReg.setVisibility(View.GONE);

        txtBack.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        svReg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_up_in));
        txtBack.setVisibility(View.VISIBLE);
        svReg.setVisibility(View.VISIBLE);

        adpType = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.type, R.layout.my_spinner_text);
        spnType.setAdapter(adpType);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnType.getSelectedItemPosition() == 0) {
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
                Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTextFocus();
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_PICTURE_CAPTURE);
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEditTextFocus();
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_PICTURE_CAPTURE);
            }
        });

        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String mobile = etMobile.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String cpass = etCPass.getText().toString().trim();
                final String type = spnType.getSelectedItem().toString().trim();

                Pattern pattern = Pattern.compile(emailPattern);
                Matcher matcher = pattern.matcher(email);


                if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || pass.isEmpty() || cpass.isEmpty()) {
                    makeErrorToast("All fields are mandatory", null, "");
                    clearEditTextFocus();
                    svReg.startAnimation(AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.shake));
                } else if (spnType.getSelectedItemPosition() == 0) {
                    makeErrorToast("Select Type", null, "");
                    spnType.startAnimation(AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.shake));
                } else if (name.length() < 3) {
                    clearEditTextFocus();
                    makeErrorToast("Invalid Name", etName, "Name (Min. 3 characters)");
                } else if (!matcher.find()) {
                    clearEditTextFocus();
                    makeErrorToast("Invalid Email", etEmail, "Invalid Email");
                } else if (mobile.length() < 10) {
                    clearEditTextFocus();
                    makeErrorToast("Invalid Mobile", etMobile, "Enter valid 10 digit mobile no.");
                } else if (!cpass.equals(pass)) {
                    clearEditTextFocus();
                    makeErrorToast("Password Mismatch", etPass, "Password Mismatch");
                    makeErrorToast("Password Mismatch", etCPass, "Password Mismatch");
                } else if (pass.length() < 6) {
                    clearEditTextFocus();
                    makeErrorToast("Invalid Password", etPass, "Password (Min. 6 characters)");
                } else {
                    UserEntity isNew = Room.databaseBuilder(RegisterActivity.this, UserDatabase.class, "user")
                            .allowMainThreadQueries().build().userDao().getUserByMobile(mobile);
                    if (isNew == null) {
                        clearEditTextFocus();
                        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                        progressDialog.setTitle("Registration Complete");
                        progressDialog.setMessage("You are being redirected\nto Home Page!");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        if (uploadedImage.equals("")) {
                            uploadedImage = "noImage";
                        }

                        UserEntity userEntity = new UserEntity(mobile, type, name, email, uploadedImage, pass,"","","");
                        Room.databaseBuilder(RegisterActivity.this, UserDatabase.class, "user")
                                .allowMainThreadQueries().build().userDao().insertUser(userEntity);

                        sharedPreferences.edit().putString("mobile", mobile).apply();
                        sharedPreferences.edit().putString("name", name).apply();
                        sharedPreferences.edit().putString("image", uploadedImage).apply();
                        sharedPreferences.edit().putString("email", email).apply();
                        sharedPreferences.edit().putString("type", type).apply();
                        sharedPreferences.edit().putBoolean("isFirst",true).apply();
                        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(mobile, null, "Welcome to Baymax, " + name + "!\nHope we serve you well.", null, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (type.equals("Doctor")) {
                                    Intent intent = new Intent(RegisterActivity.this, DashDoctor.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finish();
                                } else {
                                    Intent intent = new Intent(RegisterActivity.this, DashPatient.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finish();
                                }
                            }
                        }, 1000);

                    } else {
                        clearEditTextFocus();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage("User Already Exists.\nPlease use a different mobile number.");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                etMobile.setText(null);
                                etMobile.setHintTextColor(getResources().getColor(R.color.spinner_light));
                                etMobile.startAnimation(AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.shake));
                            }
                        });
                        dialog.create();
                        dialog.show();
                    }
                }
            }
        });
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(this).inflate(R.layout.toast, null);
        Toast toast = new Toast(this);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.error));
            editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            editText.clearFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            pictureFilePath = cursor.getString(columnIndex);
            cursor.close();
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                imgUser.setImageURI(Uri.fromFile(imgFile));
                addToCloudStorage(saveBitmapToFile(imgFile));
            }
        }
    }

    private void addToCloudStorage(File f) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void clearEditTextFocus() {
        etName.clearFocus();
        etMobile.clearFocus();
        etEmail.clearFocus();
        etPass.clearFocus();
        etCPass.clearFocus();
    }
}
