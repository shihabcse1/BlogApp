package com.ardroidplus.blogapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ardroidplus.blogapp.Models.Post;
import com.ardroidplus.blogapp.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Dialog popAddPost;
    private ImageView popUpUserImage, popUpPostImage, popUpAddButton;
    private EditText popUpTitle, popUpDescription;
    private ProgressBar popUpProgressBar;
    private static final int PReqCode = 2;
    private static final int REQUEST_CODE = 2;
    private Uri pickedImgUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //initialize
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //if the valid user exists then update Nav Header

        updateNavHeader();

        //init popup
        initializePopup();
        setUpPopUpImageClick();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setUpPopUpImageClick() {
        popUpPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here when image clicked then we need to open the Gallery
                // before opening teh Gallery we need to check if our app have the access of user files
                //Check the Platform(sdk)-->Check the Permission-->Explain the permission-->Request the permission-->Handle the Response
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestPermission();
                } else {
                    openGallery();
                }

            }
        });
    }

    private void checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please accept the required permission", Toast.LENGTH_SHORT).show();

            } else {

                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);

            }
        } else {
            //if everything okay then we have permission to access Gallery
            openGallery();
        }
    }

    private void openGallery() {
        //TODO : Open gallery intend and wait for user pick and Image
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE);
    }

    // when user picked an image ...

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            pickedImgUri = data.getData();
            popUpPostImage.setImageURI(pickedImgUri);
        }
    }

    private void initializePopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.pop_up_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //initialize popup widgets

        popUpUserImage = popAddPost.findViewById(R.id.imageView_popup_user_image);
        popUpPostImage = popAddPost.findViewById(R.id.imageView_cover);
        popUpTitle = popAddPost.findViewById(R.id.editText_title);
        popUpDescription = popAddPost.findViewById(R.id.editText_description);
        popUpProgressBar = popAddPost.findViewById(R.id.progressBar_popup);
        popUpAddButton = popAddPost.findViewById(R.id.imageView_pop_up_add);

        // load Current user profile pic
        Glide.with(Home.this).load(currentUser.getPhotoUrl()).into(popUpUserImage);

        // Add post click Listener
        popUpAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpAddButton.setVisibility(View.INVISIBLE);
                popUpProgressBar.setVisibility(View.VISIBLE);

                // we need to test all the input fields(Title and description) and post image

                if(!popUpTitle.getText().toString().equals("")
                && !popUpDescription.getText().toString().equals("")
                && pickedImgUri != null){

                    //everything is okay no empty or null value
                    // TODO Create Post Object and add it into firebase database

                    // we need to upload Post image first since we will keep reference of post Image into our database

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();
                                    //create Post Object
                                    Post post = new Post(popUpTitle.getText().toString(),
                                            popUpDescription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());

                                    // Add post to firebase Database
                                    
                                    addPost(post);
                                    
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //something goes wrong uploading picture.
                                    showMessage(e.getMessage());
                                    popUpProgressBar.setVisibility(View.INVISIBLE);
                                    popUpAddButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }else{
                    showMessage("You need to fill up all the fields and choose an Image");
                    popUpAddButton.setVisibility(View.VISIBLE);
                    popUpProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post Unique ID and upload post key

        String key = myRef.getKey();
        post.setPostKey(key);


        // add post Data to firebase Database

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added Successfully");
                popUpAddButton.setVisibility(View.VISIBLE);
                popUpProgressBar.setVisibility(View.INVISIBLE);
                popAddPost.dismiss();
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
   public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserImage = headerView.findViewById(R.id.nav_user_photo);

        navUserName.setText(currentUser.getDisplayName());
        navUserMail.setText(currentUser.getEmail());

        //using Glide to load user image
        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserImage);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.nav_sign_out){
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}