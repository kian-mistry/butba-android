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
import com.kian.butba.database.sqlite.entities.BowlerSeason;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class TableBowlerSeason extends SQLiteOpenHelper{

    private SharedPreferences sharedPreferences;

    public TableBowlerSeason(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_BOWLERS_SEASONS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_bowlers_seasons", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_BOWLERS_SEASONS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_bowlers_seasons", false);
        editor.commit();

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        onCreate(db);
    }

    public void addBowlerToSeason(BowlerSeason bowler) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bowlerSeasonId", bowler.getId());
        values.put("bowlerId", bowler.getBowlerId());
        values.put("studentStatus", bowler.getStudentStatus());
        values.put("rankingStatus", bowler.getRankingStatus());
        values.put("universityId", bowler.getUniversityId());
        values.put("academicYear", bowler.getAcademicYear());

        db.insert(DatabaseConstants.TABLE_BOWLER_SEASON, null, values);
        db.close();
    }

    public List<BowlerSeason> getBowlersSeason(int id) {
        List<BowlerSeason> bowlersSeasons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Using prepared statements.
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_PARTICULAR_BOWLERS_PLAYED_SEASON, new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()) {
            do {
                BowlerSeason bowlerSeason = new BowlerSeason(
                        Integer.parseInt(cursor.getString(0)),
                        Integer.parseInt(cursor.getString(1)),
                        Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5))
                );

                bowlersSeasons.add(bowlerSeason);
            } while(cursor.moveToNext());
        }

        return bowlersSeasons;
    }
}
