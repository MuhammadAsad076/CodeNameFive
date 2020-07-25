package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.utils.NetworkConnectionState;
import com.itridtechnologies.codenamefive.utils.NetworkErrorDialog;

public class PartnerDashboardMain extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "PartnerDashboardMain";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private boolean mLocationPermissionGranted = false;
    private int progress = 0;

    private Button goOnline;
    private Button goOffline;
    private ProgressBar progressBar;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_dashboard_main);

        //find views
        goOnline = findViewById(R.id.btn_go_online);
        goOffline = findViewById(R.id.btn_go_offline);

        //set listener
        goOnline.setOnClickListener(this);
        goOffline.setOnClickListener(this);

        //check GPS & NETWORK
        if (isNetworkOk()) {
            if (isGpsOk()) {
                Log.d(TAG, "onCreate: network and gps is oky..");
                getLocationPermissions();

            } else {
                Log.d(TAG, "onCreate: show gps error.....");
                new AlertDialog.Builder(this)
                        .setTitle("Location is disabled")
                        .setMessage("Please turn on location and services.")
                        .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            }

        } else if (!isNetworkOk()) {
            if (isGpsOk()) {
                Log.d(TAG, "onCreate: show network error....");
                new AlertDialog.Builder(this)
                        .setTitle("You are offline")
                        .setMessage("Please turn on WiFi.")
                        .setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            } else {
                Log.d(TAG, "onCreate: all  error///....");
                new AlertDialog.Builder(this)
                        .setTitle("Network & Gps is disabled")
                        .setMessage("Please turn on location and review your network settings.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create().show();
            }
        }

    }//onCreate

    //public methods________________________________________________________________________________

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device current location...");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: location found!");
                            Location currentLocation = (Location) task.getResult();
                            //after getting current location , move camera to current device location
                            if (isNetworkOk()) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            } else {
                                Toast.makeText(PartnerDashboardMain.this, "Network Error !!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "onComplete: location not found!");
                            Toast.makeText(PartnerDashboardMain.this, "Unable to find current location !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }//end if

        } catch (SecurityException ex) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + ex.getMessage());
        }

    }//end getLocation

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to, lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }//end moveCamera

    public void getLocationPermissions() {
        Log.d(TAG, "getLocationPermissions: getting location permissions..");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                //initializing the map, when all permissions are granted
                Log.d(TAG, "getLocationPermissions: permissions are okay, init google map");
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }//end if
        else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }//end request permissions

    public void initMap() {
        Log.d(TAG, "initMap: initializing the map..");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(PartnerDashboardMain.this);
    }//end method initMap

    public boolean isGpsOk() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "isGpsOk: " + gps_enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gps_enabled;
    }//end gpsOk

    //
    public boolean isNetworkOk() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            Log.d(TAG, "isNetworkOk: " + connected);

        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }//end method

    //override methods

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed..");
                            return;
                        }
                    }//end for
                }
                mLocationPermissionGranted = true;
                Log.d(TAG, "onRequestPermissionsResult: permission granted..");
                // initialize map
                initMap();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }//end switch
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map Is Ready !", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        //..
        if (mLocationPermissionGranted) {
            getDeviceLocation();

            //set a little marker to device location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //getting rid of default recenter Location button
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }//end if
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_go_online:
                if (NetworkConnectionState.isNetworkConnected(this)) {
                    updateUI("online");
                    updateProgressBar(progress);
                } else {
                    updateUI("networkError");
                    Toast.makeText(this, "You are offline !", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_go_offline:
                updateUI("offline");
                break;

        }//end switch
    }

    private void updateProgressBar(final int progress) {
        progressBar = findViewById(R.id.progressBar_finding_rides);
        final int pMax = progressBar.getMax();
        // set the progress
        progressBar.setProgress(progress);
        // thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (progress < pMax) {
                    Log.d(TAG, "run: update progress was called" + progress);
                    updateProgressBar(progress + 20);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        thread.start();
    }//end setProgressBar

    private void updateUI(String status) {

        TextView MSG_FINDING_RIDES = findViewById(R.id.msg_rider_offline);
        TextView MSG_RIDER_ONLINE = findViewById(R.id.msg_rider_online);
        TextView MSG_CONNECTED_TIME = findViewById(R.id.msg_time_connected);
        TextView CONNECTED_TIME = findViewById(R.id.tv_time_connected);
        TextView MSG_OFFLINE = findViewById(R.id.msg_you_are_offline);
        TextView MSG_CONTACT_SUPPORT = findViewById(R.id.msg_contact_support);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout_ps);

        switch (status) {

            case "online":

                //update buttons and textViews
                MSG_FINDING_RIDES.setVisibility(View.INVISIBLE);
                MSG_RIDER_ONLINE.setVisibility(View.VISIBLE);
                MSG_CONNECTED_TIME.setVisibility(View.VISIBLE);
                CONNECTED_TIME.setVisibility(View.VISIBLE);
                goOffline.setVisibility(View.VISIBLE);
                goOnline.setVisibility(View.INVISIBLE);
                break;

            case "offline":

                MSG_FINDING_RIDES.setVisibility(View.VISIBLE);
                MSG_RIDER_ONLINE.setVisibility(View.INVISIBLE);
                MSG_CONNECTED_TIME.setVisibility(View.INVISIBLE);
                CONNECTED_TIME.setVisibility(View.INVISIBLE);
                //show go offline button
                goOffline.setVisibility(View.INVISIBLE);
                goOnline.setVisibility(View.VISIBLE);
                break;

            case "networkError":
                //hide buttons
                goOnline.setVisibility(View.INVISIBLE);
                goOffline.setVisibility(View.INVISIBLE);
                //hide textViews
                MSG_FINDING_RIDES.setVisibility(View.INVISIBLE);
                MSG_RIDER_ONLINE.setVisibility(View.INVISIBLE);
                MSG_CONNECTED_TIME.setVisibility(View.INVISIBLE);
                CONNECTED_TIME.setVisibility(View.INVISIBLE);
                //show textViews of support
                MSG_OFFLINE.setVisibility(View.VISIBLE);
                MSG_CONTACT_SUPPORT.setVisibility(View.VISIBLE);
                relativeLayout.setBackgroundColor(Color.rgb(220, 53, 69));
                //show alert dialog
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.show(getSupportFragmentManager(), "NetworkErrorDialog");
        }//end switch
    }//end method

    //public methods________________________________________________________________________________

    //LifeCycle methods________________________________________________________________________________

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 2- app is resumed from foreground");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: 1- App is in paused state..");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: 3- activity restarted..");
    }
}//endClass