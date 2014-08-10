package com.battlehack.empowered;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;

public class LocalIssues extends Fragment {
	
	public final static String ISSUE_TITLE = "com.battlehack.ISSUE_TITLE";
	public final static String ISSUE_DESCRIPTION = "com.battlehack.ISSUE_DESCRIPTION";
	public final static String ISSUE_IMG = "com.battlehack.ISSUE_IMG";
	public final static String ISSUE_ID = "com.battlehack.ISSUE_ID";
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();;
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
    private boolean asyncFinished = false;
    private JSONArray jsonArray;
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View view = inflater.inflate(R.layout.activity_map, container, false);

		// Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) this.getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();

        //InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(
        //	      Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(this.getActivity().findViewById(R.id.map).getWindowToken(), 0);
        	
		LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
		
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        AsyncTaskRunner task = new AsyncTaskRunner();
        task.execute("" + location.getLatitude() , "" + location.getLongitude(), "" + 1);
        
        while (!asyncFinished) {
        	//wait
        }
        
        List<LatLng> points = new ArrayList<LatLng>(); // route is instance of PolylineOptions 

        LatLngBounds.Builder bc = new LatLngBounds.Builder();
        points.add(currentLocation);
        
        for (LatLng item : points) {
            bc.include(item);
        }
        
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19));
        
        for (int i = 0; i < jsonArray.length(); i++) {
        	try {
				JSONObject json = jsonArray.getJSONObject(i);
				LatLng latLngTemp = new LatLng(json.getDouble("latitude"), json.getDouble("longitude"));
				points.add(latLngTemp);
				
				Marker markerTmp = map.addMarker(new MarkerOptions()
                .title(json.getString("title"))
                .snippet(json.getString("description"))
                .position(latLngTemp));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    
        //((TextView)android.findViewById(R.id.textView)).setText("Android");
        
        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
            	for (int i = 0; i < jsonArray.length(); i++) {
                	try {
        				JSONObject json = jsonArray.getJSONObject(i);
        				if(marker.getTitle().equals(json.getString("title"))){
        					startIntent(json);
        				}
        			} catch (JSONException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
                }
            	
            	return false;
            }
        });
        return view;
}
	public void startIntent(JSONObject jsonIntent) {
		Intent intent = new Intent(this.getActivity() , IssueActivity.class);
    	try {
			intent.putExtra(ISSUE_TITLE, jsonIntent.getString("title"));
			intent.putExtra(ISSUE_DESCRIPTION, jsonIntent.getString("description"));
	    	intent.putExtra(ISSUE_IMG, jsonIntent.getString("image_url"));
	    	intent.putExtra(ISSUE_ID, jsonIntent.getString("id"));
	    	startActivity(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	}

	class AsyncTaskRunner extends AsyncTask<String, String, String> {


		private String resp;

		@Override
		protected String doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost("http://empowered-locals.herokuapp.com/api/v1/issue/nearby");
			request.setHeader("Content-Type", "application/json");
			
			JSONObject json = new JSONObject();

			try {
				json.put("latitude", params[0]);
				json.put("longitude", params[1]);
				json.put("mile_radius", params[2]);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				request.setEntity(new StringEntity(json.toString()));
				
				HttpResponse response = httpClient.execute(request);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    response.getEntity().getContent(), "UTF-8"));
	            String sResponse;
	            StringBuilder s = new StringBuilder();
	 
	            while ((sResponse = reader.readLine()) != null) {
	                s = s.append(sResponse);
	            }
	            
	            JSONObject jsonResponse = new JSONObject(s.toString());
	            
	            jsonArray = new JSONArray(jsonResponse.get("issues").toString());
	           
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			asyncFinished = true;
			return resp;
		}

		@Override
		protected void onPostExecute(String result) {
		}


		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
		}

		@Override
		protected void onProgressUpdate(String... text) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
		}
	}
}
