package com.mad_lab.webrtcvideocallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;


public class MainActivity extends AppCompatActivity {

    Button logout;

    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout_button);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("check_login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPref.edit();


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("Login",false);
                editor.apply();
                Toast.makeText(MainActivity.this, "logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        tabLayout = findViewById(R.id.tab);
        viewPager = findViewById(R.id.viewPager);

        ViewPagerExploreAdapter viewPagerExploreAdapter= new ViewPagerExploreAdapter(MainActivity.this);
        viewPagerExploreAdapter.addFragment(new ProfileFragment(), "Profile");
        viewPagerExploreAdapter.addFragment(new CallFragment(), "Call");

        viewPager.setAdapter(viewPagerExploreAdapter);
        viewPager.setCurrentItem(0);

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(viewPagerExploreAdapter.getTabTitle(position));
                    }
                }
        ).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        String userEmail = sharedPref.getString("userEmail","");

        ref.child("Users").orderByKey().equalTo(userEmail.split("@")[0])
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot user : snapshot.getChildren()){
                                Users currentUser = user.getValue(Users.class);
                                startService(currentUser.userEmail, currentUser.userName);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DatabaseReference online_status_all_users = database.getReference("onlineStatuses");
        online_status_all_users.child(userEmail.split("@")[0]).setValue("online");
        online_status_all_users.child(userEmail.split("@")[0]).onDisconnect().setValue("offline");


    }

    public void startService(String userID, String userName){

        Application application = getApplication(); // Android's application context
        long appID = 1818078310;   // yourAppID
        String appSign = "c3806677874dd4812d0b950eeb67dea9c4bd5b1e6bc83696085bbc2834adddf5";  // yourAppSign

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(application, appID, appSign, userID, userName,callInvitationConfig);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

}