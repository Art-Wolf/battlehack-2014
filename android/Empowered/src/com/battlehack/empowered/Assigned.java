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

public class Assigned extends Fragment {
	
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<String>();;
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
    boolean asyncFinished = false;
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View view = inflater.inflate(R.layout.frag_myissues, container, false);

        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        
        // preparing list data
        //prepareListData();
        AsyncTaskRunner task = new AsyncTaskRunner();
        task.execute();
        
        while (!asyncFinished) {
        	//wait for the webcall...
        	
        }
        
        if (listDataHeader.size() > 0 ) {
        	listAdapter = new ExpandableListAdapter(this.getActivity(), listDataHeader, listDataChild);
 
        	// setting list adapter
        	expListView.setAdapter(listAdapter);
        }
        //((TextView)android.findViewById(R.id.textView)).setText("Android");
        return view;
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
	            
	            JSONArray jsonArray = new JSONArray(s.toString());
	            
	            for (int i = 0; i < jsonArray.length(); i++) {
	            	JSONObject json = jsonArray.getJSONObject(i);
	            	listDataHeader.add(json.getString("title"));
	            	List<String> top250 = new ArrayList<String>();
	                top250.add(json.getString("title"));
	                top250.add("Total Pledged: $140");
	                top250.add("Assign To Me");
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
