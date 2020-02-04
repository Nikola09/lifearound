package com.example.lifearound;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.lifearound.data.MapEventsData;
import com.example.lifearound.data.model.LoggedInUser;
import com.example.lifearound.data.model.MapEvent;
import com.example.lifearound.data.model.Notification;
import com.jakewharton.threetenabp.AndroidThreeTen;


public class MainActivity extends AppCompatActivity
        implements MapFragment.OnFragmentInteractionListener, PeopleFragment.OnListFragmentInteractionListener, EventsFragment.OnListFragmentInteractionListener, AccountFragment.OnFragmentInteractionListener, NewsFragment.OnListFragmentInteractionListener {


    int state;

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction;//= fragmentManager.beginTransaction();
    MapFragment mapFragment = new MapFragment();
    EventsFragment eventsFragment = new EventsFragment();
    PeopleFragment peopleFragment = new PeopleFragment();
    NewsFragment newsFragment = new NewsFragment();
    AccountFragment accountFragment = new AccountFragment();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, mapFragment);
                    fragmentTransaction.show(mapFragment);
                    fragmentTransaction.commit();
                    return true;

                case R.id.navigation_events:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, eventsFragment);
                    fragmentTransaction.show(eventsFragment);
                    fragmentTransaction.commit();
                    return true;

                case R.id.navigation_people:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, peopleFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_news:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, newsFragment);
                    fragmentTransaction.commit();
                    return true;

                case R.id.navigation_account:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_container, accountFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);
        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MapEventsData.getInstance().checkOutdatedEvents();
        state = 0;
        try {
            Intent mapIntent = getIntent();
            Bundle mapBundle = mapIntent.getExtras();
            if (mapBundle != null) {
                state = mapBundle.getInt("state");//0=default ; 2=select coordinates
                if (state != 0) {
                    navView.setVisibility(View.INVISIBLE);

                }
                /*if(state == CENTER_PLACE_ON_MAP) {
                    String placeLat = mapBundle.getString("lat");
                    String placeLon=mapBundle.getString("lon");
                    placeLoc = new GeoPoint(Double.parseDouble(placeLat),Double.parseDouble(placeLon));
                }*/
            } else {
                try {
                    startMyService();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            Log.d("Error", "Error reading state");
        }

        Bundle bundle = new Bundle();//CHANGE
        bundle.putInt("message", state);
        mapFragment.setArguments(bundle);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, mapFragment);
        fragmentTransaction.show(mapFragment);
        fragmentTransaction.commit();
    }

    public void onDestroy() {

        super.onDestroy();
        if (state==0) {
            stopMyService();
        }

    }

    @Override
    public void onMapFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPeopleFragmentInteraction(LoggedInUser item, int position) {
        Bundle userBundle = new Bundle();
        userBundle.putInt("position", position);
        userBundle.putBoolean("friends", true);
        Intent userinfointent = new Intent(MainActivity.this, UserInfoActivity.class);
        userinfointent.putExtras(userBundle);
        startActivity(userinfointent);
    }

    @Override
    public void onNewsFragmentInteraction(Notification item) {

    }

    @Override
    public void onAccountFragmentInteraction(Uri uri) {

    }

    @Override
    public void onEventsFragmentInteraction(MapEvent item, int position) {

        Bundle eventBundle = new Bundle();
        eventBundle.putInt("position", position);
        eventBundle.putBoolean("filtered", false);
        Intent eventinfointent = new Intent(MainActivity.this, EventInfoActivity.class);
        eventinfointent.putExtras(eventBundle);
        startActivity(eventinfointent);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "text";
            String description = "text";
            String CHANNEL_ID="name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /*private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle info = intent.getExtras();
            double lat = Double.parseDouble(info.getString("lat"));
            double lon = Double.parseDouble(info.getString("lon"));
            me.SetLatitude(info.getString("lat"));
            me.SetLongitude(info.getString("lon"));}*/
    private void startMyService() {
        Intent i = new Intent(this, MyService.class);
        startService(i);
    }

    private void stopMyService() {
        Intent stop = new Intent(MyService.ACTION_STRING_SERVICE);
        stop.putExtra("cmd", MyService.STOP);
        sendBroadcast(stop);
    }



}

