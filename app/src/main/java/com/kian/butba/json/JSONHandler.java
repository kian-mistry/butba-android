package com.kian.butba.json;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kian Mistry on 20/10/16.
 */

public class JSONHandler {

    public static JSONObject loadJson(Context context, String filename) {
        try {
            InputStream inputStream = context.getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
