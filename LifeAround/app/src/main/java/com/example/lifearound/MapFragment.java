package com.example.lifearound;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lifearound.data.MapEventsData;
import com.example.lifearound.data.UsersData;
import com.example.lifearound.data.model.LoggedInUser;
import com.example.lifearound.data.model.MapEvent;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;

import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends AppCompatDialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MapView map=null;
    IMapController mapController=null;

    MyLocationNewOverlay myLocationOverlay;
    static final int PERMISSION_ACCESS_FINE_LOCATION=1;
    public static final int SHOW_MAP=0;
    public static final int CENTER_PLACE_ON_MAP=1;
    public static final int SELECT_COORDINATES=2;
    private HashMap<Marker,Integer> markerPlaceIdMap;
    private int state=0;
    private boolean selCoorsEnabled =false;
    private  GeoPoint placeLoc;
    ItemizedIconOverlay EventsOverlay;
    ItemizedIconOverlay PersonsOverlay;
    IntentFilter ifilter;
    BroadcastReceiver updateUIReciver;
    int FILTER_ACTIVITY = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Context ctx = view.getContext();
        int myState = this.getArguments().getInt("message");
        if(myState>=0 && myState<=2)
            state=myState;

        Button select = (Button) view.findViewById(R.id.select_coord);
        Button cancel = (Button) view.findViewById(R.id.cancel_coord);
        Button filter = (Button) view.findViewById(R.id.btn_map_filter);
        if(state!=SELECT_COORDINATES)
        {
            select.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
            filter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                            Intent mFilter= new Intent(getActivity(),MapFilterActivity.class);
                            startActivityForResult(mFilter,FILTER_ACTIVITY);
                        }
            });
        }
        if(state == SELECT_COORDINATES && !selCoorsEnabled) {
            filter.setVisibility(View.INVISIBLE);
            select.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    selCoorsEnabled=true;
                    Toast.makeText(getActivity(),"Select coordinates",Toast.LENGTH_SHORT).show();
                }
            } );

            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                    getActivity().finish();
                }
            } );
        }
        //getActivity().setContentView(R.layout.fragment_map);
        //Context ctx=getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map=view.findViewById(R.id.map);
        map.setMultiTouchControls(true);

        if(ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        }
        else {
            setupMap();
        }
        mapController = map.getController();
        if(mapController!=null){
            mapController.setZoom(15.0);
            GeoPoint startPoint =new GeoPoint(43.3209,21.8958);
            mapController.setCenter(startPoint);
        }

        ifilter= new IntentFilter();   //for receiving data from service and redrawing
        ifilter.addAction("com.hello.action");
        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                redraw();
            }
        };

        // Inflate the layout for this fragment
        return view;//
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMapFragmentInteraction(uri);
        }
    }
    public void onResume() {
        super.onResume();
        map.onResume();
        if (state ==0)
            getActivity().registerReceiver(updateUIReciver,ifilter);
    }
    public void onPause() {
        super.onPause();
        map.onPause();
        if (state ==0)
            getActivity().unregisterReceiver(updateUIReciver);
    }
    private void setCenterPlaceOnMap() {
        mapController = map.getController();
        if(mapController!=null){
            mapController.setZoom(15.0);
            mapController.animateTo(placeLoc);
        }
    }
    private void setupMap(){
        switch (state){
            case SHOW_MAP:
                setMyLocationOverlay();
                break;
            case SELECT_COORDINATES:
                mapController=map.getController();
                if(mapController!=null){
                    mapController.setZoom(15.0);
                    mapController.setCenter(new GeoPoint(43.3209,21.8958));
                }
                setOnMapClickOverlay();
                break;
            case CENTER_PLACE_ON_MAP:
            default:
                setCenterPlaceOnMap();
                break;
        }
        showEvents();
        showPersons();
    }
    private void setMyLocationOverlay(){
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity().getApplicationContext()),map);//???
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.myLocationOverlay);
        mapController =map.getController();
        if(mapController!=null){
            mapController.setZoom(15.0);
            myLocationOverlay.enableFollowLocation();
        }
    }
    private void setOnMapClickOverlay() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(state == SELECT_COORDINATES && selCoorsEnabled) {
                    String lon = Double.toString(p.getLongitude());
                    String lat = Double.toString(p.getLatitude());
                    Intent locationIntent = new Intent();
                    locationIntent.putExtra("lon", lon);
                    locationIntent.putExtra("lat", lat);
                    if(getActivity() != null)
                    {
                        getActivity().setResult(Activity.RESULT_OK, locationIntent);
                        getActivity().finish();
                    }
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(mReceive);
        map.getOverlays().add(OverlayEvents);
    }

    private void showEvents(){
        if(EventsOverlay != null)
            this.map.getOverlays().remove(EventsOverlay);
        final ArrayList<OverlayItem> items = new ArrayList<>();
        for(int i = 0; i< MapEventsData.getInstance().getFilteredEvents().size(); i++){
            MapEvent myEvent=MapEventsData.getInstance().getFilteredEvents().get(i);
            OverlayItem item=new OverlayItem(myEvent.getName(),myEvent.getDesc(),new GeoPoint(Double.parseDouble(myEvent.getLatitude()), Double.parseDouble(myEvent.getLongitude())));
            item.setMarker(this.getResources().getDrawable(R.drawable.baseline_stars_black_18dp));
            items.add(item);
        }
        EventsOverlay=new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        Intent intent=new Intent(getActivity(),EventInfoActivity.class);
                        intent.putExtra("position",index);
                        intent.putExtra("filtered",true);
                        startActivity(intent);
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {    /////////////////////////////////
                        return true;
                    }
                },getActivity());
        this.map.getOverlays().add(EventsOverlay);
    }
    private void showPersons(){
        if(PersonsOverlay != null)
            this.map.getOverlays().remove(PersonsOverlay);
        final ArrayList<OverlayItem> items = new ArrayList<>();
        for(int i = 0; i< UsersData.getInstance().getUsers().size(); i++){
            LoggedInUser singleUser=UsersData.getInstance().getUsers().get(i);
            OverlayItem item=new OverlayItem(singleUser.getDisplayName(),singleUser.getStatus(),new GeoPoint(Double.parseDouble(singleUser.getLatitude()), Double.parseDouble(singleUser.getLongitude())));

            if(singleUser.getPicture()!=null)
            {

                Bitmap image = Bitmap.createScaledBitmap(singleUser.getPicture(), 128, 128, false);
                Drawable mDrawable=new BitmapDrawable(getResources(),image);
                //Drawable mDrawable = new BitmapDrawable(getResources(), singleUser.getPicture());
                item.setMarker(mDrawable);
            }
            else
            {
                item.setMarker(this.getResources().getDrawable(R.drawable.user));
            }
            items.add(item);
        }
        PersonsOverlay=new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        Intent intent=new Intent(getActivity(),UserInfoActivity.class);
                        //intent.putExtra("position",index);
                        //intent.putExtra("position",index);
                        Bundle userBundle = new Bundle();
                        userBundle.putInt("position", index);
                        userBundle.putBoolean("friends", false);
                        intent.putExtras(userBundle);

                        startActivity(intent);
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {    /////////////////////////////////
                        return true;
                    }
                },getActivity());
        this.map.getOverlays().add(PersonsOverlay);
    }
    private void redraw()
    {
        ArrayList<MyLocationNewOverlay> myLoc=new ArrayList<MyLocationNewOverlay>();
        myLoc.add(myLocationOverlay);
        this.map.getOverlays().retainAll(myLoc);
        //clear
        // setupMap();
        showEvents();
        showPersons();
        this.map.invalidate();
    }

   @SuppressLint("MissingPermission")
   @Override
   public void onRequestPermissionsResult(int requestCode, String permissions[],int[] grantResults) {
       switch(requestCode) {
           case PERMISSION_ACCESS_FINE_LOCATION: {
               if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   setupMap();
               }
               return;
           }
       }
   }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILTER_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                redraw();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMapFragmentInteraction(Uri uri);
    }


}
