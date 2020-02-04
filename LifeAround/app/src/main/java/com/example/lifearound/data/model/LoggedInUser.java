package com.example.lifearound.data.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.content.ContextWrapper;

import android.content.res.Resources;
import android.util.Base64;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@IgnoreExtraProperties
public class LoggedInUser {

    @Exclude
    public String userId;

    public String displayName;
    public String status;
    public int socialPoints;
    public String longitude;
    public String latitude;
    @Exclude
    public Bitmap picture;
    public String imgStr;
    public ArrayList<String> friends;


    public LoggedInUser(){}
    public LoggedInUser(String userId, String displayName) {
        friends=new ArrayList<String>();
        this.userId = userId;
        this.displayName = displayName;
        this.status="";
        this.socialPoints=0;
        longitude="00.0000";
        latitude="00.0000";
        imgStr="";
    }

    @Exclude
    public String getUserId() {
        return userId;
    }
    @Exclude
    public String getDisplayName() {
        return displayName;
    }
    @Exclude
    public String getStatus()
    {
        return status;
    }
    @Exclude
    public int getSocialPoints()
    {
        return socialPoints;
    }
    @Exclude
    public void setStatus(String s)
    {
        this.status=s;
    }
    @Exclude
    public void setSocialPoints(int sp)
    {
        this.socialPoints=sp;
    }
    @Exclude
    public void addSocialPoints(int value)
    {
        this.socialPoints += value;
    }
    @Exclude
    public String getLongitude()
    {
        return longitude;
    }
    @Exclude
    public String getLatitude()
    {
        return latitude;
    }
    @Exclude
    public void setLongitude(String longitude)
    {
        this.longitude=longitude;
    }
    @Exclude
    public void setLatitude(String latitude)
    {
        this.latitude=latitude;
    }
    @Exclude
    public Bitmap getPicture()
    {
        if (imgStr!="") {
            byte[] decodedBytes = Base64.decode(imgStr.substring(imgStr.indexOf(",") + 1), Base64.URL_SAFE);
            picture= BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            return picture;
        }
        else return null;
    }
    @Exclude
    public void setPicture(Bitmap pic)
    {
        this.picture=pic;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        byte[] byteArray = bao.toByteArray();
        imgStr = Base64.encodeToString(byteArray, Base64.URL_SAFE);

    }
    @Exclude
    public void setPictureStr(String pic)
    {
        imgStr = pic;
        byte[] decodedBytes = Base64.decode(imgStr.substring(imgStr.indexOf(",") + 1), Base64.URL_SAFE);
        picture= BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

    }
    @Exclude
    public void addFriend(String uid)
    {
        if(friends==null)
            friends=new ArrayList<String>();
        if(uid!=null)
            friends.add(uid);
    }
}
