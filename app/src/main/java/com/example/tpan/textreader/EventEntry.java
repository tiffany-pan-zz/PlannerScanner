/**
 * Created by tpan on 2/3/18.
 */

package com.example.tpan.textreader;

public class EventEntry {

    public String date;
    public String startTime;
    public String endTime;
    public String eventName;

    EventEntry(String d, String st, String et, String name) {
        date = d;
        startTime = st;
        endTime = et;
        eventName = name;
    }

    public String getDate(){
        return date;
    }
    public String getStartTime(){
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getEvents() {
        return eventName;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;

    }
    public void setEvents(String events){
        this.eventName = events;
    }

    public String toString() {
        return startTime + " - " + endTime + "  " + eventName;
    }
}
