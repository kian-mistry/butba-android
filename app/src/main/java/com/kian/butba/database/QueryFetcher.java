package com.kian.butba.database;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Kian Mistry on 27/10/16.
 */

/**
 * Class which fetches queries from a MySQL database using AsyncTask.
 */
public class QueryFetcher extends AsyncTask<QueryMap, Void, ArrayList<String[]>> {

    private URL queriesUrl;
    private HttpURLConnection urlConnection;

    public AsyncResponse delegate = null;

    public interface AsyncResponse {
        void onProcessResults(ArrayList<String[]> output);
    }

    public QueryFetcher(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String[]> doInBackground(QueryMap... params) {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            switch(params[0].getQueryTag()) {
                case SELECT_ALL_BOWLERS:
                    queriesUrl = new URL(Queries.URL_SELECT_ALL_BOWLERS);
                    break;
                default:
                    break;
            }

            //Runs PHP script.
            urlConnection = (HttpURLConnection) queriesUrl.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.close();
            outputStream.close();

            inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            //PHP echoes a JSON encoded array, which is decoded here.
            JSONArray jsonArray = new JSONArray(bufferedReader.readLine());
            ArrayList<String[]> butbaMembers = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONArray butbaMemberDetails = jsonArray.getJSONArray(i);
                butbaMembers.add(new String[] {(String) butbaMemberDetails.get(0), (String) butbaMemberDetails.get(1)});
            }

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

            return butbaMembers;
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String[]> result) {
        super.onPostExecute(result);

        //Passes result back to the class which called it.
        delegate.onProcessResults(result);
    }
}
