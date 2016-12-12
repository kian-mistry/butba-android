package com.kian.butba.file;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kian Mistry on 12/12/16.
 */

/**
 * Class which downloads the result of database queries from a given URL.
 * This is executed in the background using AsyncTask.
 */
public class ServerFileDownloader extends AsyncTask<String, Integer, Boolean> {

	private static final int BYTE_SIZE = 1024;
	private static final String FILE_DIR = "/server/";

	private AsyncDelegate delegate = null;

	private File storageDir = null;
	private String fileUrl = null;
	private String fileName = null;

	public ServerFileDownloader(Context context, AsyncDelegate delegate) {
		storageDir = context.getFilesDir();
		this.delegate = delegate;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.d("DIR", storageDir.toString());
	}


	@Override
	protected Boolean doInBackground(String... params) {
		fileUrl = params[0];
		fileName = params[1];

		File folder = new File(storageDir + FILE_DIR);
		boolean isFolderCreated = folder.exists() || folder.mkdirs();

		try {
			if(isFolderCreated) {
				File file = new File(folder, fileName + ".json");

				//Obtain file from URL.
				URL url = new URL(fileUrl);
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(file);

				//Get content from URL and write to file.
				byte[] buffer = new byte[BYTE_SIZE];
				int bufferLength, totalDownloaded = 0;
				int fileLength = urlConnection.getContentLength();

				while((bufferLength = inputStream.read(buffer)) > 0) {
					totalDownloaded += bufferLength;

					outputStream.write(buffer, 0, bufferLength);
					publishProgress((totalDownloaded / fileLength) * 100);
				}
				outputStream.flush();
				outputStream.close();

				return true;
			}
			else {
				throw new FileNotFoundException("Folder could not be created: " + folder);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if(delegate != null) {
			delegate.onProcessResults(result);
		}

		if(result) {
			try {
				FileOperations.readFile(storageDir + FILE_DIR, fileName, ".json");
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
