package com.example.lifearound.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.example.lifearound.data.model.LoggedInUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class UsersData {
    private ArrayList<LoggedInUser> users;
    private LoggedInUser me;
    ArrayList<LoggedInUser> friends;
    private HashMap<String,Integer> usersKeyIndexMapping;
    private DatabaseReference database;
    private static final String FIREBASE_CHILD="users";
    ListUpdatedEventListener updateListener;
    public final int new_friend_pts=5;

    private UsersData(){
        users = new ArrayList<LoggedInUser>();
        friends=new ArrayList<LoggedInUser>();

        usersKeyIndexMapping = new HashMap<String,Integer>(); //TODO CHECK THIS
        database= FirebaseDatabase.getInstance().getReference();
        database.child(FIREBASE_CHILD).addChildEventListener(childEventListener);
        database.child(FIREBASE_CHILD).addListenerForSingleValueEvent(parentEventListener);
    }
    ValueEventListener parentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(updateListener !=null)
                updateListener.onListUpdated();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String userKey=dataSnapshot.getKey();//HERE
            if(!usersKeyIndexMapping.containsKey(userKey)){//HERE
                LoggedInUser user = dataSnapshot.getValue(LoggedInUser.class);
                user.userId=userKey;
                users.add(user);
                usersKeyIndexMapping.put(userKey,users.size()-1);
                if(updateListener !=null)
                    updateListener.onListUpdated();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String userKey=dataSnapshot.getKey();
            LoggedInUser user = dataSnapshot.getValue(LoggedInUser.class);
            user.userId=userKey;
            if(usersKeyIndexMapping.containsKey(userKey)){ //removed !
                int index = usersKeyIndexMapping.get(userKey);
                users.set(index,user);
            }
            else {
                //users.add(user);
                //usersKeyIndexMapping.put(userKey,users.size()-1);
            }
            if(updateListener !=null)
                updateListener.onListUpdated();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String userKey=dataSnapshot.getKey();
            LoggedInUser user = dataSnapshot.getValue(LoggedInUser.class);
            user.userId=userKey;
            if(!usersKeyIndexMapping.containsKey(userKey)){
                int index = usersKeyIndexMapping.get(userKey);
                users.remove(index);
                recreateKeyIndexMapping();
                if(updateListener !=null)
                    updateListener.onListUpdated();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void setEventListener(ListUpdatedEventListener listener){
        updateListener = listener;
    }
    public interface ListUpdatedEventListener {
        void onListUpdated();
    }

    private static class SingletonHolder{
        public static final UsersData instance=new UsersData();

    }

    public static UsersData getInstance(){
        return SingletonHolder.instance;
    }

    public ArrayList<LoggedInUser> getUsers() {
        return users;
    }
    public void addNewUser(LoggedInUser user)
    {
        //String key = database.push().getKey();
        users.add(user);
        usersKeyIndexMapping.put(user.userId,users.size()-1);
        database.child(FIREBASE_CHILD).child(user.userId).setValue(user);
    }
    public LoggedInUser getUser(int index)
    {
        return users.get(index);
    }
    public void deleteUser(int index)
    {
        database.child(FIREBASE_CHILD).child(users.get(index).userId).removeValue();

        users.remove(index);
        recreateKeyIndexMapping();
    }
    public void updateUser(int index, int socpts, String status, String lng, String lat, Bitmap pic){  //TODO CHECK THIS
        LoggedInUser user=users.get(index);
        user.setSocialPoints(socpts);
        user.setStatus(status);
        user.setLatitude(lat);
        user.setLongitude(lng);
        user.setPicture(pic);
    }

    public void StoreImg(Bitmap bmp) {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        //bmp.recycle();
        byte[] byteArray = bao.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);
        me.setPictureStr(imageB64);
        database.child(FIREBASE_CHILD).child(me.userId).setValue(me);

        /*byte[] decodedBytes = Base64.decode(imageB64.substring(imageB64.indexOf(",")  + 1),Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);*/

    }
    public void setMe(String id)
    {
        database.child(FIREBASE_CHILD).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(LoggedInUser.class);
                me.userId=dataSnapshot.getKey();
                getFriends();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
    }
    public LoggedInUser getMe()
    {
        return me;
    }

    public void addFriend(String f)// OR String id ???
    {

        if (me.friends!=null)
        {
            if (me.friends.contains(f))
                return;
        }
        database.child(FIREBASE_CHILD).child(f).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String friend = dataSnapshot.getKey();//getValue(LoggedInUser.class);
                me.addFriend(friend);
                database.child(FIREBASE_CHILD).child(me.getUserId()).setValue(me);
                NotificationsData.getInstance().addNewNotification("New friend added.");
                updatePoints(new_friend_pts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
    }
    public ArrayList<LoggedInUser> getFriends()
    {
        if (me.friends!=null) {
            for (String fr : me.friends) {
                database.child(FIREBASE_CHILD).child(fr).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        LoggedInUser temp=dataSnapshot.getValue(LoggedInUser.class);
                        temp.userId= dataSnapshot.getKey();
                        boolean matched=false;
                        for (LoggedInUser f:friends ) {
                            if(f.userId == temp.userId)
                                matched=true;
                        }
                        if(!matched)
                                friends.add(temp);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}

                });
            }
        }
        Collections.sort(friends, new Comparator<LoggedInUser>() {
            @Override
            public int compare(LoggedInUser user1, LoggedInUser user2)
            {
                return  user2.socialPoints-user1.socialPoints;
            }
        });
        if (friends.size()>0) {
            me.friends.clear();
            for (LoggedInUser fr : friends) {
                me.friends.add(fr.getUserId());
            }
        }
        return friends;
    }
    public LoggedInUser getUserFriend(int index)
    {
        return friends.get(index);
    }

    public void updateMyLocation(String lat,String lon)
    {
        // set value by property?
        me.setLatitude(lat);
        me.setLongitude(lon);
        database.child(FIREBASE_CHILD).child(me.userId).child("latitude").setValue(lat);
        database.child(FIREBASE_CHILD).child(me.userId).child("longitude").setValue(lon);
    }
    public void updatePoints(int num)
    {
        int tempPoints=me.getSocialPoints()+num;
        me.setSocialPoints(tempPoints);
        database.child(FIREBASE_CHILD).child(me.userId).child("socialPoints").setValue(tempPoints);
    }
    public void updateStatus(String status)
    {
        me.setStatus(status);
        database.child(FIREBASE_CHILD).child(me.userId).child("status").setValue(status);
    }

    private void recreateKeyIndexMapping(){
        usersKeyIndexMapping.clear();
        for(int i=0;i<users.size();i++) {
            usersKeyIndexMapping.put(users.get(i).userId,i);
        }
    }
}
