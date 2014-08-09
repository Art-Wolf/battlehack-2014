package com.battlehack.empowered;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class DashboardActivity extends FragmentActivity {
	ViewPager Tab;
	TabPagerAdapter TabAdapter;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		TabAdapter = new TabPagerAdapter(getSupportFragmentManager());

		Tab = (ViewPager) findViewById(R.id.pager);
		Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				actionBar = getActionBar();
				actionBar.setSelectedNavigationItem(position);
			}
		});
		Tab.setAdapter(TabAdapter);

		actionBar = getActionBar();
		// Enable Tabs on Action Bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(android.app.ActionBar.Tab tab,
					FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

				Tab.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(android.app.ActionBar.Tab tab,
					FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};
		// Add New Tab
		actionBar.addTab(actionBar.newTab().setText("Create")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("My Issues")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Local Issues")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Assigned")
				.setTabListener(tabListener));

	}
	
	public void createIssue(View view) {
		EditText createTitle = (EditText) findViewById(R.id.etCreateTitle);
		EditText createdBy = (EditText) findViewById(R.id.etCreatedBy);
		EditText createImageURL = (EditText) findViewById(R.id.etCreateImageURL);
		EditText createDescription = (EditText) findViewById(R.id.etCreateDescription);
		EditText  createLongitude = (EditText) findViewById(R.id.etCreateLongitude);
		EditText  createLatitude = (EditText) findViewById(R.id.etCreateLatitude);
		
		JSONObject json = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
		
		try {
			json.put("title", createTitle.getText().toString());
			json.put("created_by", createdBy.getText().toString());
			json.put("image_url", createImageURL.getText().toString());
			json.put("description", createDescription.getText().toString());
			json.put("longitude", createLongitude.getText().toString());
			json.put("latitude",  createLatitude.getText().toString());
			
			HttpPost request = new HttpPost("http://yoururl");
			request.addHeader("content-type", "application/json");
			request.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
			
			HttpResponse response = httpClient.execute(request);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent(this,CreateSuccessActivity.class);
    	startActivity(intent);
	}

}
