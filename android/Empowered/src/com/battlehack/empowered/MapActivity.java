package com.battlehack.empowered;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.MapFragment;

public class MapActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		// Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);  
		
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        List<LatLng> points = new ArrayList<LatLng>(); // route is instance of PolylineOptions 

        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        points.add(currentLocation);
        
        for (LatLng item : points) {
            bc.include(item);
        }
        
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19));
        
        
        map.addMarker(new MarkerOptions()
                .title("BATTLEHACK")
                .snippet("Awesome Stuff!")
                .position(currentLocation));
        
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
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
}
