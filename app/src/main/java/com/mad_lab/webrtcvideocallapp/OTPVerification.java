package com.mad_lab.webrtcvideocallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {

    FirebaseAuth auth;

    EditText mobileNo_et;
    EditText otp_et;
    Button getOTP;
    Button verify;
    TextView otpStatus;

    String code;
    String mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        auth = FirebaseAuth.getInstance();

        mobileNo_et = findViewById(R.id.mobile_number);
        otp_et = findViewById(R.id.otp);
        getOTP = findViewById(R.id.getOTP_button);
        verify = findViewById(R.id.verify_button);
        otpStatus = findViewById(R.id.otpStatusText);

        verify.setEnabled(false);

        SharedPreferences sharedPref = OTPVerification.this.getSharedPreferences("check_otp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("otp",false);
        editor.apply();

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileNo = mobileNo_et.getText().toString().trim();

                if (mobileNo.isEmpty()) {
                    mobileNo_et.setError("Mobile no. can't be empty");
                } else if (!Patterns.PHONE.matcher(mobileNo).matches()) {
                    mobileNo_et.setError("Enter valid mobile no.");
                }
                else {

                    ProgressDialog progressDialog = new ProgressDialog(OTPVerification.this);
                    progressDialog.setMessage("Sending OTP");
                    progressDialog.setCancelable(true);
                    progressDialog.show();

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber("+91" + mobileNo)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(OTPVerification.this)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    startActivity(new Intent(OTPVerification.this, MainActivity.class));
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(OTPVerification.this, "Something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    code = s;
                                    otpStatus.setText("OTP sent");
                                    otpStatus.setPadding(20, 20, 20, 20);
                                    verify.setEnabled(true);
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            })
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String otp = otp_et.getText().toString().trim();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, otp);

                ProgressDialog progressDialog = new ProgressDialog(OTPVerification.this);
                progressDialog.setMessage("Verifying OTP");
                progressDialog.setCancelable(true);
                progressDialog.show();

                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser userDetails = task.getResult().getUser();
                            Toast.makeText(OTPVerification.this, "Verified", Toast.LENGTH_SHORT).show();
                            editor.putBoolean("otp",true);
                            editor.apply();

                            //store mobile number into realtime database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            SharedPreferences sharedPref2 = OTPVerification.this.getSharedPreferences("check_login",Context.MODE_PRIVATE);
                            String userEmail = sharedPref2.getString("userEmail","");
//                            Toast.makeText(OTPVerification.this, ""+userEmail.split("@")[0], Toast.LENGTH_SHORT).show();
                            database.getReference("Users").child(userEmail.split("@")[0]).child("userEmail").setValue(userEmail);
                            database.getReference("Users").child(userEmail.split("@")[0]).child("userPhone").setValue(mobileNo);
                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            startActivity(new Intent(OTPVerification.this, MainActivity.class));
                        }else{
                            Toast.makeText(OTPVerification.this, "Verfication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}