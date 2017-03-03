package com.kian.butba.entities;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class BowlerSeasonStats {

	private int academicYearId;
	private int rankingStatus;
	private int studentStatus;
	private String university;
	private int average;
	private int games;
	private int points;
	private int bestN;

	public BowlerSeasonStats(int academicYearId, int rankingStatus, int studentStatus, String university, int average, int games, int points, int bestN) {
		this.academicYearId = academicYearId;
		this.rankingStatus = rankingStatus;
		this.studentStatus = studentStatus;
		this.university = university;
		this.average = average;
		this.games = games;
		this.points = points;
		this.bestN = bestN;
	}

	public int getAcademicYear() {
		return academicYearId;
	}

	public int getRankingStatus() {
		return rankingStatus;
	}

	public int getStudentStatus() {
		return studentStatus;
	}

	public String getUniversity() {
		return university;
	}

	public int getAverage() {
		return average;
	}

	public int getGames() {
		return games;
	}

	public int getPoints() {
		return points;
	}

	public int getBestN() {
		return bestN;
	}
}