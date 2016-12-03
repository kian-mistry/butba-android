package com.kian.butba.database.server;

import java.util.List;

/**
 * Created by Kian Mistry on 03/11/16.
 */

public interface AsyncDelegate {
    void onProcessResults(List<?> results);
}
