package com.kian.butba.database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kian Mistry on 27/10/16.
 */

/**
 * Class which will be used as an identifier to execute particular SQL queries.
 * Will hold key/value pairs to be used in a query URL.
 */
public class QueryMap {

    public enum QueryTag {
        SELECT_ALL_BOWLERS
    }

    private QueryTag queryTag;
    private HashMap<String, String> request;

    public QueryMap(QueryTag queryTag, String key, String value) {
        this.queryTag = queryTag;

        this.request = new HashMap<>();
        this.request.put(key, value);
    }

    public QueryTag getQueryTag() {
        return queryTag;
    }

    public ArrayList<String> getKeys() {
        ArrayList<String> keys = new ArrayList<>();
        for(String key : request.keySet()) {
            keys.add(key);
        }
        return keys;
    }

    public String getValue(String key) {
        return request.get(key);
    }

}
