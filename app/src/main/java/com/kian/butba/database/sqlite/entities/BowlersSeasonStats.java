package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 02/12/16.
 */

public class BowlersSeasonStats {

    private int id;
    private String name;
    private int studentStatus;
    private int rankingStatus;
    private int academicYear;
    private String university;
    private String[] stops;
    private int overallAverage;
    private int totalGames;
    private Float[] averages;
    private int totalPoints;
    private int bestN;
    private Integer[] rankingPoints;

    public BowlersSeasonStats() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(int studentStatus) {
        this.studentStatus = studentStatus;
    }

    public int getRankingStatus() {
        return rankingStatus;
    }

    public void setRankingStatus(int rankingStatus) {
        this.rankingStatus = rankingStatus;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String[] getStops() {
        return stops;
    }

    public void setStops(String[] stops) {
        this.stops = stops;
    }

    public int getOverallAverage() {
        return overallAverage;
    }

    public void setOverallAverage(int overallAverage) {
        this.overallAverage = overallAverage;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames = totalGames;
    }

    public Float[] getAverages() {
        return averages;
    }

    public void setAverages(Float[] averages) {
        this.averages = averages;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getBestN() {
        return bestN;
    }

    public void setBestN(int bestN) {
        this.bestN = bestN;
    }

    public Integer[] getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(Integer[] rankingPoints) {
        this.rankingPoints = rankingPoints;
    }
}
