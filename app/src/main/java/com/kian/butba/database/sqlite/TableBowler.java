package com.kian.butba.database.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kian.butba.database.sqlite.entities.Bowler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class TableBowler extends SQLiteOpenHelper {

    public TableBowler(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_BOWLERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addBowler(Bowler bowler) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bowlerId", bowler.getId());
        values.put("forename", bowler.getForename());
        values.put("surname", bowler.getSurname());
        values.put("gender", String.valueOf(bowler.getGender()));

        db.insert(DatabaseConstants.TABLE_BOWLER, null, values);
        db.close();
    }



    public List<Bowler> getAllBowlers() {
        ArrayList<Bowler> bowlerList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_SELECT_ALL_BOWLERS, null);

        //Go through all rows, adding each row to the list.
        if(cursor.moveToFirst()) {
            do {
                Bowler bowler = new Bowler(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3).charAt(0)
                );

                bowlerList.add(bowler);
            } while(cursor.moveToNext());
        }

        return bowlerList;
    }

}
