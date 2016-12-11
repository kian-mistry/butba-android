package com.kian.butba.file;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat.Builder;
import android.view.View;
import android.view.View.OnClickListener;

import com.kian.butba.R;

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
	public static final String RESULTS_DIR = STORAGE_DIR + "/BUTBA/Results/";
    private static final int BYTE_SIZE = 1024 * 1024;

    private AsyncDelegate delegate = null;
    private Context context = null;
	private View view = null;

    private Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int notificationId;
    private String notificationTitle;
    private Bitmap notificationIcon;

    String fileUrl = null;
    String fileName = null;
    String fileDirectory = null;
    String fileType = null;

    public FileDownloader(AsyncDelegate delegate) {
        this.delegate = delegate;
    }

    public FileDownloader(Context context) {
        this.context = context;
        this.notificationId = -1;
        this.notificationTitle = "File";
        this.notificationIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo_circle);
    }

    public FileDownloader(Context context, int notificationId, String notificationTitle) {
        this.context = context;
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.notificationIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo_circle);
    }

    public FileDownloader(Context context, int notificationId, String notificationTitle, int notificationIcon) {
        this.context = context;
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.notificationIcon = BitmapFactory.decodeResource(context.getResources(), notificationIcon);
    }

	public FileDownloader(Context context, View view, int notificationId, String notificationTitle, int notificationIcon) {
		this.context = context;
		this.notificationId = notificationId;
		this.notificationTitle = notificationTitle;
		this.notificationIcon = BitmapFactory.decodeResource(context.getResources(), notificationIcon);
		this.view = view;
	}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //Set up notification.
        notificationBuilder = new Builder(context);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_logo_launcher);
        notificationBuilder.setLargeIcon(notificationIcon);
        notificationBuilder.setTicker("BUTBA: " + notificationTitle + " downloading");
        notificationBuilder.setWhen(System.currentTimeMillis());
	    notificationBuilder.setContentTitle("BUTBA: File downloading");
	    notificationBuilder.setContentText(notificationTitle);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        fileUrl = params[0];
        fileName = params[1];
        fileDirectory = params[2];
        fileType = params[3];

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

        notificationBuilder.setProgress(100, values[0], false);
        notificationBuilder.setContentTitle("BUTBA: File downloading");
	    notificationBuilder.setContentText(notificationTitle);
        notificationManager.notify(notificationId, notificationBuilder.build());

        if(values[0] == 100) {
            //Removes progress bar from the notification.
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setTicker("BUTBA: " + notificationTitle + "downloaded");
            notificationBuilder.setContentTitle("BUTBA: File downloaded");
            notificationBuilder.setContentText(notificationTitle);

            //Open the downloaded file when the download has completed.
            notificationBuilder.setContentIntent(PendingIntent.getActivity(
                    context,
                    0,
                    openFileActivity(fileDirectory, fileName, fileType),
                    PendingIntent.FLAG_UPDATE_CURRENT));

            //Update the notification.
            notificationManager.notify(notificationId, notificationBuilder.build());

	        if(view != null) {
		        //Show snackbar once download is complete.
		        Snackbar snackbar = Snackbar.make(view, notificationTitle + " downloaded", Snackbar.LENGTH_SHORT);
		        snackbar.setAction("Open", new OnClickListener() {
			        @Override
			        public void onClick(View v) {
				        context.startActivity(openFileActivity(fileDirectory, fileName, fileType));

				        //Once opened using the snackbar, remove notification.
				        notificationManager.cancel(notificationId);
			        }
		        });
		        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colourAccent));
		        snackbar.show();
	        }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        //Passes result back to the class which called it.
        if(delegate != null) {
            delegate.onProcessResults(result);
        }
    }

    /**
     * Creates an intent which can be used to open files of any type.
     * @param fileDirectory The directory of the file.
     * @param fileName The name of the file.
     * @param fileType The file extension.
     * @return An intent which can be started.
     */
    public static Intent openFileActivity(String fileDirectory, String fileName, String fileType) {
        File file = new File(fileDirectory + fileName);
        Uri filePath = Uri.fromFile(file);

        Intent fileIntent = new Intent(Intent.ACTION_VIEW);
        fileIntent.setDataAndType(filePath, fileType);
        fileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return fileIntent;
    }
}

