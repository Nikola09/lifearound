package com.example.lifearound;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.lifearound.data.MapEventsData;
import com.example.lifearound.data.UsersData;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyService extends IntentService implements LocationListener {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.lifearound.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.lifearound.extra.PARAM2";
    private boolean run = true;
    private String lat = null;
    private String lon = null;
    private LocationManager locationManager = null;
    private Intent local;
    private int nearEventsCount;
    private int lastNearEventCount=0;
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;



    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            while (run) {
                try {
                    if (lat != null && lon != null) {
                        synchronized (this) {
                            UsersData.getInstance().updateMyLocation(lat,lon);


                            nearEventsCount= MapEventsData.getInstance().getCloseEvents(lat,lon).size();
                            if (lastNearEventCount < nearEventsCount)
                            {
                                //NOTIFY
                                notificationManager.notify(12, builder.build());
                            }
                            lastNearEventCount=nearEventsCount;
                        }

                    }
                    this.sendBroadcast(local);
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
    }
    public static final String ACTION_STRING_SERVICE = "ToService";
    public static final String ACTION_STRING_ACTIVITY = "ToActivity";
    public static final String STOP = "STOP";

    // Create a broadcast receiver
    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getExtras().get("cmd").toString();
            if (cmd.equals(STOP))
                run = false;
            Log.d("Service", "Sending broadcast to activity");
        }
    };
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service", "onCreate");
    //register the receiver
        if (serviceReceiver != null) { //for sending data to MapFragment and redrawing it
//Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_SERVICE"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_SERVICE);
//Map the intent filter to the receiver
            registerReceiver(serviceReceiver, intentFilter);
            local = new Intent();
            local.setAction("com.hello.action");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ;
        }
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,0);
        builder = new NotificationCompat.Builder(this, "name")
                .setSmallIcon(R.drawable.life_pic)
                .setContentTitle("LifeAround")
                .setContentText("There is an event near you!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager = NotificationManagerCompat.from(this);//getSystemService(NOTIFICATION_SERVICE);


        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");
        //Unregister the receiver
        unregisterReceiver(serviceReceiver);
        UsersData.getInstance().updateMyLocation("00.0000","00.0000");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.removeUpdates(this);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_ACTIVITY"
    public void sendBroadcastToActivity(Intent new_intent) {
        /*new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);*/
    }


    @Override
    public void onLocationChanged(Location loc) {
        synchronized (this) {
            lat = String.valueOf(loc.getLatitude());

            lon = String.valueOf(loc.getLongitude());

            Intent new_intent = new Intent();
            new_intent.putExtra("lat", lat);
            new_intent.putExtra("lon", lon);
            sendBroadcastToActivity(new_intent);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
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
}

        //Intent stop=new Intent(Service.ACTION_STRING_SERVICE);
        //stop.putExtra("cmd",Service.STOP);
        //sendBroadcast(stop);