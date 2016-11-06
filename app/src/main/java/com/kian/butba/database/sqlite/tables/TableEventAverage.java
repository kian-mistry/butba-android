package com.kian.butba.database.sqlite.tables;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kian.butba.database.sqlite.DatabaseConstants;
import com.kian.butba.database.sqlite.DatabaseQueries;
import com.kian.butba.database.sqlite.entities.EventAverage;
import com.kian.butba.database.sqlite.entities.OverallAverage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class TableEventAverage extends SQLiteOpenHelper {

    private SharedPreferences sharedPreferences;

    public TableEventAverage(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_EVENT_AVERAGE_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_event_average", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_EVENT_AVERAGE_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_event_average", false);
        editor.commit();

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        onCreate(db);
    }

    public void addEventAverage(EventAverage eventAverage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("entryId", eventAverage.getId());
        values.put("bowlerId", eventAverage.getBowlerId());
        values.put("totalPinfall", eventAverage.getTotalPinfall());
        values.put("numberOfGames", eventAverage.getNumberOfGames());
        values.put("rankingPinfall", eventAverage.getRankingPinfall());
        values.put("hcpRankingPinfall", eventAverage.getHcpRankingPinfall());
        values.put("eventCodeId", eventAverage.getEventCodeId());
        values.put("academicYearId", eventAverage.getAcademicYearId());

        db.insert(DatabaseConstants.TABLE_EVENT_AVERAGE, null, values);
        db.close();
    }

    public List<OverallAverage> getOverallAverageOverAllSeasons(int bowlerId) {
        List<OverallAverage> overallAverageList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_PARTICULAR_BOWLERS_AVG_OVER_ALL_SEASONS, new String[]{String.valueOf(bowlerId)});

        if(cursor.moveToFirst()) {
            do {
                OverallAverage overallAverage = new OverallAverage(
                        Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4))
                );

                overallAverageList.add(overallAverage);
            } while(cursor.moveToNext());
        }
        db.close();

        return overallAverageList;
    }
}
