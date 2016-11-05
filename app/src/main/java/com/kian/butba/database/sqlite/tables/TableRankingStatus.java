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
import com.kian.butba.database.sqlite.entities.RankingStatus;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class TableRankingStatus extends SQLiteOpenHelper {

    private SharedPreferences sharedPreferences;

    public TableRankingStatus(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_RANKING_STATUS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_ranking_status", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_RANKING_STATUS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_ranking_status", false);
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

    public void addRankingStatus(RankingStatus rankingStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statusId", rankingStatus.getId());
        values.put("ranking", rankingStatus.getRanking());

        db.insert(DatabaseConstants.TABLE_RANKING_STATUS, null, values);
        db.close();
    }

    public String getRankingStatus(int id) {
        String rankingStatus = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_PARTICULAR_RANKING_STATUS, new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()) {
            rankingStatus = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return rankingStatus;
    }
}
