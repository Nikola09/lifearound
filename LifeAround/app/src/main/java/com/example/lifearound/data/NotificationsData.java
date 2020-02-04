package com.example.lifearound.data;

import com.example.lifearound.data.model.Notification;

import java.util.ArrayList;

public class NotificationsData {
    private ArrayList<Notification> notifications;
    ListUpdatedEventListener updateListener;

    private NotificationsData(){
        notifications = new ArrayList<Notification>();
    }

    public void setEventListener(ListUpdatedEventListener listener){
        updateListener = listener;
    }
    public interface ListUpdatedEventListener {
        void onListUpdated();
    }

    private static class SingletonHolder{
        public static final NotificationsData instance=new NotificationsData();

    }

    public static NotificationsData getInstance(){
        return SingletonHolder.instance;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }
    public void addNewNotification(String notification)
    {
        notifications.add(new Notification(notification));
    }
    public Notification getNotification(int index)
    {
        return notifications.get(index);
    }

}
