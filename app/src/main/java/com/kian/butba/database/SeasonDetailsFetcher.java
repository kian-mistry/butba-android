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
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Kian Mistry on 28/10/16.
 */

/**
 * Class which fetches all the overall season details of selected member from a MySQL database using AsyncTask.
 */
public class SeasonDetailsFetcher extends AsyncTask<QueryMap, Void, ArrayList<String[]>> {

    private static final String BOWLER_ID_IDENTIFIER = "bowler_id";

    private URL queriesUrl;
    private HttpURLConnection urlConnection;

    private String bowlerId;

    public AsyncDelegate delegate = null;

    public interface AsyncDelegate {
        void onProcessResults(ArrayList<String[]> output);
    }

    public SeasonDetailsFetcher(AsyncDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String[]> doInBackground(QueryMap... params) {
        try {
            bowlerId = params[0].getValue(BOWLER_ID_IDENTIFIER);
            queriesUrl = new URL(Queries.URL_GET_BOWLERS_SEASON_DETAILS);

            //Runs PHP script.
            urlConnection = (HttpURLConnection) queriesUrl.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String postQuery =
                    URLEncoder.encode(BOWLER_ID_IDENTIFIER, "UTF-8") + "=" +
                    URLEncoder.encode(bowlerId, "UTF-8");
            bufferedWriter.write(postQuery);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            //PHP echoes a JSON encoded array, which is decoded here.
            JSONArray jsonArray = new JSONArray(bufferedReader.readLine());
            ArrayList<String[]> bowlerStatusDetails = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONArray jsonDetails = jsonArray.getJSONArray(i);

                //Skips if a bowler has not bowled in a particular season.
                if(jsonDetails.get(0).equals(null)) continue;

                String[] details = new String[jsonDetails.length()];
                for(int j = 0; j < jsonDetails.length(); j++) {
                    details[j] = jsonDetails.get(j).toString();
                }
                bowlerStatusDetails.add(details);
            }

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

            return bowlerStatusDetails;
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
