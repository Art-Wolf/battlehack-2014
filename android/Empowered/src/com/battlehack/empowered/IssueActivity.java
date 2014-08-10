package com.battlehack.empowered;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import com.battlehack.empowered.DashboardActivity.ImageUploadRunner;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class IssueActivity extends Activity {

	public final static String PLEDGE_ID = "com.battlehack.PLEDGE_ID";
	public final static String ISSUE_ID = "com.battlehack.ISSUE_ID";
	public final static String PLEDGE_AMOUNT = "com.battlehack.PLEDGE_AMOUNT";
	
	private String pledgeAmount = "";
	private String pledgeID = "";
	private String issueId = "";
	
	Bitmap bmImage;
	boolean async = false;
	
	private static final String TAG = "paymentExample";
    /**
     * - Set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money.
     * 
     * - Set to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * 
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AfeYLhAwa__zrcfT17Tm8WEbZ9vBaoQpUI5GCfVxMJqQWgM1LpfrPTHloG0U";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	    Log.d("onActivityResult","onActivityResult:" + requestCode);
	    switch(requestCode) { 
	    case REQUEST_CODE_PAYMENT:
	    	if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                		imageReturnedIntent.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                    	pledgeID = confirm.toJSONObject().getJSONObject("response").getString("id"); // toString(4));
                        //Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                    	Intent intent = new Intent(this,PledgeActivity.class);
                    	intent.putExtra(PLEDGE_ID, pledgeID);
                    	intent.putExtra(ISSUE_ID, issueId);
                    	intent.putExtra(PLEDGE_AMOUNT, pledgeAmount);
                    	startActivity(intent);
                    	

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
	    	}

	    }
    }
	    
	public void pledge(View view)
	{	
		PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

		Intent intent = new Intent(this, PaymentActivity.class);

		intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

		startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    
	}

	private PayPalPayment getThingToBuy(String paymentIntentSale) {
		Spinner spin = (Spinner) findViewById(R.id.spPledgeAmount);
		pledgeAmount = (String) spin.getSelectedItem();
	    return new PayPalPayment(new BigDecimal((String) spin.getSelectedItem()), "USD", "Pledge",
	    		paymentIntentSale);
	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue);
		
		Intent intent = getIntent();
		issueId = intent.getStringExtra(LocalIssues.ISSUE_ID);
		String issueTitle = intent.getStringExtra(LocalIssues.ISSUE_TITLE);
		String issueDescription = intent.getStringExtra(LocalIssues.ISSUE_DESCRIPTION);
		String issueImageURL = intent.getStringExtra(LocalIssues.ISSUE_IMG);
		
		Spinner spinner = (Spinner) findViewById(R.id.spPledgeAmount);
	     // Spinner click listener
	      //  spinner.setOnItemSelectedListener((OnItemSelectedListener) this);
	 
	        // Spinner Drop down elements
	        List<String> categories = new ArrayList<String>();
	        categories.add("1.00");
	        categories.add("2.00");
	        categories.add("5.00");
	        categories.add("10.00");
	        categories.add("15.00");
	 
	        // Creating adapter for spinner
	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, categories);
	 
	        // Drop down layout style - list view with radio button
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 
	        // attaching data adapter to spinner
	        spinner.setAdapter(dataAdapter);
	        
		DownloadImageTask task = new DownloadImageTask();
	
		task.execute(issueImageURL);
		
		while (!async) {
			//wait
		}
		
		ImageView image = (ImageView) findViewById(R.id.imageView1);
				
		image.setImageBitmap(bmImage);
		
		TextView tvTitle = (TextView) findViewById(R.id.tvIssueTitle);
		tvTitle.setText(issueTitle);
		
		TextView tvDesc = (TextView) findViewById(R.id.tvIssueDescription);
		tvDesc.setText(issueDescription);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.issue, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    

	    public DownloadImageTask() {
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        
	        try {
	        	bmImage = downloadBitmap(urldisplay);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return bmImage;
	    }

	    private Bitmap downloadBitmap(String url) {
	    	Bitmap image = null;
	        // initilize the default HTTP client object
	        final DefaultHttpClient client = new DefaultHttpClient();

	        //forming a HttoGet request 
	        final HttpGet getRequest = new HttpGet(url);
	        try {

	            HttpResponse response = client.execute(getRequest);

	            //check 200 OK for success
	            final int statusCode = response.getStatusLine().getStatusCode();

	            if (statusCode != HttpStatus.SC_OK) {
	                Log.w("ImageDownloader", "Error " + statusCode + 
	                        " while retrieving bitmap from " + url);
	                return null;

	            }

	            final HttpEntity entity = response.getEntity();
	            if (entity != null) {
	                InputStream inputStream = null;
	                try {
	                    // getting contents from the stream 
	                    inputStream = entity.getContent();

	                    // decoding stream data back into image Bitmap that android understands
	                    image = BitmapFactory.decodeStream(inputStream);


	                } finally {
	                    if (inputStream != null) {
	                        inputStream.close();
	                    }
	                    entity.consumeContent();
	                }
	            }
	        } catch (Exception e) {
	            // You Could provide a more explicit error message for IOException
	            getRequest.abort();
	            Log.e("ImageDownloader", "Something went wrong while" +
	                    " retrieving bitmap from " + url + e.toString());
	        } 

	        async = true;
	        return image;
	    }
	}
}
