package com.kian.butba.database.sqlite.entities;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class RankingStatus {

    private int id;
    private String ranking;

    public RankingStatus(int id, String ranking) {
        this.id = id;
        this.ranking = ranking;
    }

    public int getId() {
        return id;
    }

    public String getRanking() {
        return ranking;
    }
}
