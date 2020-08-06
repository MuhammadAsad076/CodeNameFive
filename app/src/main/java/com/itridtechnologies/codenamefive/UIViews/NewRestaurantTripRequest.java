package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itridtechnologies.codenamefive.R;

public class NewRestaurantTripRequest extends AppCompatActivity implements OnMapReadyCallback , View.OnClickListener{

    //constants
    public static final String TAG = "RestaurantTripRequest";
    //vars
    private GoogleMap mMap;
    private LatLng mCustomerLocation;
    private LatLng mRestaurantLocation;
    private LatLngBounds mMapFocusArea;
    //ui views
    private Button gotoRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_resturant_trip_request);

        //find views
        gotoRestaurant = findViewById(R.id.btn_rider_accept_restaurant_order);
        gotoRestaurant.setOnClickListener(this);

        mCustomerLocation = new LatLng(31.574232, 74.384835);//home
        mRestaurantLocation = new LatLng(31.515346, 74.343942);//ijaz center

        //initialize google map
        if (isGpsOk() && isNetworkOk()) {
            initMap();
        } else {
            Log.d(TAG, "onCreate: missing internet or Gps..");
        }

    }//onCreate

    //Methods_______________________________________________________________________________________

    public void initMap() {
        Log.d(TAG, "initMap: initializing the map..");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_trip_request);

        mapFragment.getMapAsync(NewRestaurantTripRequest.this);
    }//end method initMap

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: google maps is ready !!");
        mMap = googleMap;

        if (isGpsOk()) {
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
        }//end if

        //mao ui components settings
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //disable compass
        mMap.getUiSettings().setCompassEnabled(false);

        MarkerOptions options1 = new MarkerOptions()
                .title("Destination")
                .position(mCustomerLocation);

        MarkerOptions options2 = new MarkerOptions()
                .title("Pickup Food")
                .position(mRestaurantLocation);

        mMap.addMarker(options1);
        mMap.addMarker(options2);

        setCameraViewWindow(mCustomerLocation , mRestaurantLocation);


    }// end OnMapReady

    private void setCameraViewWindow(LatLng customer , LatLng restaurant) {
        Log.d(TAG, "setCameraViewWindow: setting camera view...");

        double bottomBounds = restaurant.latitude - .01;
        double bottomLeftBounds = restaurant.longitude - .01;

        double topBounds = customer.latitude + .01;
        double topRightBounds = customer.longitude + .01;

        //set the boundary
        mMapFocusArea = new LatLngBounds(
                new LatLng(bottomBounds , bottomLeftBounds),
                new LatLng(topBounds , topRightBounds)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea , 0));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea , 1));

    }// end SetCamera

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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_rider_accept_restaurant_order:
                startActivity(new Intent(NewRestaurantTripRequest.this , GoToPickupLocation.class));
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            default:
                Log.d(TAG, "onClick: unknown view id");

        }//switch

    }// listener

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

}//endClass