package com.kian.butba.database.sqlite.tables;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kian.butba.database.sqlite.DatabaseConstants;
import com.kian.butba.database.sqlite.entities.Bowler;
import com.kian.butba.database.sqlite.queries.DatabaseQueries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public class TableBowler extends SQLiteOpenHelper {

    private SharedPreferences sharedPreferences;

    public TableBowler(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_BOWLERS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_bowlers", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_BOWLERS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_bowlers", false);
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

    public Bowler getBowler(int bowlerId) {
        Bowler bowler = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_PARTICULAR_BOWLER, new String[]{String.valueOf(bowlerId)});

        //Go through all rows, adding each row to the list.
        if(cursor.moveToFirst()) {
            bowler = new Bowler(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3).charAt(0)
            );
        }
        db.close();

        return bowler;
    }

    public List<Bowler> getGenderSpecificBowlers(String gender) {
        ArrayList<Bowler> bowlerList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_SELECT_GENDER_SPECIFIC_BOWLERS, new String[]{gender});

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
        db.close();

        return bowlerList;
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
        db.close();

        return bowlerList;
    }
}
