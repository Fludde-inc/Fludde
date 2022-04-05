package com.example.fludde;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignupActivity extends AppCompatActivity {
    private EditText etNewUserName;
    private EditText etUserEmail;
    private EditText etNewUserPass;
    private EditText etNewUserPass2;
    private Button btnClear;
    private Button  btnCreate;
    private ImageButton ibAddProfileImage;
    private ImageView ivProfileImage;
    private String  TAG = "SignUp";
    private File photoFile;
    public String currentPhotoPath;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etNewUserName = findViewById(R.id.etNewUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etNewUserPass = findViewById(R.id.etNewUserPass);
        etNewUserPass2 = findViewById(R.id.etNewUserPass2);
        btnClear = findViewById(R.id.btnClear);
        btnCreate = findViewById(R.id.btnCreate);
        ibAddProfileImage = findViewById(R.id.ibAddProfileImage);
        ivProfileImage = findViewById(R.id.ivProfileImage);


        ibAddProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etNewUserName.setText("");
                etUserEmail.setText("");
                etNewUserPass.setText("");
                etNewUserPass2.setText("");

            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etUserEmail.getText().toString().isEmpty())
                {
                    Toast.makeText(SignupActivity.this, "Email address is needed", Toast.LENGTH_SHORT).show();
                    etUserEmail.requestFocus();
                    return;
                }
                if (!etUserEmail.getText().toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(etUserEmail.getText().toString()).matches()) {

                } else {
                    Toast.makeText(SignupActivity.this, "Enter valid Email address !", Toast.LENGTH_SHORT).show();
                    etUserEmail.requestFocus();
                    return;
                }

                if(etNewUserName.getText().toString().isEmpty()){
                    Toast.makeText(SignupActivity.this, "User Name cannot be empty", Toast.LENGTH_SHORT).show();
                    etNewUserName.requestFocus();
                    return;
                }
                if (etNewUserPass.getText().toString().isEmpty()){
                    Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    etNewUserPass.requestFocus();
                    return;
                }
                if (etNewUserPass2.getText().toString().isEmpty()){
                    Toast.makeText(SignupActivity.this, "Please confirm password", Toast.LENGTH_SHORT).show();
                    etNewUserPass2.requestFocus();
                    return;
                }

                if(!etNewUserPass2.getText().toString().equals(etNewUserPass.getText().toString())){
                    etNewUserPass.setText("");
                    etNewUserPass2.setText("");

                    etNewUserPass.requestFocus();
                    Toast.makeText(SignupActivity.this, "Password does not match", Toast.LENGTH_LONG).show();

                    return;
                }
                else {



                    String userName = etNewUserName.getText().toString();
                    String userEmail = etUserEmail.getText().toString();
                    String userPass = etNewUserPass.getText().toString();

                    etNewUserPass.setText("");
                    etNewUserPass2.setText("");
                    etUserEmail.setText("");
                    etNewUserName.setText("");

                    ParseFile profilePic = new ParseFile(photoFile);

                    profilePic.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e!=null)
                            {
                                Log.e("ImageSaving","Issue Saving profile pic");
                            }
                            else{
                                createUser(userName, userEmail, userPass, profilePic);
                            }
                        }
                    });

                }


            }
        });
    }

    private void createUser(String userName, String userEmail, String userPass, ParseFile profilePic) {
//        ParseUser newUser = new ParseUser();
        User userProfile = new User();

        userProfile.setUsername(userName);
        userProfile.setEmail(userEmail);
        userProfile.setPassword(userPass);


        if (profilePic != null) {
            userProfile.setImage(profilePic);
        }
        else
        {

        }




        userProfile.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Sign up issue", e);
                } else {

                    goToLogin();

                    Toast.makeText(SignupActivity.this, "Account was Created, please log in", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }



    private void goToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);

    }
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(currentPhotoPath);
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
//                ImageView ivPreview = (ImageView) findViewById(R.id.ivPostImage);
                ivProfileImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

}