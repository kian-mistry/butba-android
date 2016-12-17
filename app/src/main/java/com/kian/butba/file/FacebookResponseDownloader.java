package com.kian.butba.file;

import android.content.Context;
import android.os.AsyncTask;

import com.facebook.GraphResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Kian Mistry on 17/12/16.
 */

/**
 * Class which saves the response from Facebook Graph Query into a .json file.
 * This is executed in the background using AsyncTask.
 */
public class FacebookResponseDownloader extends AsyncTask<GraphResponse, Integer, Boolean> {

	private static final String FILE_DIR = "/server/";

	private AsyncDelegate delegate = null;

	private File storageDir = null;
	private GraphResponse response = null;
	private String fileName = null;

	public FacebookResponseDownloader(Context context, AsyncDelegate delegate) {
		storageDir = context.getFilesDir();
		this.delegate = delegate;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(GraphResponse... params) {
		response = params[0];
		fileName = FileOperations.FACEBOOK_RESPONSE;

		File folder = new File(storageDir + FILE_DIR);
		boolean isFolderCreated = folder.exists() || folder.mkdirs();

		try {
			if(isFolderCreated) {
				File file = new File(folder, fileName + ".json");


				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
				bufferedWriter.write(response.getRawResponse());
				bufferedWriter.close();

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
