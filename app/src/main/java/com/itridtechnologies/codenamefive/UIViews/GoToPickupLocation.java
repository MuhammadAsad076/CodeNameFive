package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.ui.IconGenerator;
import com.itridtechnologies.codenamefive.R;

public class GoToPickupLocation extends AppCompatActivity implements OnMapReadyCallback {

    //constants
    private static final String TAG = "GoToPickupLocation";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15.5f;

    //vars
    private LatLng riderCurrentLocation;
    private LatLng restaurantCoordinates;
    private LatLngBounds mMapFocusArea;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //ui views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_pickup_location);

        if (isGpsOk()) {
            getLocationPermissions();
        }

    }//onCreate

    //Methods_______________________________________________________________________________________

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");

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
    }//end RequestPermission

    public void initMap() {
        Log.d(TAG, "initMap: initializing the map..");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_pickup_location);

        mapFragment.getMapAsync(GoToPickupLocation.this);
    }//end method initMap

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready (Pickup)..");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style_silver));
                Log.d(TAG, "onMapReady: map style silver added...");

                if (!success) {
                    Log.d(TAG, "onMapReady: styling failed..");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }// end try/catch

            //get driver location
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //enable blue location dot
            mMap.setMyLocationEnabled(true);
            //mao ui components settings
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //disable compass
            mMap.getUiSettings().setCompassEnabled(false);
            //add marker to map
            addMapMarker();
            //add polyline
            drawPolyLines();
        }//end if

    }// end MapReady

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

    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device current location...");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted && isGpsOk()) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: location found!");
                            Location currentLocation = task.getResult();

                            //after getting current location , move camera to current device location
                            if (currentLocation != null) {
                                //get lat lng
                                currentLocation.setAccuracy(100);
                                currentLocation.setBearing(0);
                                riderCurrentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                restaurantCoordinates = new LatLng(31.515346, 74.343942);//ijaz center

                                if (isGpsOk()) {
                                    setCameraViewWindow(riderCurrentLocation, restaurantCoordinates);
                                } else {
                                    Toast.makeText(GoToPickupLocation.this, "Location error, enable location services", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "onComplete: current location is null !!");
                            }
                        } else {
                            Log.d(TAG, "onComplete: location not found!");
                            Toast.makeText(GoToPickupLocation.this, "Unable to find current location !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }//end if

        } catch (SecurityException ex) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + ex.getMessage());
        }

    }//end getLocation

    private void setCameraViewWindow(LatLng rider, LatLng restaurant) {
        Log.d(TAG, "setCameraViewWindow: setting camera view...");

        double bottomBounds = restaurant.latitude - .01;
        double bottomLeftBounds = restaurant.longitude - .01;

        double topBounds = rider.latitude + .01;
        double topRightBounds = rider.longitude + .01;

        //set the boundary
        mMapFocusArea = new LatLngBounds(
                new LatLng(bottomBounds, bottomLeftBounds),
                new LatLng(topBounds, topRightBounds)
        );

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea, 0));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea , 1));

    }// end SetCamera

    public void addMapMarker() {
        Log.d(TAG, "addMapMarker: trying to add marker");

        ImageView imageView;
        IconGenerator iconGenerator;

        imageView = new ImageView(this);
        iconGenerator = new IconGenerator(this);

        imageView.setImageResource(R.drawable.map_pin_pickup);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(128, 128));
        //imageView.setPadding(4, 4, 4, 4);
        iconGenerator.setContentView(imageView);
        iconGenerator.setBackground(null);

        Bitmap icon = iconGenerator.makeIcon();

        MarkerOptions options = new MarkerOptions()
                .title("Me")
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(restaurantCoordinates);


        mMap.addMarker(options);
    }//end addMarker

    private void drawPolyLines() {
        PolylineOptions options = new PolylineOptions();
        options.add(riderCurrentLocation , restaurantCoordinates);

        Polyline polyline = mMap.addPolyline(options);
        polyline.setColor(R.color.appThemeColor);
        polyline.setJointType(JointType.ROUND);
        polyline.setWidth(4f);
    }//end draw

    //LifeCycle events______________________________________________________________________________

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: activity paused...");
        //check for location permissions
        if (!isGpsOk()) {
            Log.d(TAG, "onPause: user has turned off GPS !!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: activity destroyed !!");
    }

}//end class