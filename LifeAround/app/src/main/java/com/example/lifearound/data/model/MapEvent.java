package com.example.lifearound.data.model;

//import com.google.firebase.database.Exclude;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties ////FIREBASE
public class MapEvent {
    public String name;
    public String description;
    public String longitude;
    public String latitude;
    public String strStartTime;//Date
    public String strEndTime;//Date
    @Exclude
    public LocalDateTime startTime;//Date
    @Exclude
    public LocalDateTime endTime;//Date
    public String organizer;//TODO ADD ORGANIZER(OR JUST PERSON NAME)
    @Exclude  ////FIREBASE
    public String key; ////FIREBASE

    public MapEvent(){}

    public MapEvent(String n,String d){
        name=n;
        description=d;
        organizer="unknown";
        startTime = LocalDateTime.now();
        endTime = LocalDateTime.now();
        latitude="00.0000";
        longitude="00.0000";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        strStartTime=startTime.format(formatter);
        strEndTime=endTime.format(formatter);
    }
    @Exclude
    public String getName()
    {
        return name;
    }
    @Exclude
    public String getDesc()
    {
        return description;
    }
    @Exclude
    public void setName(String nme)
    {
        this.name=nme;
    }
    @Exclude
    public void setDesc(String desc)
    {
        this.description=desc;
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
    public LocalDateTime getStartTime()
    {
        if (startTime!=null) {
            return startTime;
        }
        else
        {
            String[] temp=strStartTime.split("[:/ -]");
            startTime = LocalDateTime.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]));
            return startTime;
        }
    } //.FORMAT
    @Exclude
    public LocalDateTime getEndTime()
    {
        if (endTime!=null) {
            return endTime;
        }
        else
        {
            String[] temp=strEndTime.split("[:/ -]");
            endTime = LocalDateTime.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]));
            return endTime;
        }
    }
    @Exclude
    public void setStartTime(LocalDateTime time)
    {
        this.startTime=time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        strStartTime=startTime.format(formatter);
    }
    @Exclude
    public void setEndTime(LocalDateTime time)
    {
        this.endTime=time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        strEndTime=endTime.format(formatter);
    }
    @Exclude
    public void setStrStartTime(String time)
    {
        strStartTime=time;
        String[] temp=strStartTime.split("[:/ -]");
        startTime = LocalDateTime.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]));

    }
    @Exclude
    public void setStrEndTime(String time)
    {
        strEndTime=time;
        String[] temp=strEndTime.split("[:/ -]");
        endTime = LocalDateTime.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4]));

    }
    @Exclude
    public String getOrganizer()
    {
        return organizer;
    }
    @Exclude
    public void setOrganizer(String o)
    {
        organizer=o;
    }
    @Exclude
    public String getStartTimeStr()
    {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        //return startTime.format(formatter);
        return strStartTime;
    }
    @Exclude
    public String getEndTimeStr()
    {
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        //return endTime.format(formatter);
        return strEndTime;
    }
    public String toString()
    {
        return this.name;
    }

}
