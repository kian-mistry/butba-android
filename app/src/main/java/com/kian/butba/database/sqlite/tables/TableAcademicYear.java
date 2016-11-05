package com.kian.butba.database.sqlite.tables;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kian.butba.database.sqlite.DatabaseConstants;
import com.kian.butba.database.sqlite.DatabaseQueries;
import com.kian.butba.database.sqlite.entities.AcademicYear;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class TableAcademicYear extends SQLiteOpenHelper {

    private SharedPreferences sharedPreferences;

    public TableAcademicYear(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_ACADEMIC_YEAR_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_academic_year", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_ACADEMIC_YEAR_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_academic_year", false);
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

    public void addAcademicYear(AcademicYear academicYear) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("yearId", academicYear.getId());
        values.put("acadYear", academicYear.getAcademicYear());

        db.insert(DatabaseConstants.TABLE_ACADEMIC_YEAR, null, values);
        db.close();
    }

    public String getAcademicYear(int id) {
        String academicYear = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_PARTICULAR_ACADEMIC_YEAR, new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()) {
            academicYear = cursor.getString(0);
        }
        db.close();

        return academicYear;
    }

    public List<AcademicYear> getAllAcademicYears() {
        ArrayList<AcademicYear> academicYearsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_SELECT_ALL_ACADEMIC_YEARS, null);

        if(cursor.moveToFirst()) {
            do {
                AcademicYear academicYear = new AcademicYear(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1)
                );

                academicYearsList.add(academicYear);
            } while(cursor.moveToNext());
        }
        db.close();

        return academicYearsList;
    }
}
