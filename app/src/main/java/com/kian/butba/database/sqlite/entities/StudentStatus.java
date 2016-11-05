package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class StudentStatus {

    private int id;
    private String student;

    public StudentStatus(int id, String student) {
        this.id = id;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public String getStudent() {
        return student;
    }
}
