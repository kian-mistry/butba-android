package com.kian.butba.entities;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class Bowler {

	private int id;
	private String forename;
	private String surname;
	private char gender;

	public Bowler(int id, String forename, String surname, char gender) {
		this.id = id;
		this.forename = forename;
		this.surname = surname;
		this.gender = gender;
	}

	public int getId() {
		return id;
	}

	public String getForename() {
		return forename;
	}

	public String getSurname() {
		return surname;
	}

	public String getFullName() {
		return forename + " " + surname;
	}

	public char getGender() {
		return gender;
	}
}