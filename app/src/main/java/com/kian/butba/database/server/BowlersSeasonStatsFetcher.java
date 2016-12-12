package com.kian.butba.database.server;

import android.content.Context;
import android.os.AsyncTask;

import com.kian.butba.database.sqlite.entities.BowlersSeasonStats;
import com.kian.butba.file.FileOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 02/12/16.
 */

public class BowlersSeasonStatsFetcher extends AsyncTask<Void, Void, List<BowlersSeasonStats>> {

	private Context context = null;
    private AsyncDelegate delegate = null;

    public BowlersSeasonStatsFetcher(Context context, AsyncDelegate delegate) {
	    this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<BowlersSeasonStats> doInBackground(Void... params) {
        try {
            //PHP echoes a JSON encoded array which is decoded here.
	        String jsonString = FileOperations.readFile(
			        context.getFilesDir() + FileOperations.INTERNAL_SERVER_DIR,
			        FileOperations.BOWLERS_SEASON_STATS_FILE,
			        ".json");

			JSONObject outerJsonObject = new JSONObject(jsonString);

            ArrayList<BowlersSeasonStats> bowlersSeasonStatsList = new ArrayList<>();

            for(int i = 0; i < outerJsonObject.length(); i++) {
                if(!outerJsonObject.isNull(String.valueOf(i))) {
                    JSONObject innerJsonObject = outerJsonObject.getJSONObject(String.valueOf(i));

                    BowlersSeasonStats bowlersSeasonStats = new BowlersSeasonStats();
                    bowlersSeasonStats.setStudentStatus(innerJsonObject.getInt("StudentStatus"));
                    bowlersSeasonStats.setRankingStatus(innerJsonObject.getInt("RankingStatus"));

                    if(bowlersSeasonStats.getStudentStatus() == 1) {
                        bowlersSeasonStats.setUniversity(innerJsonObject.getString("University"));
                    }
                    else {
                        bowlersSeasonStats.setUniversity("Ex-Student");
                    }

                    bowlersSeasonStats.setAcademicYear(innerJsonObject.getInt("AcademicYear"));

                    JSONArray stopsArray = innerJsonObject.getJSONArray("Stops");
                    String[] stops = new String[stopsArray.length()];
                    for(int j = 0; j < stopsArray.length(); j++) {
                        stops[j] = stopsArray.getString(j);
                    }
                    bowlersSeasonStats.setStops(stops);

                    bowlersSeasonStats.setOverallAverage(innerJsonObject.getInt("OverallAverage"));
                    bowlersSeasonStats.setTotalGames(innerJsonObject.getInt("TotalGames"));

                    JSONObject averagesObject = innerJsonObject.getJSONObject("Averages");
                    Float[] averages = new Float[averagesObject.length()];
                    for(int k = 0; k < averagesObject.length(); k++) {
                        String temp = averagesObject.getString(bowlersSeasonStats.getStops()[k]);

                        if(temp.equals("-")) {
                            averages[k] = null;
                        }
                        else {
                            averages[k] = Float.parseFloat(temp);
                        }
                    }
                    bowlersSeasonStats.setAverages(averages);

                    bowlersSeasonStats.setTotalPoints(innerJsonObject.getInt("TotalPoints"));
                    bowlersSeasonStats.setBestN(innerJsonObject.getInt("BestN"));

                    JSONArray rankingsArray = innerJsonObject.getJSONArray("RankingPoints");
                    Integer[] rankings = new Integer[rankingsArray.length()];
                    for(int l = 0; l < rankingsArray.length(); l++) {
                        Object temp = rankingsArray.get(l);

                        if(temp.equals("-")) {
                            rankings[l] = null;
                        }
                        else {
                            rankings[l] = (Integer) temp;
                        }
                    }
                    bowlersSeasonStats.setRankingPoints(rankings);

                    bowlersSeasonStatsList.add(bowlersSeasonStats);
                }
            }

            return bowlersSeasonStatsList;
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<BowlersSeasonStats> result) {
        super.onPostExecute(result);

        //Passes result back to the class which called it.
        delegate.onProcessResults(result);
    }
}
