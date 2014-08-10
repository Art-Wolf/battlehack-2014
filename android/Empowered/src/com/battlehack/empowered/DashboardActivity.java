package com.battlehack.empowered;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DashboardActivity extends FragmentActivity implements LocationListener {
	public final static String PLEDGE_ID = "com.battlehack.PLEDGE_ID";
	public final static String ISSUE_ID = "com.battlehack.ISSUE_ID";
	public final static String PLEDGE_AMOUNT = "com.battlehack.PLEDGE_AMOUNT";
	
	ViewPager Tab;
	TabPagerAdapter TabAdapter;
	ActionBar actionBar;
	private LocationManager locationManager;
	private String longitude;
	private String latitude;
	private static final int SELECT_PICTURE = 65537;
	private String fileLocation = "";
	
	private String pledgeAmount = "";
	private String pledgeID = "";
	private String issueId = "";
	
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
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AfeYLhAwa__zrcfT17Tm8WEbZ9vBaoQpUI5GCfVxMJqQWgM1LpfrPTHloG0U";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Hipster Store")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
	
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
	    	break;
	    case SELECT_PICTURE:
	    	 Log.d("onActivityResult","onActivityResult:" + SELECT_PICTURE);
	        if(resultCode == RESULT_OK){  
	        	Uri selectedImage = imageReturnedIntent.getData();
	        	ImageUploadRunner runner = new ImageUploadRunner();
				runner.execute(selectedImage.toString());
				Button imageButton = (Button) findViewById(R.id.button2);
				imageButton.setText("Selected");
				imageButton.setEnabled(false);
				
				while (fileLocation == "") {
					// wait for id
				}
				 
				Button createButton = (Button) findViewById(R.id.button1);
				createButton.setEnabled(true);
				
	        }
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
				3000,   // 3 sec
				10, this);

		
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

	public void onLocationChanged(Location location) {
	}


	public void createIssue(View view) {
		EditText createTitle = (EditText) findViewById(R.id.etCreateTitle);
		EditText createdBy = (EditText) findViewById(R.id.etCreatedBy);
		//EditText createImageURL = (EditText) findViewById(R.id.etCreateImageURL);
		EditText createDescription = (EditText) findViewById(R.id.etCreateDescription);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  

		longitude = "" + location.getLongitude();
		latitude = "" + location.getLatitude();

		try {
			AsyncTaskRunner runner = new AsyncTaskRunner();
			runner.execute(createTitle.getText().toString(), 
					createdBy.getText().toString(), 
					"http://empowered-locals.herokuapp.com/" + fileLocation.replace("\"", ""), //createImageURL.getText().toString(),
					createDescription.getText().toString(),
					longitude, 
					latitude);

			PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

	        Intent intent = new Intent(this, PaymentActivity.class);

	        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

	        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
	        
		} finally {}
	}

	private PayPalPayment getThingToBuy(String paymentIntentSale) {
			Spinner spin = (Spinner) findViewById(R.id.spPledgeAmount);
			pledgeAmount = (String) spin.getSelectedItem();
		    return new PayPalPayment(new BigDecimal((String) spin.getSelectedItem()), "USD", "Pledge",
		    		paymentIntentSale);
	    
	}

	class AsyncTaskRunner extends AsyncTask<String, String, String> {


		private String resp;

		@Override
		protected String doInBackground(String... params) {

			JSONObject json = new JSONObject();

			try {
				json.put("title", params[0]);
				json.put("submitted_by", params[1]);
				json.put("image_url", params[2]);
				json.put("description", params[3]);
				json.put("longitude", params[4]);
				json.put("latitude", params[5]);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost("http://empowered-locals.herokuapp.com/api/v1/issue");
			request.setHeader("Content-Type", "application/json");
			
			
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
	            
	            issueId = s.toString();
				
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

	class ImageUploadRunner extends AsyncTask<String, String, String> {


		private String resp;

		@Override
		protected String doInBackground(String... params) {

			String uriString = params[0];
			Uri selectedImage = Uri.parse(uriString);
			
            InputStream imageStream;
			try {
				imageStream = getContentResolver().openInputStream(selectedImage);
				Bitmap image = BitmapFactory.decodeStream(imageStream);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				image.compress(CompressFormat.JPEG, 75, bos);
				byte[] data = bos.toByteArray();
	            HttpClient httpClient = new DefaultHttpClient();
	            HttpPost postRequest = new HttpPost(
	                    "http://empowered-locals.herokuapp.com/api/v1/upload");
	            ByteArrayBody bab = new ByteArrayBody(data, "issue.jpg");
	            // File file= new File("/mnt/sdcard/forest.png");
	            // FileBody bin = new FileBody(file);
	            MultipartEntity reqEntity = new MultipartEntity(
	                    HttpMultipartMode.BROWSER_COMPATIBLE);
	            reqEntity.addPart("image_file", bab);
	            //reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
	            postRequest.setEntity(reqEntity);
	            HttpResponse response = httpClient.execute(postRequest);
	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    response.getEntity().getContent(), "UTF-8"));
	            String sResponse;
	            StringBuilder s = new StringBuilder();
	 
	            while ((sResponse = reader.readLine()) != null) {
	                s = s.append(sResponse);
	            }
	            
	            fileLocation = s.toString();
				
	            fileLocation.replace("\"", "");
	           
	            
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return resp;
		}

		public void execute(Bitmap yourSelectedImage) {
			// TODO Auto-generated method stub
			
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

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
