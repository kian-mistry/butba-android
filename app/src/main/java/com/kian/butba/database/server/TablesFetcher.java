package com.kian.butba.database.server;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

/**
 * Class which fetches a particular table from a MYSQL database using AsyncTask.
 */
public class TablesFetcher extends AsyncTask<QueryTag, Void, List<String[]>> {

    private AsyncDelegate delegate = null;

    private URL urlQuery = null;
    private HttpURLConnection urlConnection;

    public TablesFetcher(AsyncDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String[]> doInBackground(QueryTag... params) {
        try {
            switch(params[0]) {
                case GET_ACADEMIC_YEARS:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_ACADEMIC_YEARS);
                    break;
                case GET_ALL_BOWLERS:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_BOWLERS);
                    break;
                case GET_ALL_BOWLERS_SEASONS:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_BOWLERS_SEASONS);
                    break;
                case GET_EVENT_AVERAGES:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_BOWLERS_EVENT_AVERAGE);
                    break;
                case GET_RANKING_STATUSES:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_RANKING_STATUSES);
                    break;
                case GET_STUDENT_STATUSES:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_STUDENT_STATUSES);
                    break;
                case GET_UNIVERSITIES:
                    urlQuery = new URL(QueriesUrl.URL_GET_ALL_UNIVERSITIES);
                    break;
                default:
                    urlQuery = null;
                    break;
            }

            urlConnection = (HttpURLConnection) urlQuery.openConnection();
            urlConnection.setDoInput(true);

            //Set up input stream so the output echoed from the PHP file can be read.
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //PHP echoes a JSON encoded array which is decoded here.
            JSONArray jsonArray = new JSONArray(bufferedReader.readLine());
            ArrayList<String[]> rows = new ArrayList<>();
            JSONArray row;
            String[] columns;
            for(int i = 0; i < jsonArray.length(); i++) {
                row = jsonArray.getJSONArray(i);
                columns = new String[row.length()];
                for(int j = 0; j < row.length(); j++) {
                    //Taking null values into consideration.
                    columns[j] = (row.get(j).equals(null) || row.get(j).equals("null")) ? "" : (String) row.get(j);
                }
                rows.add(columns);
            }

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

            return rows;
        }
        catch(IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<String[]> result) {
        super.onPostExecute(result);

        //Passes result back to the class which called it.
        delegate.onProcessResults(result);
    }
}
