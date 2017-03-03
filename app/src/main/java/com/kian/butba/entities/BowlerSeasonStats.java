package com.kian.butba.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class BowlerSeasonStats implements Serializable {

	private int academicYearId;
	private int rankingStatus;
	private int studentStatus;
	private String university;
	private int average;
	private int games;
	private int points;
	private int bestN;

	private List<String> stopsList;
	private HashMap<String, String> tournamentAverages;
	private HashMap<String, String> tournamentPoints;

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

	public List<String> getStopsList() {
		return stopsList;
	}

	public HashMap<String, String> getTournamentAverages() {
		return tournamentAverages;
	}

	public HashMap<String, String> getTournamentPoints() {
		return tournamentPoints;
	}

	public void setStopsList(List<String> stopsList) {
		this.stopsList = stopsList;
	}

	public void setTournamentAverages(HashMap<String, String> tournamentAverages) {
		this.tournamentAverages = tournamentAverages;
	}

	public void setTournamentPoints(HashMap<String, String> tournamentPoints) {
		this.tournamentPoints = tournamentPoints;
	}
}