package com.kian.butba;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.widget.TextView;

import com.kian.butba.database.server.QueriesUrl;
import com.kian.butba.file.AsyncDelegate;
import com.kian.butba.file.FileOperations;
import com.kian.butba.file.ServerFileDownloader;
import com.kian.butba.multithreading.FileDownloaderThreadMonitor;

public class SplashActivity extends Activity {

	private static final int MAX_FILES = 3;
	private static final int STARTUP_DELAY = 1500;

	private SharedPreferences prefInitialisation;
	private boolean isInitialised = false;
	private boolean isConnected = false;

	private TextView tvStatus;
	private AlertDialog alertDialog;

	private FileDownloaderThreadMonitor threadMonitor = new FileDownloaderThreadMonitor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_splash);

	    //Initialise views.
	    tvStatus = (TextView) findViewById(R.id.splash_status);

	    //Check whether app has been ran for the first time.
	    prefInitialisation = getSharedPreferences("butba_initialisation", Context.MODE_PRIVATE);
	    isInitialised = prefInitialisation.getBoolean("pref_initialised", false);
    }

	@Override
	protected void onResume() {
		super.onResume();

		//Check if device connected to the Internet.
		isConnected = FileOperations.hasInternetConnection(this);

		//Will close the alert dialog if it is present on the screen.
		closeConnectionDialogue();

		//Checks if the device has not been initialised and is not connected to the Internet.
		if(!isInitialised && !isConnected) {
			tvStatus.setText("Checking Internet connection...");
			promptConnectionDialogue();
		}
		//Downloads necessary files if device is not initialised but is connected to the Internet.
		else if(!isInitialised && isConnected) {
			tvStatus.setText("Fetching data...");

			if(!FileOperations.fileExists(getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.ALL_BOWLERS, ".json")) {
				startDownloadingTask(getFileDownloader(),
						QueriesUrl.URL_GET_ALL_BOWLERS,
						FileOperations.ALL_BOWLERS);
			}

			if(!FileOperations.fileExists(getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_AVERAGES, ".json")) {
				startDownloadingTask(getFileDownloader(),
						QueriesUrl.URL_GET_LATEST_EVENT_AVERAGES,
						FileOperations.LATEST_AVERAGES);
			}

			if(!FileOperations.fileExists(getFilesDir() + FileOperations.INTERNAL_SERVER_DIR, FileOperations.LATEST_RANKINGS, ".json")) {
				startDownloadingTask(getFileDownloader(),
						QueriesUrl.URL_GET_LATEST_EVENT_RANKINGS,
						FileOperations.LATEST_RANKINGS);
			}
		}
		//Goes to main activity if device has been initialised.
		else {
			tvStatus.setText("Loading...");

			//Shows startup screen for 1.5 seconds.
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intentMain = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intentMain);
				}
			}, STARTUP_DELAY);
		}
	}

	/**
	 * Creates an alert dialog prompting the user to connect to the Internet.
	 */
	private void promptConnectionDialogue() {
		Builder alertDialogBuilder = new Builder(SplashActivity.this);

		alertDialogBuilder.setTitle("Internet Connection")
				.setMessage("Connect to the internet?");

		alertDialogBuilder.setPositiveButton("OPEN SETTINGS", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Display the Settings app so that the user is able to connect to the Internet.
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_SETTINGS);
				intent.addCategory(Intent.CATEGORY_DEFAULT);

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

				startActivity(intent);

				alertDialog.cancel();
			}
		});

		alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Kills the activity, if refusing to connect to the Internet.
				finish();
			}
		});

		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/**
	 * Closes the alert dialog, if it is shown on the screen.
	 */
	private void closeConnectionDialogue() {
		if(alertDialog != null && alertDialog.isShowing()) {
			alertDialog.cancel();
		}
	}

	/**
	 * Creates a new AsyncTask to download the JSON file from the server.
	 * (AsyncTasks can not be executed more than once).
	 *
	 * @return A new ServerFileDownloader object.
	 */
	private ServerFileDownloader getFileDownloader() {
		return new ServerFileDownloader(this, new AsyncDelegate() {
			@Override
			public void onProcessResults(Boolean success) {
				if(success) {
					synchronized(threadMonitor) {
						threadMonitor.incrementFilesDownloaded();

						if(threadMonitor.getFilesDownloaded() >= MAX_FILES) {
							Editor editor = prefInitialisation.edit();
							editor.putBoolean("pref_initialised", true);
							editor.commit();

							Intent intentMain = new Intent(SplashActivity.this, MainActivity.class);
							startActivity(intentMain);
						}
					}
				}
			}
		});
	}

	/**
	 * Runs multiple AsyncTasks, providing SDK version >= 11.
	 * @param asyncTask The AsyncTask to execute.
	 * @param params The parameters which the AsyncTask takes.
	 */
	private void startDownloadingTask(AsyncTask<String, Integer, Boolean> asyncTask, String... params) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		}
		else {
			asyncTask.execute(params);
		}
	}
}