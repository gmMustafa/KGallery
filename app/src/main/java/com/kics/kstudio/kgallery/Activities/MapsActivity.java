package com.kics.kstudio.kgallery.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kics.kstudio.kgallery.DataBase.LocationSqlite;
import com.kics.kstudio.kgallery.DataModels.MapMarkers;
import com.kics.kstudio.kgallery.Misc.CustomDialogClass;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/*Google Mobile Services (GMS) is a collection of Google applications and APIs that help
support functionality across devices. These apps work together seamlessly to ensure your
device provides a great user experience right out of the box. */
//Contains all possible error codes for when a client fails to connect to Google Play services.
/*The main entry point for Google Play services integration.*/
//The main entry point for location services integration.
/*Used for receiving notifications from the FusedLocationProviderApi when
 the location has changed. The methods are called if the LocationListener
 has been registered with the location client using the request*/
//A data object that contains quality of service parameters for requests to the FusedLocationProviderApi. //PRIORITY_HIGH_ACCURACY
//Defines a camera move. An object of this type can be used to modify a map's camera by calling
// animateCamera(CameraUpdate)
//A class containing methods for creating CameraUpdate objects that change a map's camera.
// To modify the map's camera, call animateCamera(CameraUpdate)
/*
    This is the main class of the Google Maps Android API and is the entry point for
    all methods related to the map. You cannot instantiate a GoogleMap object directly,
     rather, you must obtain one from the getMapAsync() method on a MapFragment or MapView
    that you have added to your application.*/
/*A Map component in an app. This fragment is the simplest way to place a map in an application.
    It's a wrapper around a view of a map to automatically handle the necessary life cycle needs.*/
/*A View which displays a map (with data obtained from the Google Maps service).
When focused, it will capture keypresses and touch gestures to move the map.*/
/*
    onCreate(Bundle)
    onStart()
    onResume()
    onPause()
    onStop()
    onDestroy()
    onSaveInstanceState()
    onLowMemory()

A GoogleMap must be acquired using getMapAsync(OnMapReadyCallback). The MapView automatically initializes the maps system and the view.
*/
//Callback interface for when the map is ready to be used.
//Used to create a definition of a Bitmap image, used for marker icons and ground overlays.
//latitude longitude
///marker

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GpsStatus.Listener {

    CameraUpdate camera;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    LocationSqlite locationSqlite;
    ArrayList<MapMarkers> mapList;
    ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    Double lat, lng;    static final int REQUEST_PERMISSION_KEY = 1;

    void getPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.CHANGE_COMPONENT_ENABLED_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE};

        if (!Utils.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    PERMISSIONS, REQUEST_PERMISSION_KEY);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gal_activity_maps);

        getPermissions();

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);

        statusCheck();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mWifi.isConnected()) {
            Toast.makeText(this, "WIFI NOT CONNECTED", Toast.LENGTH_SHORT).show();
            finish();
        }

        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.addGpsStatusListener(this);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "LOCATION (GPS) NOT CONNECTED", Toast.LENGTH_SHORT).show();
            finish();
        }


        locationSqlite = new LocationSqlite(getApplicationContext());
        getMarkers();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

    }

    private void getMarkers() {
        mapList = (ArrayList<MapMarkers>) locationSqlite.getAllRecord();
    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void setMarks() {
        if (mapList != null) {
            Log.e("Check", String.valueOf(mapList.size()));
            for (int i = 0; i < mapList.size(); i++) {
                LatLng llt = new LatLng(mapList.get(i).getFIELD_LAT(), mapList.get(i).getFIELD_LNG());
                markerOptions.add(new MarkerOptions());
                markerOptions.get(i).position(llt);
                markerOptions.get(i).title(mapList.get(i).getFIELD_TITLE());
            }

            for (int i = 0; i < mapList.size(); i++) {
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(markerOptions.get(i).getPosition());
                circleOptions.strokeColor(R.color.cardview_dark_background);
                circleOptions.fillColor(R.color.place);
                circleOptions.radius(100);
                mGoogleMap.addCircle(circleOptions);
                //googleMap.clear();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(markerOptions.get(i).getPosition()));
                mGoogleMap.addMarker(markerOptions.get(i));
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); // two minute interval
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else
        */
        {


            setMarks();
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(final LatLng latLng) {

                    final MarkerOptions markerOptionsobj = new MarkerOptions();
                    mGoogleMap.clear();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                    final CustomDialogClass cdd = new CustomDialogClass(MapsActivity.this);
                    cdd.show();

                    cdd.setDialogListener(new CustomDialogClass.DialogListener() {
                        @Override
                        public void onCompleted() {
                            locationSqlite.addRecord(latLng.latitude, latLng.longitude, cdd.place);

                            CircleOptions circleOptions = new CircleOptions();
                            circleOptions.center(latLng);
                            circleOptions.radius(100);
                            mGoogleMap.addCircle(circleOptions);
                            markerOptionsobj.position(latLng);
                            markerOptionsobj.title(cdd.place);
                            markerOptions.add(markerOptionsobj);
                            mGoogleMap.addMarker(markerOptions.get(markerOptions.size() - 1));
                            getMarkers();
                            /*adapter = new MarkerAdapter(mapList, getApplicationContext());
                            ListMarkers.recyclerView.setAdapter(adapter);
                            */
                            setMarks();
                        }

                        @Override
                        public void onCanceled() {
                            setMarks();
                            cdd.dismiss();
                        }
                    });
                }
            });
            LatLng coordinate;
            if (mapList.size() > 0) {
                coordinate = new LatLng(mapList.get(0).getFIELD_LAT(), mapList.get(0).getFIELD_LNG());
            } else {
                coordinate = new LatLng(31.5204, 74.3587);
            }
            camera = CameraUpdateFactory.newLatLngZoom(coordinate, 100);
            googleMap.moveCamera(camera);
            googleMap.animateCamera(camera);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                lat = location.getLatitude();
                lng = location.getLongitude();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                setMarks();
                //move map camera
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.btn_back:
                if (checked)
                    finish();
                break;
        }
    }


    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                //Toast.makeText(getBaseContext()," GPS CONNECTIVITY FAILURE",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {

        if (Build.VERSION.SDK_INT >= 23) {
            checkDrawOverlayPermission();
        } else {
            // do something for phones running an SDK before lollipop
            AlertMessageNoGpsforles23();
        }
    }


    private void AlertMessageNoGpsforles23() {
        // do something for phones running an SDK before lollipop
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        // LayoutParams.TYPE_TOAST or
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    private void AlertMessageNoGps() {
        // do something for phones running an SDK before lollipop
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        // LayoutParams.TYPE_TOAST or
//        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

//        int LAYOUT_FLAG;
//        LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        WindowManager.LayoutParams params =  new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                LAYOUT_FLAG,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
//                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
//        alert.getWindow().setType(params);
        alert.show();
    }

    public final static int REQUEST_CODE = 214;


    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MapsActivity.this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                AlertMessageNoGpsforles23();
//                AlertMessageNoGps();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                    AlertMessageNoGps();
                }
            }
        }
    }
}

