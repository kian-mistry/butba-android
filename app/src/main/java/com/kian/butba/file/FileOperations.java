package com.kian.butba.file;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Kian Mistry on 12/12/16.
 */

public class FileOperations {

	public static final String INTERNAL_SERVER_DIR = "/server/";

	public static final String BOWLERS_SEASON_STATS_FILE = "bowlers_seasons_stats";
	public static final String LATEST_AVERAGES = "latest_averages";
	public static final String LATEST_RANKINGS = "latest_rankings";

	public static final String FACEBOOK_RESPONSE = "facebook_response";
	public static final String TWITTER_RESPONSE ="twitter_response";

	public static boolean fileExists(String directory, String fileName, String fileExt) {
		File file = new File(directory + fileName + fileExt);

		//f.exists() will return true if directory exists as well.
		return file.exists() && !file.isDirectory();
	}

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

	public static void writeFile(String directory, String fileName, String fileExt, String text) throws IOException {
		FileWriter fileWriter = new FileWriter(directory + fileName + fileExt);

		fileWriter.write(text);
		fileWriter.flush();
		fileWriter.close();
	}

	public static boolean hasInternetConnection(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

		return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
	}
}
