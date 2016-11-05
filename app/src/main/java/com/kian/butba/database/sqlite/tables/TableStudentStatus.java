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
import com.kian.butba.database.sqlite.entities.StudentStatus;

/**
 * Created by Kian Mistry on 05/11/16.
 */

public class TableStudentStatus extends SQLiteOpenHelper {

    private SharedPreferences sharedPreferences;

    public TableStudentStatus(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_STUDENT_STATUS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_student_status", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_STUDENT_STATUS_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_student_status", false);
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

    public void addStudentStatus(StudentStatus studentStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statusId", studentStatus.getId());
        values.put("student", studentStatus.getStudent());

        db.insert(DatabaseConstants.TABLE_STUDENT_STATUS, null, values);
        db.close();
    }

    public String getStudentStatus(int id) {
        String studentStatus = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(DatabaseQueries.QUERY_PARTICULAR_STUDENT_STATUS, new String[]{String.valueOf(id)});

        if(cursor.moveToFirst()) {
            studentStatus = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return studentStatus;
    }
}
