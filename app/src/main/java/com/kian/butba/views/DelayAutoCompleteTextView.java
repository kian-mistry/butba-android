package com.kian.butba.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by Kian Mistry on 11/03/17.
 */

public class DelayAutoCompleteTextView extends AppCompatAutoCompleteTextView {
	
	private static final int MESSAGE_TEXT_CHANGED = 100;
	private static final int DEFAULT_AUTOCOMPLETE_DELAY = 750;
	
	private ProgressBar progressBar;
	
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DelayAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
		}
	};
	
	public DelayAutoCompleteTextView(Context context) {
		super(context);
	}
	
	public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	
	@Override
	protected void performFiltering(CharSequence text, int keyCode) {
		if(progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
		
		handler.removeMessages(MESSAGE_TEXT_CHANGED);
		handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, text), DEFAULT_AUTOCOMPLETE_DELAY);
	}
	
	@Override
	public void onFilterComplete(int count) {
		if(progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
		
		super.onFilterComplete(count);
	}
}