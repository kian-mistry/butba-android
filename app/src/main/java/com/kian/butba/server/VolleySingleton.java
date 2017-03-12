package com.kian.butba.server;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kian Mistry on 11/03/17.
 */

public class VolleySingleton {
	
	private static VolleySingleton volleySingleton;
	private Context context;
	
	private RequestQueue requestQueue;
	
	private VolleySingleton(Context context) {
		this.context = context;
		requestQueue = getRequestQueue();
	}
	
	private RequestQueue getRequestQueue() {
		if(requestQueue == null) {
			requestQueue = Volley.newRequestQueue(context.getApplicationContext());
		}
		
		return requestQueue;
	}
	
	public static synchronized VolleySingleton getInstance(Context context) {
		if(volleySingleton == null) {
			volleySingleton = new VolleySingleton(context);
		}
		
		return volleySingleton;
	}
	
	public<T> void addToRequestQueue(Request<T> request) {
		requestQueue.add(request);
	}
}