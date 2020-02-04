package com.example.lifearound;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lifearound.data.MapEventsData;
import com.example.lifearound.data.UsersData;
import com.example.lifearound.data.model.MapEvent;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity implements View.OnClickListener {

    String lat;
    String lon;
    public static final int SELECT_COORDINATES=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        lat="";
        lon="";

        final Button finishedButton = (Button)findViewById(R.id.createevent);
        finishedButton.setOnClickListener(this);
        finishedButton.setEnabled(false);
        Button locationButton=findViewById(R.id.newlocation);
        locationButton.setOnClickListener(this);
        EditText titleEditText = findViewById(R.id.addeventtitle);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                finishedButton.setEnabled(s.length()>0);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createevent: {
                EditText etTitle = (EditText) findViewById(R.id.addeventtitle);
                String title=etTitle.getText().toString();
                EditText etDesc=(EditText) findViewById(R.id.addeventdescription);
                String desc=etDesc.getText().toString();
                EditText st=findViewById(R.id.starttime);
                String sst=st.getText().toString();
                String[] ab=sst.split(":");
                EditText sd=findViewById(R.id.startdate);
                String ssd=sd.getText().toString();
                String[] aa=ssd.split("/");
                EditText et=findViewById(R.id.endtime);
                String set=et.getText().toString();
                String[] bb=set.split(":");
                EditText ed=findViewById(R.id.enddate);
                String sed=ed.getText().toString();
                String[] ba=sed.split("/");
                if(ba.length==3 && bb.length==2 && aa.length==3 && ab.length==2 && lat!="" && lon!="")
                {
                    LocalDateTime startTime = LocalDateTime.of(Integer.parseInt(aa[0]), Integer.parseInt(aa[1]), Integer.parseInt(aa[2]), Integer.parseInt(ab[0]), Integer.parseInt(ab[1]));
                    LocalDateTime endTime = LocalDateTime.of(Integer.parseInt(ba[0]), Integer.parseInt(ba[1]), Integer.parseInt(ba[2]), Integer.parseInt(bb[0]), Integer.parseInt(bb[1]));
                    if(startTime.isAfter(LocalDateTime.now()) && endTime.isAfter(startTime)&& aa[0].equalsIgnoreCase(String.valueOf(LocalDateTime.now().getYear())) && aa[0].equalsIgnoreCase(ba[0])) {
                        MapEvent event = new MapEvent(title, desc);
                        event.setEndTime(endTime);
                        event.setStartTime(startTime);
                        event.setLatitude(lat);
                        event.setLongitude(lon);
                        event.setOrganizer(UsersData.getInstance().getMe().getDisplayName());
                        MapEventsData.getInstance().addNewEvent(event);
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                    else Toast.makeText(this, "Failed:Wrong Date or Time", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(this, "Failed:Wrong format or location", Toast.LENGTH_SHORT).show();
                //finish();
                break;
            }
           /* case R.id.editmyplace_cancel_button: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }*/
            case R.id.newlocation: {
                Intent i=new Intent(this,MainActivity.class);
                i.putExtra("state",SELECT_COORDINATES);
                startActivityForResult(i,1);
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        try
        {
            if(resultCode == Activity.RESULT_OK)
            {
                lon = data.getExtras().getString("lon");
                //EditText lonText = findViewById(R.id.editmyplace_lon_edit);
                //lonText.setText(lon);
                lat = data.getExtras().getString("lat");
                //EditText latText = findViewById(R.id.editmyplace_lat_edit);
                //latText.setText(lat);
            }
        }
        catch (Exception e) {
            //TODO: handle exception
        }
    }
}
