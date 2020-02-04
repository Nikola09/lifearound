package com.example.lifearound;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifearound.data.MapEventsData;
import com.example.lifearound.data.model.MapEvent;

public class EventInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        //assert getSupportActionBar() != null;   //null check
        //getActionBar().setDisplayHomeAsUpEnabled(true);   //show back button


        int position=-1;
        boolean filtered=false;
        try{
            Intent listIntent=getIntent();
            Bundle eventBundle =listIntent.getExtras();
            position=eventBundle.getInt("position");
            filtered=eventBundle.getBoolean("filtered");
        } catch(Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }
        if(position >= 0 && !filtered){
            MapEvent event= MapEventsData.getInstance().getEvent(position);
            TextView twName=findViewById(R.id.title);
            twName.setText(event.getName());
            TextView twOrg=findViewById(R.id.organizer);
            twOrg.setText(event.getOrganizer());
            TextView twStartTime=findViewById(R.id.starttime);
            twStartTime.setText(event.getStartTimeStr());
            TextView twEndTime=findViewById(R.id.endtime);
            twEndTime.setText(event.getEndTimeStr());
            TextView twDesc=findViewById(R.id.description);
            twDesc.setText(event.getDesc());
        }
        else if (position >= 0 && filtered)
        {
            MapEvent event= MapEventsData.getInstance().getFilteredEvent(position);
            TextView twName=findViewById(R.id.title);
            twName.setText(event.getName());
            TextView twOrg=findViewById(R.id.organizer);
            twOrg.setText(event.getOrganizer());
            TextView twStartTime=findViewById(R.id.starttime);
            twStartTime.setText(event.getStartTimeStr());
            TextView twEndTime=findViewById(R.id.endtime);
            twEndTime.setText(event.getEndTimeStr());
            TextView twDesc=findViewById(R.id.description);
            twDesc.setText(event.getDesc());
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
