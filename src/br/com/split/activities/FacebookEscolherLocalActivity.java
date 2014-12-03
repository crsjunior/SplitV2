/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.split.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import br.com.split.controller.SplitApplication;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class FacebookEscolherLocalActivity extends FragmentActivity implements LocationListener
{
	private static final int PLACE_ACTIVITY = 1;
	private LocationManager locationManager;
	private Location lastKnownLocation;
	private UiLifecycleHelper lifecycleHelper;
	private Location pickPlaceForLocationWhenSessionOpened = null;
	private static final int SEARCH_RADIUS_METERS = 1000;
	private static final int SEARCH_RESULT_LIMIT = 50;
	private static final String SEARCH_TEXT = "bar";
	private static final int LOCATION_CHANGE_THRESHOLD = 50; // meters
	private static final Location PORTO_ALEGRE_LOCATION = new Location("") {
		{
			setLatitude(-30.034656);
			setLongitude(-51.2176584);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		lifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception)
			{
				onSessionStateChanged(session, state, exception);
			}
		});
		lifecycleHelper.onCreate(savedInstanceState);

		ensureOpenSession();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		onClickGPS();
	}

	private boolean ensureOpenSession()
	{
		if (Session.getActiveSession() == null ||
				!Session.getActiveSession().isOpened()) {
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception)
				{
					onSessionStateChanged(session, state, exception);
				}
			});
			return false;
		}
		return true;
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// Update the display every time we are started (this will be "no place selected" on first
		// run, or possibly details of a place if the activity is being re-created).
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		lifecycleHelper.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		lifecycleHelper.onPause();

		// Call the 'deactivateApp' method to log an app event for use in analytics and advertising
		// reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		lifecycleHelper.onResume();

		// Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
		// the onResume methods of the primary Activities that an app may be launched into.
		AppEventsLogger.activateApp(this);
	}

	private void onError(Exception exception)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Error").setMessage(exception.getMessage()).setPositiveButton("OK", null);
		builder.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		lifecycleHelper.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	private void onSessionStateChanged(Session session, SessionState state, Exception exception)
	{
		if (pickPlaceForLocationWhenSessionOpened != null && state.isOpened()) {
			Location location = pickPlaceForLocationWhenSessionOpened;
			pickPlaceForLocationWhenSessionOpened = null;
			startPickPlaceActivity(location);
		}
	}

	public void onLocationChanged(Location location)
	{
		lastKnownLocation = location;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	private void startPickPlaceActivity(Location location)
	{
		if (ensureOpenSession()) {
			SplitApplication application = (SplitApplication) getApplication();
			application.setSelectedPlace(null);

			Intent intent = new Intent(this, FacebookEscolherLocal.class);
			FacebookEscolherLocal.populateParameters(intent, location, null);

			startActivityForResult(intent, PLACE_ACTIVITY);
		} else {
			pickPlaceForLocationWhenSessionOpened = location;
		}
	}

	//TODO: Melhorar este método
	private void onClickGPS()
	{
		try {
			if (lastKnownLocation == null) {
				Criteria criteria = new Criteria();
				String bestProvider = locationManager.getBestProvider(criteria, false);
				if (bestProvider != null) {
					lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
				}
			}
			if (lastKnownLocation == null) {
				String model = android.os.Build.MODEL;
				if (model.equals("sdk") || model.equals("google_sdk") || model.contains("x86")) {
					// Looks like they are on an emulator, pretend we're in Paris if we don't have a
					// location set.
					lastKnownLocation = PORTO_ALEGRE_LOCATION;
				} else {
					//onError(new Exception(getString(R.string.erro_sem_localizacao)));
					//return;
					lastKnownLocation = PORTO_ALEGRE_LOCATION;
				}
			}
			startPickPlaceActivity(lastKnownLocation);
		} catch (Exception ex) {
			onError(ex);
		}
	}

}
