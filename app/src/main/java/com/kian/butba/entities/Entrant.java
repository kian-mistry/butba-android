package com.kian.butba.entities;

/**
 * Created by Kian Mistry on 05/03/17.
 */

public class Entrant {
	
	private int id;
	private String name;
	private String university;
	private int latestQualifiedAverage;
	
	public Entrant(int id, String name, String university, int latestQualifiedAverage) {
		this.id = id;
		this.name = name;
		this.university = university;
		this.latestQualifiedAverage = latestQualifiedAverage;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUniversity() {
		return university;
	}
	
	public int getLatestQualifiedAverage() {
		return latestQualifiedAverage;
	}
}