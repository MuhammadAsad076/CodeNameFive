package com.itridtechnologies.codenamefive.UIViews;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.itridtechnologies.codenamefive.Models.PolyLinesData;
import com.itridtechnologies.codenamefive.R;
import com.itridtechnologies.codenamefive.utils.MapMarkerGenerator;

import java.util.ArrayList;
import java.util.List;

public class GoToPickupLocation extends AppCompatActivity implements
        OnMapReadyCallback, View.OnClickListener, GoogleMap.OnPolylineClickListener {

    //constants
    private static final String TAG = "GoToPickupLocation";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //vars
    private com.google.maps.model.LatLng riderCurrentLocation;
    private com.google.maps.model.LatLng restaurantCoordinates;
    private LatLngBounds mMapFocusArea;
    private boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolyLinesData> mPolyLineDataList = new ArrayList<>();

    //ui views
    private ImageButton recenterPickupLocation;
    private RelativeLayout layout;
    private TextView tripDuration;
    private TextView tripDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_pickup_location);

        //find views
        recenterPickupLocation = findViewById(R.id.imgBtn_recenter_pickup);
        layout = findViewById(R.id.rel_lay_recenter);

        //set listener
        recenterPickupLocation.setOnClickListener(this);

        if (isGpsOk()) {
            getLocationPermissions();
        } else {
            Log.d(TAG, "onCreate: Location is disabled !!");
        }

    }//onCreate

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.imgBtn_recenter_pickup:
                layout.setVisibility(View.INVISIBLE);
                recenterPickupLocation.setVisibility(View.INVISIBLE);
                recenterPickupLocation();
                break;

            default:
                Log.d(TAG, "onClick: invalid view..");

        }//switch
    }// listener

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

        if (mGeoApiContext == null) {

            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.GOOGLE_MAP_API_KEY))
                    .build();
            Log.d(TAG, "initMap: GeoApiContext is ready...");
        }

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

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //get driver location
            getDeviceLocation();
            //enable blue location dot
            mMap.setMyLocationEnabled(true);
            //mao ui components settings
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //disable compass
            mMap.getUiSettings().setCompassEnabled(false);
            //add polyline

            //map camera listener
            mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {
                    Log.d(TAG, "onCameraMoveStarted: camera moved due to reason: " + i);

                    //camera moved due to developer animation code: 3
                    if (i == 3) {
                        recenterPickupLocation.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.INVISIBLE);
                    }
                    //camera moved due to user gestures code: 1
                    if (i == 1) {
                        //show the recenter button
                        recenterPickupLocation.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.VISIBLE);
                    }
                }
            });
            //lines click listener
            mMap.setOnPolylineClickListener(this);
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

                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                            @Override
                            public void onSuccess(Location location) {

                                Log.d(TAG, "onComplete: location found!");

                                //after getting current location , move camera to current device location
                                if (location != null) {
                                    //get lat lng
                                    riderCurrentLocation = new com.google.maps.model.LatLng(location.getLatitude(), location.getLongitude());
                                    restaurantCoordinates = new com.google.maps.model.LatLng(31.573183, 74.381215);// doce

                                    if (isGpsOk()) {
                                        setCameraViewWindow(riderCurrentLocation, restaurantCoordinates);
                                        //cal d
                                        calculateDirections();

                                    } else {
                                        Toast.makeText(GoToPickupLocation.this, "Location error, enable location services", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Log.d(TAG, "onComplete: current location is null !!");
                                }

                            }//on Location Success
                        });
            }
        } catch (SecurityException ex) {
            Log.d(TAG, "getDeviceLocation: Security Exception: " + ex.getMessage());
        }

    }//end getLocation

    private void setCameraViewWindow(com.google.maps.model.LatLng rider, com.google.maps.model.LatLng restaurant) {
        Log.d(TAG, "setCameraViewWindow: setting camera view...");

        double bottomBounds;
        double bottomLeftBounds;

        double topBounds;
        double topRightBounds;

        bottomBounds = restaurant.lat;
        bottomLeftBounds = restaurant.lng;

        topBounds = rider.lat;
        topRightBounds = rider.lng;

        if (restaurant.lat > rider.lat) {
            Log.d(TAG, "setCameraViewWindow: southern latitude exceeds northern latitude: "+restaurant.lat+" <><><>"+rider.lat);

            //set the boundary
            mMapFocusArea = new LatLngBounds(
                    new LatLng(topBounds, topRightBounds),
                    new LatLng(bottomBounds, bottomLeftBounds)
            );
        } else {
            //set the boundary
            mMapFocusArea = new LatLngBounds(
                    new LatLng(bottomBounds, bottomLeftBounds),
                    new LatLng(topBounds, topRightBounds)
            );
        }

        recenterPickupLocation();

        //add marker to map
        addMapMarker();

    }// end SetCamera

    private void recenterPickupLocation() {

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea, 0));
        mMap.setLatLngBoundsForCameraTarget(mMapFocusArea);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMapFocusArea.getCenter() , 13.2f));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mMapFocusArea.getCenter(), 13.2f,0,0)));

    }// end recenter

    public void addMapMarker() {
        Log.d(TAG, "addMapMarker: trying to add marker" + restaurantCoordinates.toString());

        Bitmap pickupMarker = MapMarkerGenerator.getMarkerIcon(this , R.drawable.map_pin_restaurant);

        LatLng pickup = new LatLng(restaurantCoordinates.lat, restaurantCoordinates.lng);

        MarkerOptions options = new MarkerOptions()
                .title("Pickup")
                .icon(BitmapDescriptorFactory.fromBitmap(pickupMarker))
                .position(pickup);

        if (restaurantCoordinates != null) {
            mMap.addMarker(options);
        } else {
            Toast.makeText(this, "Restaurant not found !", Toast.LENGTH_SHORT).show();
        }

    }//end addMarker

    private void calculateDirections() {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = restaurantCoordinates;// end point

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        //enable/disable options
        directions.alternatives(true);
        directions.avoid(DirectionsApi.RouteRestriction.TOLLS);
        directions.origin(riderCurrentLocation.toString()); //starting point

        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination.toString()).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                //draw lines on map
                addPolylineToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }// end calculate

    private void updateUI(String minutes, String distance) {
        Log.d(TAG, "updateUI: updating distance and time of trip...");
        //find views
        tripDuration = findViewById(R.id.tv_est_minutes);
        tripDistance = findViewById(R.id.tv_est_distance);
        //set updated values
        if (minutes != null) {
            tripDuration.setText(minutes);
            tripDistance.setText(distance);
        } else {
            tripDuration.setText(R.string.def_min);
            tripDistance.setText(R.string.def_dist);
        }
    }//end updateUI

    private void addPolylineToMap(final DirectionsResult result) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                //delete the previously calculated routes
                if (mPolyLineDataList.size() > 0) {

                    for (PolyLinesData polyLinesData : mPolyLineDataList) {
                        polyLinesData.getPolyline().remove();
                    }
                    //clear list
                    mPolyLineDataList.clear();
                    mPolyLineDataList = new ArrayList<>();

                }//end if

                double duration = 999999999;
                double distance = 999999999;

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.LTGRAY);
                    polyline.setClickable(true);

                    //save a reference to poly lines data
                    mPolyLineDataList.add(new PolyLinesData(polyline, route.legs[0]));

                    //determine shortest route
                    double tempDuration = route.legs[0].duration.inSeconds;
                    double tempDistance = route.legs[0].distance.inMeters;

                    if (tempDuration < duration && tempDistance < distance) {

                        duration = tempDuration;
                        distance = tempDistance;

                        updateUI(route.legs[0].duration.humanReadable, route.legs[0].distance.humanReadable);

                        onPolylineClick(polyline);
                    }

                }//end main for loop
            }//end run
        });
    }// end addPolyLines

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

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: activity restarted!");
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        for (PolyLinesData polylineData : mPolyLineDataList) {
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());

            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(getResources().getColor(R.color.appThemeColor));
                polylineData.getPolyline().setJointType(JointType.ROUND);
                polylineData.getPolyline().setZIndex(1);
                updateUI(polylineData.getLeg().duration.humanReadable,
                        polylineData.getLeg().distance.humanReadable);

            } else {
                polylineData.getPolyline().setColor(Color.LTGRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }
}//end class