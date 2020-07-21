package com.ardroidplus.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ardroidplus.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ImageView imageViewLogin;
    private EditText userMail, userPassword;
    private Button buttonLogin;
    private ProgressBar progressBarLogin;

    private FirebaseAuth mAuth;

    private Intent HomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, com.ardroidplus.blogapp.Activities.HomeActivity.class);

        imageViewLogin = findViewById(R.id.imageView_login);
        userMail = findViewById(R.id.editText_user_mail);
        userPassword = findViewById(R.id.editText_user_password);
        buttonLogin = findViewById(R.id.button_login);
        progressBarLogin = findViewById(R.id.progressBar_login);

        buttonLogin.setVisibility(View.VISIBLE);
        progressBarLogin.setVisibility(View.INVISIBLE);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonLogin.setVisibility(View.VISIBLE);
                progressBarLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString().trim();
                final String password = userPassword.getText().toString().trim();

                if (mail.isEmpty() || password.isEmpty()) {
                    showMessage("Please Verify all fields");
                } else {
                    signIn(mail, password);
                }
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            buttonLogin.setVisibility(View.VISIBLE);
                            progressBarLogin.setVisibility(View.INVISIBLE);
                            updateUI();
                        } else {
                            showMessage(task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();
    }


}