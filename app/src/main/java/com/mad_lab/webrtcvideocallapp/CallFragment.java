package com.mad_lab.webrtcvideocallapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CallFragment extends Fragment {

    FirebaseDatabase database;

    ArrayList<Users> arrUsers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences sharedPref = getActivity().getSharedPreferences("check_login", Context.MODE_PRIVATE);
        String userEmail = sharedPref.getString("userEmail","");

        refreshOnlineUsers(userEmail, recyclerView);


        DatabaseReference online_status_all_users = FirebaseDatabase.getInstance().getReference().child("onlineStatuses");
        online_status_all_users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                refreshOnlineUsers(userEmail, recyclerView);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;

    }

    public void refreshOnlineUsers(String userEmail, RecyclerView recyclerView){
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        arrUsers.clear();
        RecyclerUserAdapter adapter = new RecyclerUserAdapter(getContext(), arrUsers, getActivity().getApplication());
        recyclerView.setAdapter(adapter);

        ref.child("Users").orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot user : snapshot.getChildren()) {
                                Users currentUser = user.getValue(Users.class);
                                if(!userEmail.equals(currentUser.userEmail)) {

                                    DatabaseReference online_status_all_users = FirebaseDatabase.getInstance().getReference().child("onlineStatuses");
                                    online_status_all_users.child(currentUser.userEmail.split("@")[0])
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String snooping_status = snapshot.getValue(String.class);
                                                    if(snooping_status.equals("online")){
//                                                        Toast.makeText(getContext(), "" + currentUser.userEmail, Toast.LENGTH_SHORT).show();
                                                        arrUsers.add(new Users(currentUser.profileUri, currentUser.userName, currentUser.userEmail, currentUser.userPhone));
                                                        RecyclerUserAdapter adapter = new RecyclerUserAdapter(getContext(), arrUsers, getActivity().getApplication());
                                                        recyclerView.setAdapter(adapter);
                                                    }
                                                }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


}