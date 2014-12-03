package br.com.split.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import br.com.split.R;
import br.com.split.controller.SplitApplication;

import com.facebook.FacebookException;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;

public class FriendPickerActivity extends FragmentActivity// implements LocationListener
{
	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	public static final Uri PLACE_PICKER = Uri.parse("picker://place");
	public static final String TAG = "FRIENDPICKER";

	private FriendPickerFragment friendPickerFragment;
	private PlacePickerFragment placePickerFragment;
	private LocationListener locationListener;
	private static final int SEARCH_RADIUS_METERS = 1000;
	private static final int SEARCH_RESULT_LIMIT = 50;
	private static final String SEARCH_TEXT = "bar";
	private static final int LOCATION_CHANGE_THRESHOLD = 50; // meters
	private static final Location PORTO_ALEGRE_LOCATION = new Location("") {
		{
			// TODO: Colocar aqui a localizacao atual obtida pelo GPS.
			setLatitude(-30.034656);
			setLongitude(-51.2176584);
		}
	};
	private double latitude;
	private double longitude;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_escolher_amigos);

		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Fragment fragmentToShow = null;

		Uri intentUri = getIntent().getData();

		if (FRIEND_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				friendPickerFragment = new FriendPickerFragment(args);
				friendPickerFragment.setFriendPickerType(FriendPickerFragment.FriendPickerType.TAGGABLE_FRIENDS);
			} else {
				friendPickerFragment = (FriendPickerFragment) manager
						.findFragmentById(R.id.picker_fragment);
			}
			// Customizações da tela do FriendPicker do facebook
			friendPickerFragment.setDoneButtonText(getResources().getString(
					R.string.acao_feito));
			friendPickerFragment.setTitleText(getResources().getString(
					R.string.tela_amigos_titulo));

			// Configura o listener para caso de erro
			friendPickerFragment
					.setOnErrorListener(new PickerFragment.OnErrorListener() {
						@Override
						public void onError(PickerFragment<?> fragment,
								FacebookException error)
						{
							FriendPickerActivity.this.onError(error);
						}
					});
			// Configura o listener para o clique no botao
			friendPickerFragment
					.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
						@Override
						public void onDoneButtonClicked(
								PickerFragment<?> fragment)
						{
							finishActivity();
						}
					});
			fragmentToShow = friendPickerFragment;

		} else if (PLACE_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				placePickerFragment = new PlacePickerFragment(args);

			} else {
				placePickerFragment = (PlacePickerFragment) manager
						.findFragmentById(R.id.picker_fragment);
			}
			placePickerFragment.setDoneButtonText(getResources().getString(R.string.acao_feito));
			placePickerFragment.setTitleText(getResources().getString(R.string.localizacao_titulo));
			placePickerFragment
					.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
						@Override
						public void onSelectionChanged(
								PickerFragment<?> fragment)
						{
							finishActivity(); // call finish since you can only
							// pick one place
						}
					});
			placePickerFragment
					.setOnErrorListener(new PickerFragment.OnErrorListener() {
						@Override
						public void onError(PickerFragment<?> fragment,
								FacebookException error)
						{
							FriendPickerActivity.this.onError(error);
						}
					});
			placePickerFragment
					.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
						@Override
						public void onDoneButtonClicked(
								PickerFragment<?> fragment)
						{
							finishActivity();
						}
					});
			fragmentToShow = placePickerFragment;
		} else {
			// Nothing to do, finish
			setResult(RESULT_CANCELED);
			finish();
			return;
		}

		manager.beginTransaction()
				.replace(R.id.picker_fragment, fragmentToShow).commit();
	}

	private void onError(Exception error)
	{
		onError(error.getLocalizedMessage(), false);
	}

	private void onError(String error, final boolean finishActivity)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.erro_dialogo_titulo)
				.setMessage(error)
				.setPositiveButton(R.string.erro_dialogo_texto_botao,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i)
							{
								if (finishActivity) {
									finishActivity();
								}
							}
						});
		builder.show();
	}

	private void finishActivity()
	{
		SplitApplication splitApp = (SplitApplication) getApplication();
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			if (friendPickerFragment != null) {
				splitApp.setSelectedUsers(friendPickerFragment.getSelection());
			}
		} else if (PLACE_PICKER.equals(getIntent().getData())) {
			if (placePickerFragment != null) {
				splitApp.setSelectedPlace(placePickerFragment.getSelection());
			}
		}
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			try {
				friendPickerFragment.loadData(false);
			} catch (Exception ex) {
				onError(ex);
			}
		} else if (PLACE_PICKER.equals(getIntent().getData())) {
			try {
				Location location = null;
				// Instantiate the default criteria for a location provider
				Criteria criteria = new Criteria();
				// Get a location manager from the system services
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				// Get the location provider that best matches the criteria
				String bestProvider = locationManager.getBestProvider(criteria,
						false);
				if (bestProvider != null) {
					// Get the user's last known location
					location = locationManager
							.getLastKnownLocation(bestProvider);
					if (locationManager.isProviderEnabled(bestProvider)
							&& locationListener == null) {
						// Set up a location listener if one is not already set
						// up
						// and the selected provider is enabled
						locationListener = new LocationListener() {
							@Override
							public void onLocationChanged(Location location)
							{
								// On location updates, compare the current
								// location to the desired location set in the
								// place picker
								float distance = location
										.distanceTo(placePickerFragment
												.getLocation());
								if (distance >= LOCATION_CHANGE_THRESHOLD) {
									placePickerFragment.setLocation(location);
									placePickerFragment.loadData(true);
								}
							}

							@Override
							public void onStatusChanged(String s, int i,
									Bundle bundle)
							{
							}

							@Override
							public void onProviderEnabled(String s)
							{
							}

							@Override
							public void onProviderDisabled(String s)
							{
							}
						};
						locationManager.requestLocationUpdates(bestProvider, 1,
								LOCATION_CHANGE_THRESHOLD, locationListener,
								Looper.getMainLooper());
					}
				}
				if (location == null) {
					// Set the default location if there is no
					// initial location
					//					String model = Build.MODEL;
					//					if (model.equals("sdk") || model.equals("google_sdk")
					//							|| model.contains("x86")) {
					//						// This may be the emulator, use the default location
					//						location = PORTO_ALEGRE_LOCATION;
					//					}
//					location = PORTO_ALEGRE_LOCATION;
//					location = getLocation();
					location = new Location("") {
						{
							setLatitude(latitude);
							setLongitude(longitude);
						}
					};
				}
				if (location != null) {
					// Configure the place picker: search center, radius,
					// query, and maximum results.
					placePickerFragment.setLocation(location);
					placePickerFragment.setRadiusInMeters(SEARCH_RADIUS_METERS);
					placePickerFragment.setSearchText(SEARCH_TEXT);
					placePickerFragment.setResultsLimit(SEARCH_RESULT_LIMIT);
					// Start the API call
					placePickerFragment.loadData(true);
				} else {
					// If no location found, show an error

					onError(getResources().getString(
							R.string.erro_sem_localizacao), true);
				}
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		if (locationListener != null) {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// Remove updates for the location listener
			locationManager.removeUpdates(locationListener);
			locationListener = null;
		}
	}

	private void atualizarLocalizacao()
	{
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		Location localizacao = locationManager.getLastKnownLocation(provider);
		this.latitude = localizacao.getLatitude();
		this.longitude = localizacao.getLongitude();
	}

//	@Override
//	public void onLocationChanged(Location location)
//	{
//		this.latitude = location.getLatitude();
//		this.longitude = location.getLongitude();
//	}
//
//	@Override
//	public void onProviderDisabled(String provider)
//	{
//	}
//
//	@Override
//	public void onProviderEnabled(String provider)
//	{
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras)
//	{
//	}
}
