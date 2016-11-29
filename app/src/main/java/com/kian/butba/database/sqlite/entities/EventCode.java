package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 29/11/16.
 */

public class EventCode {

    private int id;
    private String event;

    public EventCode(int id, String event) {
        this.id = id;
        this.event = event;
    }

    public int getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }
}
