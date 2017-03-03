package com.kian.butba.entities;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class StudentStatus {

	private int studentStatusId;

	public StudentStatus(int studentStatusId) {
		this.studentStatusId = studentStatusId;
	}

	public String getStudentStatus() {
		switch(studentStatusId) {
			case 1: return "Student";
			case 2: return "Ex-Student";
			default: return null;
		}
	}
}