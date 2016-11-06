package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 06/11/16.
 */

public class EventAverage {

    private int id;
    private int bowlerId;
    private int totalPinfall;
    private int numberOfGames;
    private int rankingPinfall;
    private int hcpRankingPinfall;
    private int eventCodeId;
    private int academicYearId;

    public EventAverage(int id, int bowlerId, int totalPinfall, int numberOfGames, int rankingPinfall, int hcpRankingPinfall, int eventCodeId, int academicYearId) {
        this.id = id;
        this.bowlerId = bowlerId;
        this.totalPinfall = totalPinfall;
        this.numberOfGames = numberOfGames;
        this.rankingPinfall = rankingPinfall;
        this.hcpRankingPinfall = hcpRankingPinfall;
        this.eventCodeId = eventCodeId;
        this.academicYearId = academicYearId;
    }

    public int getId() {
        return id;
    }

    public int getBowlerId() {
        return bowlerId;
    }

    public int getTotalPinfall() {
        return totalPinfall;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public int getRankingPinfall() {
        return rankingPinfall;
    }

    public int getHcpRankingPinfall() {
        return hcpRankingPinfall;
    }

    public int getEventCodeId() {
        return eventCodeId;
    }

    public int getAcademicYearId() {
        return academicYearId;
    }
}
