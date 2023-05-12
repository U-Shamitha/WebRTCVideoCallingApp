package com.mad_lab.webrtcvideocallapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;


public class ProfileFragment extends Fragment {

    FirebaseDatabase database;

    ImageView userImage;
    TextView userName_tv;
    TextView userEmail_tv;
    TextView userPhone_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("check_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String userEmail = sharedPref.getString("userEmail","");

        userImage = view.findViewById(R.id.userImage);
        userName_tv = view.findViewById(R.id.userName);
        userEmail_tv = view.findViewById(R.id.userEmail);
        userPhone_tv = view.findViewById(R.id.userPhone);

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.child("Users").orderByKey().equalTo(userEmail.split("@")[0])
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot user : snapshot.getChildren()){
                                Users currentUser = user.getValue(Users.class);
                                Picasso.get().load(currentUser.profileUri).into(userImage);
                                userName_tv.setText(currentUser.userName);
                                userEmail_tv.setText(userEmail);
                                userPhone_tv.setText(currentUser.userPhone);
//                                startService(currentUser.userEmail, currentUser.userName);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        DatabaseReference online_status_all_users = database.getReference("onlineStatuses");
//        online_status_all_users.child(userEmail.split("@")[0]).setValue("online");
//        online_status_all_users.child(userEmail.split("@")[0]).onDisconnect().setValue("offline");



        return view;
    }

}