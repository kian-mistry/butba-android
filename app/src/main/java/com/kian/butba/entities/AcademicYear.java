package com.kian.butba.entities;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class AcademicYear {

	private int academicYearId;

	public AcademicYear(int academicYearId) {
		this.academicYearId = academicYearId;
	}

	public String getAcademicYear() {
		switch(academicYearId) {
			case 1: return "2015/16";
			case 2: return "2016/17";
			default: return null;
		}
	}
}