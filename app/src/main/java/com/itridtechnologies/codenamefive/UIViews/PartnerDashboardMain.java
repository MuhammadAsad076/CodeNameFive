package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.utils.NetworkConnectionState;
import com.itridtechnologies.codenamefive.utils.NetworkErrorDialog;
import com.itridtechnologies.codenamefive.utils.RiderManager;
import com.kusu.loadingbutton.LoadingButton;

public class PartnerDashboardMain extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = "PartnerDashboardMain";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15.5f;
    private LatLng userCoordinates;
    private boolean mLocationPermissionGranted = false;
    MediaPlayer player;

    //UI Views
    private LoadingButton goOnline;
    private Button goOffline;
    private RelativeLayout relativeLayoutGps;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ImageButton partnerAppMenu;
    private ImageButton recenterGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_dashboard_main);

        //find views
        relativeLayoutGps = findViewById(R.id.rel_lay_gps);
        goOnline = findViewById(R.id.btn_go_online);
        goOffline = findViewById(R.id.btn_go_offline);
        partnerAppMenu = findViewById(R.id.imgBtn_app_menu);
        recenterGps = findViewById(R.id.imgBtn_recenter_gps);

        //set listener
        goOnline.setOnClickListener(this);
        goOffline.setOnClickListener(this);
        partnerAppMenu.setOnClickListener(this);
        recenterGps.setOnClickListener(this);

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
        //check if rider is already online
        if (RiderManager.getRiderConnected()) {
            Log.d(TAG, "onCreate: rider is connected..");
            goOnline.setVisibility(View.INVISIBLE);
            goOffline.setVisibility(View.VISIBLE);
            updateUI("online");
        } else {
            Log.d(TAG, "onCreate: rider not online yet...");
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
                            userCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                            if (isNetworkOk() && userCoordinates != null) {
                                moveCamera(userCoordinates);
                            } else {
                                Toast.makeText(PartnerDashboardMain.this, "Network error, try again", Toast.LENGTH_SHORT).show();
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

    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera: moving the camera to, lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        DEFAULT_ZOOM)
        );
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
        //add marker to map
        addMapMarker();
    }//end moveCamera

    private void animateCameraToCurrentLocation() {
        Log.d(TAG, "animateCameraToCurrentLocation: moving camera to new location...");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                userCoordinates = null;

                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: location found!");
                            Location currentLocation = (Location) task.getResult();

                            //after getting current location , move camera to current device location
                            userCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            mMap.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            userCoordinates,
                                            14.5f
                                    )
                            );
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
    }// end animateCamera


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

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
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
        } else {
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

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {
                    Log.d(TAG, "onCameraMoveStarted: camera moved due to reason: " + i);

                    //camera moved due to developer animation code: 3
                    if (i == 3) {
                        recenterGps.setVisibility(View.INVISIBLE);
                        relativeLayoutGps.setVisibility(View.INVISIBLE);
                    }
                    //camera moved due to user gestures code: 1
                    if (i == 1) {
                        //show the recenter button
                        recenterGps.setVisibility(View.VISIBLE);
                        relativeLayoutGps.setVisibility(View.VISIBLE);
                    }
                }//end camera moved
            });
            //mao ui components settings
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //disable compass
            mMap.getUiSettings().setCompassEnabled(false);
        }//end if
    }//end onMapReady

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_go_online:
                if (NetworkConnectionState.isNetworkConnected(this)) {
                    updateUI("online");
                } else {
                    updateUI("networkError");
                    Toast.makeText(this, "You are offline !", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_go_offline:
                updateUI("offline");
                break;

            case R.id.imgBtn_app_menu:
                startActivity(new Intent(PartnerDashboardMain.this, PartnerAppMenu.class));
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.imgBtn_recenter_gps:
                updateUI("recenter");

        }//end switch
    }

    private void updateUI(String status) {

        TextView MSG_RIDER_ONLINE = findViewById(R.id.msg_rider_online);
        TextView MSG_CONNECTED_TIME = findViewById(R.id.msg_time_connected);
        TextView CONNECTED_TIME = findViewById(R.id.tv_time_connected);
        TextView MSG_OFFLINE = findViewById(R.id.msg_you_are_offline);
        TextView MSG_CONTACT_SUPPORT = findViewById(R.id.msg_contact_support);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout_ps);

        switch (status) {

            case "online":

                //update buttons and textViews
                MSG_RIDER_ONLINE.setVisibility(View.VISIBLE);
                MSG_CONNECTED_TIME.setVisibility(View.VISIBLE);
                CONNECTED_TIME.setVisibility(View.VISIBLE);
                //goOnline.setVisibility(View.INVISIBLE);
                //find trips for rider
                findRiderTrips();
                break;

            case "offline":

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
                break;

            case "recenter":
                //recenter to device location
                animateCameraToCurrentLocation();
                break;

        }//end switch
    }//end method

    private void findRiderTrips() {
        Log.d(TAG, "findRiderTrips: finding trips..");
        //set button state to loading
        goOnline.showLoading();

        //trip lookup in separate thread
        Thread backgroundThread = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 3 seconds
                    sleep(3000);

                } catch (Exception e) {
                    Log.e(TAG, "run: " + e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        // After 3 seconds redirect to another intent
                        goOnline.hideLoading();
                        goOnline.setText(R.string.online);
                        startActivity(new Intent(PartnerDashboardMain.this, NewRestaurantTripRequest.class));
                        //open anim
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //sound and vibration feedback
                        createVibeAlertWithSound(500);
                        //set rider status
                        RiderManager.setRiderConnected(true);
                        Log.d(TAG, "run: trip found");
                    }
                });// UI Thread
            }//run
        };

        // start thread
        backgroundThread.start();

    }//end findTrips

    private void createVibeAlertWithSound(int millis) {
        Log.d(TAG, "createVibeAlertWithSound: trying to give haptic feedback...");
        //init vibe
        Vibrator vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //check for hardware
        if (vibe.hasVibrator() && player == null) {

            if (Build.VERSION.SDK_INT >= 26) {
                //vibrate
                vibe.vibrate(VibrationEffect.createOneShot(millis , VibrationEffect.DEFAULT_AMPLITUDE));
                player = MediaPlayer.create(this , R.raw.swiftly);
                //sound
                player.start();
                //release player
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        player = null;
                    }
                });
            } else {
                player = MediaPlayer.create(this , R.raw.swiftly);
                //sound
                player.start();
                //release player
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        player.release();
                        player = null;
                    }
                });
                vibe.vibrate(millis);
            }
        }//end if

    }//end vibration

    public void addMapMarker() {
        Log.d(TAG, "addMapMarker: trying to add marker");

        ImageView imageView;
        IconGenerator iconGenerator;

        imageView = new ImageView(this);
        iconGenerator = new IconGenerator(this);

        imageView.setImageResource(R.drawable.map_pin_customer);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(128, 128));
        //imageView.setPadding(4, 4, 4, 4);
        iconGenerator.setContentView(imageView);
        iconGenerator.setBackground(null);

        Bitmap icon = iconGenerator.makeIcon();

        MarkerOptions options = new MarkerOptions()
                .title("Me")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(new LatLng(31.519123, 74.344939));


        mMap.addMarker(options);
    }//end addMarker

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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}//endClass