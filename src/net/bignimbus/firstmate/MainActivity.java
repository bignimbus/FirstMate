package net.bignimbus.firstmate;

import java.util.ArrayList;
import java.util.List;

import com.projectsbynimbus.intents.R;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity implements LocationListener,
		OnItemSelectedListener {

	int request_Code = 1;
	private EditText start;
	private EditText end;
	public LocationListener locationListener;
	public LocationManager locationManager;
	public Location location;
	private String provider;
	public String coords = "Location not available";
	public Spinner spinner;
	public String vehicle;
	public String vehicleInitial = "c";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (location != null) {
			coords = String.valueOf(location.getLatitude() + ","
					+ String.valueOf(location.getLongitude()));
		} else {
			coords = "Location not available";
		}

		// Spinner element
		spinner = (Spinner) findViewById(R.id.spinner);

		// Spinner click listener
		spinner.setOnItemSelectedListener(this);

		// Spinner Drop down elements
		// ---Can I convert to google maps tokens here?---
		List<String> categories = new ArrayList<String>();
		categories.add("Drive");
		categories.add("Mass Transit");
		categories.add("Walk");
		categories.add("Bicycle");

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	// listen for location changes, update location & coords
	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			coords = String.valueOf(location.getLatitude() + ","
					+ String.valueOf(location.getLongitude()));
		} else {
			coords = "Location not available";
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// On selecting a spinner item
		vehicle = parent.getItemAtPosition(position).toString();

		if ("Drive".equals(vehicle)) {
			vehicleInitial = "c";
		} else if ("Mass Transit".equals(vehicle)) {
			vehicleInitial = "r";
		} else if ("Walk".equals(vehicle)) {
			vehicleInitial = "w";
		} else if ("Bicycle".equals(vehicle)) {
			vehicleInitial = "b";
		} else {
			vehicleInitial = "c";
		}
	}

	// assume driving directions unless one is selected. (Failsafe)
	public void onNothingSelected(AdapterView<?> arg0) {
		vehicleInitial = "c";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// switch start & destination fields for return directions
	public void onClickFlip(View view) {
		start = (EditText) findViewById(R.id.start);
		end = (EditText) findViewById(R.id.end);
		String startFlip = ((EditText) findViewById(R.id.start)).getText()
				.toString();
		String endFlip = ((EditText) findViewById(R.id.end)).getText()
				.toString();
		end.setText(startFlip);
		start.setText(endFlip);
	}

	// called when user clicks the GPS location button
	public void onClickGetGPS(View view) {
		start = (EditText) findViewById(R.id.start); // convert start to string
		location = locationManager.getLastKnownLocation(provider); // update
																	// location
		coords = String.valueOf(location.getLatitude() + ","
				+ String.valueOf(location.getLongitude()));
		if (location != null) {
			start.setText(coords);
		} else if (coords != null) {
			start.setText(coords);
		} else {
			start.setText("Location not available");
		}
	}

	public void onClickShowMap(View view) {
		String start = ((EditText) findViewById(R.id.start)).getText()
				.toString();
		String end = ((EditText) findViewById(R.id.end)).getText().toString();
		Intent i = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?saddr=" + start
						+ "&daddr=" + end + "&dirflg=" + vehicleInitial));
		startActivity(i);
	}
}
