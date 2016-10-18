package com.kian.butba.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Kian Mistry on 18/10/16.
 */

public class RecyclerTouchListener implements OnItemTouchListener {

    private GestureDetector gestureDetector;
    private OnItemPressListener pressListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OnItemPressListener pressListener) {
        this.pressListener = pressListener;

        gestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(child != null && pressListener != null) {
                    pressListener.onLongTouch(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if(child != null && pressListener != null && gestureDetector.onTouchEvent(e)) {
            pressListener.onTouch(child, recyclerView.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
