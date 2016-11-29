package com.kian.butba.database.sqlite.tables;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kian.butba.database.sqlite.DatabaseConstants;
import com.kian.butba.database.sqlite.DatabaseQueries;
import com.kian.butba.database.sqlite.entities.EventCode;

/**
 * Created by Kian Mistry on 29/11/16.
 */

public class TableEventCode extends SQLiteOpenHelper {

    private SharedPreferences sharedPreferences;

    public TableEventCode(Context context) {
        super(context, DatabaseConstants.DATABASE_NAME, null, DatabaseConstants.DATABASE_VERSION);

        sharedPreferences = context.getSharedPreferences("butba_database", Context.MODE_PRIVATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseQueries.QUERY_CREATE_EVENT_CODE_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_event_code", true);
        editor.commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseQueries.QUERY_DROP_EVENT_CODE_TABLE);

        Editor editor = sharedPreferences.edit();
        editor.putBoolean("pref_table_event_code", false);
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

    public void addEvent(EventCode event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("eventCodeId", event.getId());
        values.put("event", event.getEvent());

        db.insert(DatabaseConstants.TABLE_EVENT_CODE, null, values);
        db.close();
    }
}
