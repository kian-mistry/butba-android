package com.kian.butba.views;

import android.view.View;

/**
 * Created by Kian Mistry on 18/10/16.
 */

public interface OnItemPressListener {
    void onTouch(View view, int position);
    void onLongTouch(View view, int position);
}
