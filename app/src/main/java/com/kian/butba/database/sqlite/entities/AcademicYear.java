package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class AcademicYear {

    private int id;
    private String academicYear;

    public AcademicYear(int id, String academicYear) {
        this.id = id;
        this.academicYear = academicYear;
    }

    public int getId() {
        return id;
    }

    public String getAcademicYear() {
        return academicYear;
    }
}
