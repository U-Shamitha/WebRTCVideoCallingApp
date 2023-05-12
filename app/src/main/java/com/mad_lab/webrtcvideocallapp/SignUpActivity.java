package com.mad_lab.webrtcvideocallapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseStorage storage;

    ImageButton profileImg;
    ActivityResultLauncher<String> launcher;

    private EditText name;
    private EditText email;
    private EditText password;
    Button signupButton;
    TextView loginRedirectText;
    Boolean imageSelected;
    Uri profileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if(getApplicationContext().getSharedPreferences("check_login", Context.MODE_PRIVATE).getBoolean("Login", false)){
            if(getApplicationContext().getSharedPreferences("check_otp", Context.MODE_PRIVATE).getBoolean("otp", false)){
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }else{
                startActivity(new Intent(SignUpActivity.this, OTPVerification.class));
            }
        }


        // database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        //sign up
        auth = FirebaseAuth.getInstance();
        signupButton = findViewById(R.id.signup_button);
        profileImg = findViewById(R.id.profile_image);
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        profileUri = result;
                        profileImg.setImageURI(result);
                        Toast.makeText(SignUpActivity.this, String.valueOf(profileUri), Toast.LENGTH_SHORT).show();
                        imageSelected = !(String.valueOf(profileUri).equals(null) || String.valueOf(profileUri).equals(""));
                    }
                });
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        imageSelected = false;
        profileUri = null;

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = name.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(!imageSelected){
                    profileImg.setLongClickable(true);
                    profileImg.performLongClick();
                }
                else if(userName.isEmpty()){
                    name.setError("Name cannot be empty");
                }
                else if(userEmail.isEmpty()){
                    email.setError("Email cannot be empty");
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    email.setError("Please enter valid email");
                }
                else if(pass.isEmpty()){
                    password.setError("Password cannot be empty");
                }
                else if(pass.length()<6){
                    password.setError("Password should be at least 6 characters long");
                }
                else{

                    ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
                    progressDialog.setMessage("Signing up");
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    //store profile image in cloud storage
                    final StorageReference storageRef = storage.getReference()
                            .child("profileImage");
                    storageRef.child(userEmail.split("@")[0]).putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //get uri
                            storageRef.child(userEmail.split("@")[0]).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Toast.makeText(SignUpActivity.this, "uri"+uri, Toast.LENGTH_SHORT).show();

                                    DatabaseReference ref = database.getReference("Users");

                                    //store user details and uri
                                    ref.child(userEmail.split("@")[0]).setValue(new Users(uri.toString(), userName))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

//                                                    Toast.makeText(SignUpActivity.this, "usr details uploaded", Toast.LENGTH_SHORT).show();

                                                    //create user
                                                    auth.createUserWithEmailAndPassword(userEmail, pass)
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if(progressDialog.isShowing()) {
                                                                        progressDialog.dismiss();
                                                                    }
                                                                    Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                }
                                                            });

                                                }else{
                                                    Toast.makeText(SignUpActivity.this,"SignUp Failed"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                }
                            });

                        }
                    });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });


    }

}


