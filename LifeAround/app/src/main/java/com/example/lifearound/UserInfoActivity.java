package com.example.lifearound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifearound.data.UsersData;
import com.example.lifearound.data.model.LoggedInUser;


public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        int position=-1;
        boolean friends=false;
        try{
            Intent listIntent=getIntent();
            Bundle eventBundle =listIntent.getExtras();
            position=eventBundle.getInt("position");
            friends=eventBundle.getBoolean("friends");
        } catch(Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }
        if(position >= 0 && !friends){
            LoggedInUser user= UsersData.getInstance().getUser(position);
            TextView twName=findViewById(R.id.name);
            twName.setText(user.getDisplayName());
            TextView twOrg=findViewById(R.id.status);
            twOrg.setText(user.getStatus());
            TextView twStartTime=findViewById(R.id.sp);
            twStartTime.setText(String.valueOf(user.getSocialPoints()));
            ImageView ivPic=findViewById(R.id.userpicture);
            if(user.getPicture()!=null)
                ivPic.setImageBitmap(user.getPicture());
        }
        else if (position >= 0 && friends)
        {
            LoggedInUser user= UsersData.getInstance().getUserFriend(position);
            TextView twName=findViewById(R.id.name);
            twName.setText(user.getDisplayName());
            TextView twOrg=findViewById(R.id.status);
            twOrg.setText(user.getStatus());
            TextView twStartTime=findViewById(R.id.sp);
            twStartTime.setText(String.valueOf(user.getSocialPoints()));
            ImageView ivPic=findViewById(R.id.userpicture);
            if(user.getPicture()!=null)
                ivPic.setImageBitmap(user.getPicture());
        }
    }
}
