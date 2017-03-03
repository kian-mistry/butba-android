package com.kian.butba.entities;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class RankingStatus {

	private int rankingStatusId;

	public RankingStatus(int rankingStatusId) {
		this.rankingStatusId = rankingStatusId;
	}

	public String getRankingStatus() {
		switch(rankingStatusId) {
			case 1: return "Scratch";
			case 2: return "Handicap";
			default: return null;
		}
	}
}