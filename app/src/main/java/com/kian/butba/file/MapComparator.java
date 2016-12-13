package com.kian.butba.file;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Kian Mistry on 13/12/16.
 */

public class MapComparator implements Comparator<Map<String, String>> {

	public enum Sort {ASCENDING, DESCENDING}

	private String key;
	private Sort sort;

	public MapComparator(String key, Sort sort) {
		this.key = key;
		this.sort = sort;
	}


	@Override
	public int compare(Map<String, String> firstMap, Map<String, String> secondMap) {
		Integer firstValue = Integer.valueOf(firstMap.get(key));
		Integer secondValue = Integer.valueOf(secondMap.get(key));

		switch(sort) {
			case ASCENDING:
				return sortAscending(firstValue, secondValue);
			case DESCENDING:
				return sortDescending(firstValue, secondValue);
			default:
				return 0;
		}
	}

	private int sortAscending(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	private int sortDescending(int x, int y) {
		return (x > y) ? -1 : ((x == y) ? 0 : 1);
	}
}
