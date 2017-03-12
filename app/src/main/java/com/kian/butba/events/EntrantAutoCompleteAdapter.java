package com.kian.butba.events;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.kian.butba.R;
import com.kian.butba.database.QueriesUrl;
import com.kian.butba.entities.Entrant;
import com.kian.butba.server.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kian Mistry on 05/03/17.
 */

public class EntrantAutoCompleteAdapter extends BaseAdapter implements Filterable {
	
	private Context context;
	private List<Entrant> entrantList;
	
	public EntrantAutoCompleteAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return entrantList.size();
	}
	
	@Override
	public Entrant getItem(int position) {
		return entrantList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.dropdown_item_two_line, parent, false);
		}
		
		((TextView) convertView.findViewById(R.id.dropdown_header)).setText(getItem(position).getName());
		((TextView) convertView.findViewById(R.id.dropdown_sub_header)).setText(getItem(position).getUniversity());
		
		return convertView;
	}
	
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				
				if(constraint != null) {
					Log.d("SEARCH TERM", constraint.toString());
					
					findEntrants(context, constraint.toString());
					
					filterResults.values = entrantList;
					filterResults.count = entrantList.size();
				}
				
				return filterResults;
			}
			
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if(results != null && results.count > 0) {
					entrantList = (List<Entrant>) results.values;
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}
		};
		
		return filter;
	}
	
	/**
	 * Obtains a list of entrants that match the search term.
	 *
	 * @param context The context of the activity.
	 * @param searchTerm The term which will be used to query a list of entrants with qualified averages.
	 */
	private void findEntrants(final Context context, String searchTerm) {
		entrantList = new ArrayList<>();
		
		JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, QueriesUrl.url_get_latest_bowlers_qual_average(searchTerm), null,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						try {
							for(int i = 0; i < response.length(); i++) {
								JSONObject jsonObject = response.getJSONObject(i);
								
								int id = Integer.parseInt(jsonObject.getString("Id"));
								String name = jsonObject.getString("Name");
								String university = jsonObject.getString("University");
								int qualAvg = Integer.parseInt(jsonObject.getString("Average"));
								
								entrantList.add(new Entrant(id, name, university, qualAvg));
								notifyDataSetChanged();
							}
						}
						catch(JSONException e) {
							e.printStackTrace();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError e) {
						e.printStackTrace();
						notifyDataSetInvalidated();
					}
				}
		);
		
		VolleySingleton.getInstance(context).addToRequestQueue(request);
	}
}