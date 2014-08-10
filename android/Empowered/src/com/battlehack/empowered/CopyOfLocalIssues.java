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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;

public class CopyOfLocalIssues extends Fragment {
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();;
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View view = inflater.inflate(R.layout.frag_localissues, container, false);

        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        
        // preparing list data
        //prepareListData();
        AsyncTaskRunner task = new AsyncTaskRunner();
        task.execute();
        
        while (listDataHeader.size() < 1) {
        	//wait for the webcall...
        	
        }
        listAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        //((TextView)android.findViewById(R.id.textView)).setText("Android");
        return view;
}

	private void prepareListData() {
		// TODO Auto-generated method stub
		//listDataHeader = new ArrayList<String>();
        //listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("Top 250");
        listDataHeader.add("Now Showing");
        listDataHeader.add("Coming Soon..");
 
        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");
 
        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");
 
        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");
 
        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
	}
	
	class AsyncTaskRunner extends AsyncTask<String, String, String> {


		private String resp;

		@Override
		protected String doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet("http://empowered-locals.herokuapp.com/api/v1/issue/list");
			request.setHeader("Content-Type", "application/json");
			
			
			try {
				HttpResponse response = httpClient.execute(request);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    response.getEntity().getContent(), "UTF-8"));
	            String sResponse;
	            StringBuilder s = new StringBuilder();
	 
	            while ((sResponse = reader.readLine()) != null) {
	                s = s.append(sResponse);
	            }
	            
	            Log.d("localissues", s.toString());
	            JSONArray jsonArray = new JSONArray(s.toString());
	            
	            for (int i = 0; i < jsonArray.length(); i++) {
	            	JSONObject json = jsonArray.getJSONObject(i);
	            	listDataHeader.add(json.getString("title"));
	            	List<String> top250 = new ArrayList<String>();
	                top250.add("The Shawshank Redemption");
	                top250.add("The Godfather");
	                top250.add("The Godfather: Part II");
	                top250.add("Pulp Fiction");
	                top250.add("The Good, the Bad and the Ugly");
	                top250.add("The Dark Knight");
	                top250.add("12 Angry Men");
	                listDataChild.put(listDataHeader.get(i), top250);
	            }
				
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
