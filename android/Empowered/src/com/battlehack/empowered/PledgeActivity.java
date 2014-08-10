package com.battlehack.empowered;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PledgeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pledge);
		// Show the Up button in the action bar.
		setupActionBar();

		Intent intent = getIntent();
		String pledgeId = intent.getStringExtra(DashboardActivity.PLEDGE_ID);
		String issueId = intent.getStringExtra(DashboardActivity.ISSUE_ID);
		String pledgeAmount = intent
				.getStringExtra(DashboardActivity.PLEDGE_AMOUNT);

		AsyncTaskRunner task = new AsyncTaskRunner();
		task.execute(pledgeId, issueId, pledgeAmount);

		TextView tvSuccess = (TextView) findViewById(R.id.pledgeSuccess);

		tvSuccess
				.setText("Congratulations John for donating: $" + pledgeAmount);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pledge, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class AsyncTaskRunner extends AsyncTask<String, String, String> {

		private String resp;

		@Override
		protected String doInBackground(String... params) {

			JSONObject json = new JSONObject();

			try {
				json.put("paypal_id", params[0]);
				json.put("issue_id", params[1].replace("\"", ""));
				json.put("pledge_amount", params[2]);
				json.put("user_id", "John");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(
					"http://empowered-locals.herokuapp.com/api/v1/pledge");
			request.setHeader("Content-Type", "application/json");

			try {
				request.setEntity(new StringEntity(json.toString()));

				httpClient.execute(request);

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return resp;
		}
	}

}
