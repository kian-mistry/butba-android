package com.kian.butba.averages;

/**
 * Created by Kian Mistry on 21/12/16.
 */

public enum AverageCategories {

	STUDENT_MALE(0, "Student Male"),
	STUDENT_FEMALE(1, "Student Female"),
	EX_STUDENT_MALE(2, "Ex-Student Male"),
	EX_STUDENT_FEMALE(3, "Ex-Student Female");

	private final int id;
	private final String title;

	AverageCategories(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
}