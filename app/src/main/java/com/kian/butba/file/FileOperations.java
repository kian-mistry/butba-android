package com.kian.butba.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class FileOperations {

	public static final String INTERNAL_SERVER_DIR = "/server/";

	public static final String BOWLERS_SEASON_STATS_FILE = "bowlers_seasons_stats";
	public static final String LATEST_AVERAGES = "latest_averages";

	public static String readFile(String directory, String fileName, String fileExt) throws IOException {
		File file = new File(directory, fileName + fileExt);
		FileInputStream inputStream = new FileInputStream(file);

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null) {
			builder.append(line).append("\n");
		}
		reader.close();
		inputStream.close();

		return builder.toString();
	}

	public static boolean fileExists(String directory, String fileName, String fileExt) {
		File file = new File(directory + fileName + fileExt);

		//f.exists() will return true if directory exists as well.
		return (file.exists() && !file.isDirectory());
	}
}
