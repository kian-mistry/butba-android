package com.kian.butba.file;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kian Mistry on 07/12/16.
 */

/**
 * Class which downloads a file from a given URL.
 * This is executed in the background using AsyncTask.
 */
public class FileDownloader extends AsyncTask<String, Integer, Boolean> {

    public static final String STORAGE_DIR = Environment.getExternalStorageDirectory().toString();
    public static final String ENTRY_FORMS_DIR = STORAGE_DIR + "/BUTBA/Entry Forms/";
    private static final int BYTE_SIZE = 1024 * 1024;

    private AsyncDelegate delegate = null;

    public FileDownloader(AsyncDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String fileUrl = params[0];
        String fileName = params[1];
        String fileDirectory = params[2];

        File folder = new File(fileDirectory);
        boolean isFolderCreated = folder.exists() || folder.mkdirs();

        try {
            if(isFolderCreated) {
                File file = new File(folder, fileName);
                file.createNewFile();

                //Obtain file from URL.
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);

                //Get content from URL and write to a file.
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if(values[0] == 100) {
            Log.d("FILE DL", values[0].toString());
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        //Passes result back to the class which called it.
        delegate.onProcessResults(result);
    }
}

