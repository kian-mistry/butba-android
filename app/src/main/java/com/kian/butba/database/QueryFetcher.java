package com.kian.butba.database;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

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

/**
 * Created by Kian Mistry on 27/10/16.
 */

/**
 * Class which fetches queries from a MySQL database using AsyncTask.
 */
public class QueryFetcher extends AsyncTask<QueryMap, Void, String> {

    private Context context;

    private URL queriesUrl;
    private HttpURLConnection urlConnection;

    private AlertDialog alertDialog;

    public QueryFetcher(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
    }

    @Override
    protected String doInBackground(QueryMap... params) {
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

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

            return jsonArray.toString();
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
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //JSON Array displayed in AlertDialog.
        alertDialog.setMessage(result);
        alertDialog.show();
    }
}
