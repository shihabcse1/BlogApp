package com.ardroidplus.blogapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardroidplus.blogapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {

    ImageView imageViewPost, imageViewUserPost, imageViewCurrentUser;
    TextView textViewPostDescription, textViewPostDateName, textViewPostTitle;
    EditText editTextComment;
    Button buttonAddComment;
    String postKey;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // make the status bar to transparent
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getSupportActionBar().hide();


        // init Views

        imageViewPost = findViewById(R.id.imageView_post_details);
        imageViewUserPost = findViewById(R.id.imageView_user_image_post);
        imageViewCurrentUser = findViewById(R.id.imageView_post_details_current_user);

        textViewPostTitle = findViewById(R.id.textView_post_title);
        textViewPostDateName = findViewById(R.id.textView_post_details_date_name);
        textViewPostDescription = findViewById(R.id.textView_post_details_description);

        editTextComment = findViewById(R.id.editText_post_details_comment);
        buttonAddComment = findViewById(R.id.button_post_details_add_comment);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // now we need to bind all data into the views
        // first we need to get post data
        // we need to sent post details data to the activity
        // then we need to receive post data

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imageViewPost);

        String postTitle = getIntent().getExtras().getString("title");
        textViewPostTitle.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imageViewUserPost);

        String postDescription = getIntent().getExtras().getString("description");
        textViewPostDescription.setText(postDescription);

        // set Comment user image

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imageViewCurrentUser);

        //get post id

        postKey = getIntent().getExtras().getString("postKey");

        String date = timeStampToString(getIntent().getExtras().getLong("postDate"));
        textViewPostDateName.setText(date);

    }

    private String timeStampToString(long time){

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;

    }
}
