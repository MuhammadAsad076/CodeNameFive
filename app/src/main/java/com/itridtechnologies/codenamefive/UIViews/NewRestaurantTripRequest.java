package com.itridtechnologies.codenamefive.UIViews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itridtechnologies.codenamefive.R;

import static com.itridtechnologies.codenamefive.utils.MapMarkerGenerator.getMarkerIcon;

public class NewRestaurantTripRequest extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    //constants
    public static final String TAG = "RestaurantTripRequest";
    //vars
    private GoogleMap mMap;
    private LatLng mCustomerLocation;
    private LatLng mRestaurantLocation;
    private LatLngBounds mMapFocusArea;
    //ui views
    private Button gotoRestaurant;
    private ImageButton recenterMarker;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_resturant_trip_request);

        //find views
        gotoRestaurant = findViewById(R.id.btn_rider_accept_restaurant_order);
        recenterMarker = findViewById(R.id.imgBtn_recenter_markers);
        layout = findViewById(R.id.rel_lay_recenter_markers);

        //set listener
        gotoRestaurant.setOnClickListener(this);
        recenterMarker.setOnClickListener(this);

        mCustomerLocation = new LatLng(31.574232, 74.384835);//home
        mRestaurantLocation = new LatLng(31.583621, 74.381712);//western union

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

        //map camera listener
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.d(TAG, "onCameraMoveStarted: camera moved due to reason: " + i);

                //camera moved due to developer animation code: 3
                if (i == 3) {
                    recenterMarker.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                }
                //camera moved due to user gestures code: 1
                if (i == 1) {
                    //show the recenter button
                    recenterMarker.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });

        //mao ui components settings
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        //disable compass
        mMap.getUiSettings().setCompassEnabled(false);
        //add markers to map
        addMapMarkers();
        //set camera view
        setCameraViewWindow(mCustomerLocation, mRestaurantLocation);

    }// end OnMapReady

    public void addMapMarkers() {
        Log.d(TAG, "addMapMarker: trying to add marker");

        Bitmap iconCustomer = getMarkerIcon(this , R.drawable.map_pin_destination);
        Bitmap iconRestaurant = getMarkerIcon(this , R.drawable.map_pin_restaurant);

        MarkerOptions CMO = new MarkerOptions()
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromBitmap(iconCustomer))
                .position(mCustomerLocation);

        MarkerOptions RMO = new MarkerOptions()
                .title("Pickup")
                .icon(BitmapDescriptorFactory.fromBitmap(iconRestaurant))
                .position(mRestaurantLocation);

        mMap.addMarker(CMO);
        mMap.addMarker(RMO);
    }//end addMarker

    private void setCameraViewWindow(LatLng customer, LatLng restaurant) {
        Log.d(TAG, "setCameraViewWindow: setting camera view...");

        double bottomBounds = restaurant.latitude - .01;
        double bottomLeftBounds = restaurant.longitude - .01;

        double topBounds = customer.latitude + .01;
        double topRightBounds = customer.longitude + .01;

        //set the boundary
        mMapFocusArea = new LatLngBounds(
                new LatLng(bottomBounds, bottomLeftBounds),
                new LatLng(topBounds, topRightBounds)
        );

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea, 0));
        mMap.setLatLngBoundsForCameraTarget(mMapFocusArea);

    }// end SetCamera

    private void recenterMarkerLocations() {

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mMapFocusArea, 0));

    }// end recenter

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
                startActivity(new Intent(NewRestaurantTripRequest.this, GoToPickupLocation.class));
                //open anim
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.imgBtn_recenter_markers:
                layout.setVisibility(View.INVISIBLE);
                recenterMarker.setVisibility(View.INVISIBLE);
                recenterMarkerLocations();
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