package com.ardroidplus.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ardroidplus.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    ImageView imageViewUserReg;
    static int PReqCode = 1;
    static int REQUEST_CODE = 1;
    Uri pickedImgUri;

    private EditText editTextUserName, editTextUserMail, editTextUserPassword, editTextUserPassword2;
    private Button buttonReg;
    private ProgressBar progressBarLoading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageViewUserReg = findViewById(R.id.imageViewUserRegistration);
        editTextUserName = findViewById(R.id.editTextTextPersonName);
        editTextUserMail = findViewById(R.id.editTextTextPersonMail);
        editTextUserPassword = findViewById(R.id.editTextPassword);
        editTextUserPassword2 = findViewById(R.id.editTextTextPassword2);
        buttonReg = findViewById(R.id.buttonRegister);
        progressBarLoading = findViewById(R.id.progressBar);


        mAuth = FirebaseAuth.getInstance(); //initialize the FirebaseAuth instance.


        progressBarLoading.setVisibility(View.INVISIBLE); // progressbar is invisible first time.

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonReg.setVisibility(View.INVISIBLE);
                progressBarLoading.setVisibility(View.VISIBLE);
                final String name = editTextUserName.getText().toString().trim();
                final String mail = editTextUserMail.getText().toString().trim();
                final String password = editTextUserPassword.getText().toString().trim();
                final String password2 = editTextUserPassword2.getText().toString().trim();

                if (name.isEmpty() || mail.isEmpty() || password.isEmpty() || password2.isEmpty() || !password.equals(password2)) {
                    showMessage("Please Fill up all the fields");
                } else {
                    createUserAccount(name, mail, password);
                }
            }
        });

        imageViewUserReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Platform(sdk)-->Check the Permission-->Explain the permission-->Request the permission-->Handle the Response
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                } else {
                    openGallery();
                }
            }
        });
    }

    private void createUserAccount(final String name, String mail, String password) {
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMessage("Account Created");//Account has been created successfully
                            //After Creating account we need to update profile picture and name
                            updateUserInfo(name, pickedImgUri, mAuth.getCurrentUser());

                        } else {
                            showMessage("Account Creation Failed!" + task.getException());
                        }
                        buttonReg.setVisibility(View.VISIBLE);
                        progressBarLoading.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {
        //First we need to update user's photo to firebase storage and get url
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image uploaded successfully
                //now we can get the image URI
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //URI contains Image URL
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //user info updated successfully
                                            showMessage("Registration Complete");
                                            updateUI();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        //TODO : Open gallery intend and wait for user pick and Image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please accept the required permission", Toast.LENGTH_SHORT).show();

            } else {

                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);

            }
        } else {
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImgUri = data.getData();
            imageViewUserReg.setImageURI(pickedImgUri);
        }
    }
}