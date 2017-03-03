package com.kian.butba.file;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.kian.butba.database.QueriesUrl;

import java.io.IOException;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class DownloadHelpers {

	/**
	 * Downloads the latest bowlers stats.
	 *
	 * @param activity The current activity.
	 * @param bowlerId The ID of the bowler.
	 */
	public static void downloadSelectedBowlerStats(final Activity activity, int bowlerId) {
		if(!FileOperations.fileExists(activity.getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.BOWLERS_SEASON_STATS_FILE + "_" + bowlerId, ".json")) {
			if(FileOperations.hasInternetConnection(activity)) {
				new ServerFileDownloader(activity, new AsyncDelegate() {
					@Override
					public void onProcessResults(Boolean success) {
						if(success) {
							Snackbar.make(activity.getCurrentFocus(), "Stats loaded", Snackbar.LENGTH_SHORT).show();
						}
						else {
							Snackbar.make(activity.getCurrentFocus(), "Failed to load stats", Snackbar.LENGTH_SHORT).show();
						}
					}
				}).execute(
						QueriesUrl.url_get_bowlers_stats(bowlerId),
						FileOperations.BOWLERS_SEASON_STATS_FILE
				);
			}
			else {
				Snackbar.make(activity.getCurrentFocus(), "No internet connection", Snackbar.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Downloads the latest bowler's status.
	 *
	 * @param activity The current activity.
	 * @param bowlerId The ID of the bowler.
	 */
	public static void downloadBowlersLatestStatus(final Activity activity, final int bowlerId) {
		if(!FileOperations.fileExists(activity.getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_STATUS_FILE + "_" + bowlerId, ".json")) {
			if(FileOperations.hasInternetConnection(activity)) {
				new ServerFileDownloader(activity, new AsyncDelegate() {
					@Override
					public void onProcessResults(Boolean success) {
						if(success) {
							FileParsers.parseBowlersStatus(activity, bowlerId);
						}
						else {
							try {
								throw new IOException("File could not be downloaded.");
							}
							catch(IOException e) {
								e.printStackTrace();
							}
						}
					}
				}).execute(
						QueriesUrl.url_get_bowlers_latest_status(bowlerId),
						FileOperations.LATEST_STATUS_FILE + "_" + bowlerId
				);
			}
			else {
				Snackbar.make(activity.getCurrentFocus(), "No internet connection", Snackbar.LENGTH_SHORT).show();
			}
		}
	}
}