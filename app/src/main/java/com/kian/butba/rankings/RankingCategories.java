package com.kian.butba.rankings;

/**
 * Created by Kian Mistry on 19/12/16.
 */

public enum RankingCategories {

	STUDENT_SCRATCH_MALE(0, "Student Scratch Male"),
	STUDENT_SCRATCH_FEMALE(1, "Student Scratch Female"),
	STUDENT_HANDICAP(2, "Student Handicap"),
	EX_STUDENT_SCRATCH_MALE(3, "Ex-Student Scratch Male"),
	EX_STUDENT_SCRATCH_FEMALE(4, "Ex-Student Scratch Female"),
	EX_STUDENT_HANDICAP(5, "Ex-Student Handicap"),
	UNIVERSITY(6, "University");

	private final int id;
	private final String title;

	RankingCategories(int id, String title) {
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
