package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class University {

    private int id;
    private String university;

    public University(int id, String university) {
        this.id = id;
        this.university = university;
    }

    public int getId() {
        return id;
    }

    public String getUniversity() {
        return university;
    }
}
