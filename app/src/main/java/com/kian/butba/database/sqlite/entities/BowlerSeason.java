package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class BowlerSeason {

    private int id;
    private int bowlerId;
    private int studentStatus;
    private int rankingStatus;
    private int universityId;
    private int academicYear;   //Academic year IDs start from 1, denote the 2015/16 academic year.

    public BowlerSeason(int id, int bowlerId, int studentStatus, int rankingStatus, int universityId, int academicYear) {
        this.id = id;
        this.bowlerId = bowlerId;
        this.studentStatus = studentStatus;
        this.rankingStatus = rankingStatus;
        this.universityId = universityId;
        this.academicYear = academicYear;
    }

    public int getId() {
        return id;
    }

    public int getBowlerId() {
        return bowlerId;
    }

    public int getStudentStatus() {
        return studentStatus;
    }

    public int getRankingStatus() {
        return rankingStatus;
    }

    public int getUniversityId() {
        return universityId;
    }

    public int getAcademicYear() {
        return academicYear;
    }
}
