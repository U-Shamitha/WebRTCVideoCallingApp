package com.mad_lab.webrtcvideocallapp;

import android.net.Uri;

public class Users {
    public String userName;
    public String profileUri;
    public String userPhone;
    public String userEmail;

    public Users(){

    }
    public Users(String profileUri,String userName){

        this.userName = userName;
        this.profileUri = profileUri;
        this.userEmail = "userEmail@gmail.com";
        this.userPhone = "userPhone";
    }

    public Users(String profileUri, String userName, String userEmail, String userPhone){
        this.profileUri = profileUri;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }


}
