package com.mad_lab.webrtcvideocallapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class RecyclerUserAdapter extends RecyclerView.Adapter<RecyclerUserAdapter.ViewHolder> {

    Context context;
    Application application;
    ArrayList<Users> arrUsers;

    public RecyclerUserAdapter(Context context, ArrayList<Users> arrUsers){
        this.context = context;
        this.arrUsers = arrUsers;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(arrUsers.get(position).userName);
//        holder.email.setText(arrUsers.get(position).userEmail);
//        holder.phone.setText(arrUsers.get(position).userPhone);
        Picasso.get().load(arrUsers.get(position).profileUri).into(holder.img);
        holder.videoCallBtn.setIsVideoCall(true);
        holder.videoCallBtn.setResourceID("zego_uikit_call");
        holder.videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(arrUsers.get(position).userEmail, arrUsers.get(position).userName)));


    }


    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, email, phone;
        ImageView img;
        ZegoSendCallInvitationButton videoCallBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userName);
//            email = itemView.findViewById(R.id.userEmail);
//            phone = itemView.findViewById(R.id.userPhone);
            img = itemView.findViewById(R.id.profile_image);
            videoCallBtn = itemView.findViewById(R.id.video_call_btn);

        }
    }


}
