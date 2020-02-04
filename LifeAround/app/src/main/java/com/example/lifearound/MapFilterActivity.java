package com.example.lifearound;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lifearound.data.MapEventsData;

public class MapFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_filter);

        final EditText etTitle=(EditText) findViewById(R.id.filter_title);
        final EditText etDate=(EditText) findViewById(R.id.filter_date);
        final EditText etDistance=(EditText) findViewById(R.id.filter_distance);
        Button btnFilter = (Button) findViewById(R.id.btn_filter);
        Button btnFilterDefault = (Button) findViewById(R.id.btn_filter_default);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title=etTitle.getText().toString();
                String date=etDate.getText().toString();
                String distance=etDistance.getText().toString();
                MapEventsData.getInstance().setFilter(title,date,distance);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        btnFilterDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MapEventsData.getInstance().setFilter("","","");
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }
}
