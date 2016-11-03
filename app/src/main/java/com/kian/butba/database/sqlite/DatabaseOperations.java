package com.kian.butba.database.sqlite;

import android.app.Activity;
import android.support.design.widget.Snackbar;

import com.kian.butba.database.server.AsyncDelegate;
import com.kian.butba.database.server.QueryTag;
import com.kian.butba.database.server.TablesFetcher;
import com.kian.butba.database.sqlite.entities.Bowler;

import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class DatabaseOperations {
    /**
     * Set up a query fetcher to retrieve all the BUTBA members and to store in a local SQLite database.
     * @param activity
     */
    public static void getAllBowlers(final Activity activity) {
        TablesFetcher bowlersFetcher = new TablesFetcher(new AsyncDelegate() {
            @Override
            public void onProcessResults(List<String[]> results) {
                for(int i = 0; i < results.size(); i++) {
                    TableBowler tableBowler = new TableBowler(activity);
                    tableBowler.addBowler(new Bowler(
                            Integer.valueOf(results.get(i)[0]),
                            results.get(i)[1],
                            results.get(i)[2],
                            results.get(i)[3].charAt(0)
                    ));
                }

                Snackbar.make(activity.getCurrentFocus(), "Retrieved all bowlers", Snackbar.LENGTH_SHORT).show();
            }
        });
        bowlersFetcher.execute(QueryTag.GET_ALL_BOWLERS);
    }

}
