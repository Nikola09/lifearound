package com.example.lifearound.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.lifearound.data.model.LoggedInUser;
import com.example.lifearound.data.model.MapEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;

public class MapEventsData {
    private ArrayList<MapEvent> mapEvents;
    private HashMap<String,Integer> mapEventsKeyIndexMapping;
    private DatabaseReference database;
    private static final String FIREBASE_CHILD="map-events";
    private ListUpdatedEventListener updateListener;
    private boolean filtering;
    private String filter_title;
    private String filter_date;
    private String filter_distance;
    public final int new_event_pts=2;
    public final double notify_distance=100; //for test: change it to 2000

    private MapEventsData(){
        filtering=false;
        mapEvents = new ArrayList<MapEvent>();
        mapEventsKeyIndexMapping = new HashMap<String,Integer>(); //TODO CHECK THIS
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
            String mapEventKey=dataSnapshot.getKey();
            if(!mapEventsKeyIndexMapping.containsKey(mapEventKey)){
                MapEvent mapEvent = dataSnapshot.getValue(MapEvent.class);
                mapEvent.key=mapEventKey;
                mapEvents.add(mapEvent);
                mapEventsKeyIndexMapping.put(mapEventKey,mapEvents.size()-1);
                if(updateListener !=null)
                    updateListener.onListUpdated();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String mapEventKey=dataSnapshot.getKey();
            MapEvent mapEvent = dataSnapshot.getValue(MapEvent.class);
            mapEvent.key=mapEventKey;
            if(mapEventsKeyIndexMapping.containsKey(mapEventKey)){
                int index = mapEventsKeyIndexMapping.get(mapEventKey);
                mapEvents.set(index,mapEvent);
            }
            else {
                mapEvents.add(mapEvent);
                mapEventsKeyIndexMapping.put(mapEventKey,mapEvents.size()-1);
            }
            if(updateListener !=null)
                updateListener.onListUpdated();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            String mapEventKey=dataSnapshot.getKey();
            MapEvent mapEvent = dataSnapshot.getValue(MapEvent.class);
            mapEvent.key=mapEventKey;
            if(mapEventsKeyIndexMapping.containsKey(mapEventKey)){
                int index = mapEventsKeyIndexMapping.get(mapEventKey);
                mapEvents.remove(index);
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
        public static final MapEventsData instance=new MapEventsData();

    }

    public static MapEventsData getInstance(){
        return SingletonHolder.instance;
    }

    public ArrayList<MapEvent> getMapEvents() {
        return mapEvents;
    }
    public void addNewEvent(MapEvent event)
    {
        String key = database.push().getKey();
        mapEvents.add(event);
        mapEventsKeyIndexMapping.put(key,mapEvents.size()-1);
        database.child(FIREBASE_CHILD).child(key).setValue(event);
        event.key=key;
        NotificationsData.getInstance().addNewNotification("New event created.");
        UsersData.getInstance().updatePoints(new_event_pts);
    }
    public MapEvent getEvent(int index)
    {
        return mapEvents.get(index);
    }
    public void deleteEvent(int index)
    {
        if (mapEvents.size()!=0 && mapEventsKeyIndexMapping.size()!=0) {
            database.child(FIREBASE_CHILD).child(mapEvents.get(index).key).removeValue();

            mapEvents.remove(index);
            recreateKeyIndexMapping();
        }
    }
    public void updateEvent(int index, String nme, String desc, String lng, String lat, LocalDateTime st,LocalDateTime et){  //TODO CHECK THIS
        MapEvent mapEvent=mapEvents.get(index);
        mapEvent.name=nme;
        mapEvent.description=desc;
        mapEvent.latitude=lat;
        mapEvent.longitude=lng;
        mapEvent.startTime=st;
        mapEvent.endTime=et;
        database.child(FIREBASE_CHILD).child(mapEvent.key).setValue(mapEvent);
    }

    public void setFilter(String title,String date,String distance)
    {
        if (title!="" || date!="" || distance!="")
        {
            filtering=true;
            filter_title=title;
            filter_date=date;
            filter_distance=distance;
        }
        else
        {
            filtering=false;
            filter_title="";
            filter_date="";
            filter_distance="";
        }
    }
    public ArrayList<MapEvent> getFilteredEvents()
    {
        if (!filtering) {
            return getMapEvents();
        }
        else
        {
            ArrayList<MapEvent> list=new ArrayList<MapEvent>();
            LoggedInUser temp_me=UsersData.getInstance().getMe();
            boolean added;
            for (MapEvent singleEvent:mapEvents ) {
                added=false;
                if (singleEvent.getName().contains(filter_title)) {
                    list.add(singleEvent);
                    added=true;
                }
                if (!filter_date.equals(""))
                {
                    String[] str=singleEvent.getStartTimeStr().split(" ");
                    if (!str[0].equals(filter_date) && added)
                    {
                        list.remove(singleEvent);
                        added=false;
                    }
                }
                if (!filter_distance.equals("") && added)
                {
                    double d=Double.valueOf(filter_distance);
                    double distance=calculateDistance(Double.valueOf(temp_me.getLongitude()),Double.valueOf(temp_me.getLatitude()),
                            Double.valueOf(singleEvent.getLongitude()),Double.valueOf(singleEvent.getLatitude()));
                    if (d<distance)
                    {
                        list.remove(singleEvent);
                    }
                }
            }
            return list;
        }
    }
    public MapEvent getFilteredEvent(int index)
    {
        return getFilteredEvents().get(index);
    }

    private void recreateKeyIndexMapping(){
        mapEventsKeyIndexMapping.clear();
        for(int i=0;i<mapEvents.size();i++) {
            mapEventsKeyIndexMapping.put(mapEvents.get(i).key,i);
        }
    }
    public void checkOutdatedEvents()
    {
        for (int i=0;i<mapEvents.size();i++)
        {
            if (mapEvents.get(i).getEndTime().isBefore(LocalDateTime.now())) //ako se zavrzava pre trenutnog vremena, obrisi
            {
                deleteEvent(i);
            }
        }
    }
    public ArrayList<MapEvent> getCloseEvents(String myLat,String myLon)
    {
        ArrayList<MapEvent> retList=new ArrayList<MapEvent>();
        for (MapEvent singleEvent: mapEvents ) {
            double distance=calculateDistance(Double.valueOf(myLon),Double.valueOf(myLat),
                    Double.valueOf(singleEvent.getLongitude()),Double.valueOf(singleEvent.getLatitude()));
            if (distance < notify_distance &&
                    singleEvent.getStartTime().isBefore(LocalDateTime.now()) && singleEvent.getEndTime().isAfter(LocalDateTime.now()))
            {
                retList.add(singleEvent);
            }
        }
        return retList;
    }

    public double calculateDistance(double longitude1, double latitude1,double longitude2, double latitude2) {
        double c =
                Math.sin(Math.toRadians(latitude1)) *
                        Math.sin(Math.toRadians(latitude2)) +
                        Math.cos(Math.toRadians(latitude1)) *
                                Math.cos(Math.toRadians(latitude2)) *
                                Math.cos(Math.toRadians(longitude2) -
                                        Math.toRadians(longitude1));
        c = c > 0 ? Math.min(1, c) : Math.max(-1, c);
        return 3959 * 1.609 * 1000 * Math.acos(c);
    }
}
