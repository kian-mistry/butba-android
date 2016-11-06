package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 06/11/16.
 */

public class OverallAverage {

    private int bowlerId;
    private int totalPinfall;
    private int totalGames;
    private int average;
    private int academicYearId;

    public OverallAverage(int bowlerId, int totalPinfall, int totalGames, int average, int academicYearId) {
        this.bowlerId = bowlerId;
        this.totalPinfall = totalPinfall;
        this.totalGames = totalGames;
        this.average = average;
        this.academicYearId = academicYearId;
    }

    public int getBowlerId() {
        return bowlerId;
    }

    public int getTotalPinfall() {
        return totalPinfall;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getAverage() {
        return average;
    }

    public int getAcademicYearId() {
        return academicYearId;
    }
}
