package com.kian.butba.file;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.kian.butba.database.QueriesUrl;

/**
 * Created by Kian Mistry on 02/03/17.
 */

public class DownloadHelpers {

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
}