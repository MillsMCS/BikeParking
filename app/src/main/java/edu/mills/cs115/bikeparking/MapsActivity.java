package edu.mills.cs115.bikeparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The top-level activity for Bike Parking. The accompanying view
 * enables users to display {@link MapsActivity} and {@link RackFragment}.
 */
public class MapsActivity extends AppCompatActivity implements
        OnMarkerClickListener, OnMapReadyCallback {

    static Marker currentMarker;
    private GoogleMap mMap;
    private ShareActionProvider shareActionProvider;
    private LatLng currentCoords;
    private Boolean clicked = false;
    private float zoomLevel = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (savedInstanceState != null) {
            clicked = savedInstanceState.getBoolean("clicked");
            currentCoords = new LatLng(savedInstanceState.getDouble("lat"), savedInstanceState.getDouble("lng"));
        }
    }

    @Override
    /**
     * Creates and inflates the toolbar.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shareMenuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        setShareActionIntent("@string/share_action_text");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**
     * Called when the user clicks on toolbar items.
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.add_rack) {
            Intent myIntent = new Intent(MapsActivity.this, AddRackActivity.class);
            MapsActivity.this.startActivity(myIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }//setShareActionIntent

    /**
     *Sets up the initial googleMaps instance view.
     *
     * @param googleMap, the googleMap instance that is displayed
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (servicesOK()) {
            try {
                String url = "https://naclo.cs.umass.edu/cgi-bin/bikeparkingserver/get-rack.py";
                LatLng Court_Stevenson = new LatLng(37.781292, -122.186266);
                LatLng Court_Stevenson2 = new LatLng(37.7814257, -122.1863674);
                LatLng Underwood_BuildingA = new LatLng(37.7810884, -122.185789);
                LatLng WarrenOlney = new LatLng(37.782181, -122.182206);
                LatLng MillsCollege = new LatLng(37.781004, -122.182827);

                BitmapDescriptor bdf = BitmapDescriptorFactory.fromResource(R.drawable.bikecon);
                mMap.addMarker(new MarkerOptions()
                        .position(Court_Stevenson)
                        .icon(bdf)
                );
                mMap.addMarker(new MarkerOptions()
                        .position(Court_Stevenson2)
                        .icon(bdf)
                );
                mMap.addMarker(new MarkerOptions()
                        .position(Underwood_BuildingA)
                        .icon(bdf)
                );
                mMap.addMarker(new MarkerOptions()
                        .position(WarrenOlney)
                        .icon(bdf)
                );

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MillsCollege, zoomLevel));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MillsCollege, zoomLevel));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.fragment_rack, null);
                        // Other information to set the data goes here
                        return v;
                    }
                });

            } catch (Exception e) {
                Toast toast = Toast.makeText(this,
                        this.getString(R.string.google_play_not_installed),
                        Toast.LENGTH_SHORT);
                toast.show();
            }

        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            /**
             * Allows the GoogleMaps instance to be manipulated
             */
            public void onMapClick(final LatLng currentCoords) {
                final double finalLongitude = currentCoords.longitude;
                final double finalLatitude = currentCoords.latitude;
                if (currentCoords != null) {
                    Marker currentMarker = mMap.addMarker(new MarkerOptions().position(currentCoords));
                    Intent edit = new Intent(MapsActivity.this, AddRackActivity.class);
                    edit.putExtra("longitude", finalLongitude);
                    edit.putExtra("latitude", finalLatitude);
                    startActivity(edit);
                    currentMarker.setVisible(true);
                    currentMarker.showInfoWindow();
                }
            }
        });
    }


    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the info window.
        if (clickCount != null) {
            marker.showInfoWindow();
            currentMarker = marker;
            clicked = true;
        }
        return false;
    }

    /**
     * Checks to see if the users device has GooglePlayServices installed.
     *
     * @return true if device contains googlePlayServices, otherwise false
     */
    public boolean servicesOK() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Toast.makeText(this, this.getString(R.string.google_play_cannot_connect),
                    Toast.LENGTH_LONG).show();
        }
        return false;
    }//ServicesOk

    /**
     * Saves the activity state of the app.
     *
     * @param savedInstanceState, the bundle that saved the activity state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Integer saveMarker = null;
        LatLng coords = null;
        if (currentMarker != null) {
            saveMarker = (Integer) currentMarker.getTag();
            coords = currentMarker.getPosition();
        }
        if (saveMarker != null) {
            savedInstanceState.putInt("markerTag", saveMarker);
        }
        if (coords != null) {
            savedInstanceState.putDouble("lat", coords.latitude);
            savedInstanceState.putDouble("lng", coords.longitude);
        }
        savedInstanceState.putBoolean("clicked", clicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "Activity is being resumed");
    }
}