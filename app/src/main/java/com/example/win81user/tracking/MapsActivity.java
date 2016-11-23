package com.example.win81user.tracking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    Location mLocation;
    GoogleApiClient mGoogleApiClient;
    LatLng latlng;
    private LocationRequest mLocationRequest;
    LatLng from;
    Double meters = 0.00;
    LatLng[] arrLatLng = new LatLng[2];
    Boolean checkStart = false;
    Double second = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.d("Oncreate", "Oncreate");
        findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStart = true;
            }
        });
        findViewById(R.id.bt_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStart = false;
//                เดิน
//                4.5 - 5 กม./ชม. หรือ 1.25 - 1.38 เมตรต่อวินาที
//
//                 วิ่ง
//                27 - 32 กม./ชม. หรือ 7.5 - 8.8 เมตรต่อวินาที
                if(meters/1000/(3600/second) == 0){
                    Toast.makeText(MapsActivity.this, "สถานะ : นอน\nระยะทาง : "+meters+" เมตร\nเวลาที่ใช้ : "+second+" วินาที\nคำนวนเป็น : "+meters/1000/(3600/second)+"กิโลเมตร/ชั่วโมง", Toast.LENGTH_SHORT).show();
                }else if(meters/1000/(3600/second)<27){
                    Toast.makeText(MapsActivity.this, "สถานะ : เดิน\nระยะทาง : "+meters+" เมตร\nเวลาที่ใช้ : "+second+" วินาที\nคำนวนเป็น : "+meters/1000/(3600/second)+"กิโลเมตร/ชั่วโมง", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MapsActivity.this, "สถานะ : วิ่ง\nระยะทาง : "+meters+" เมตร\nเวลาที่ใช้ : "+second+" วินาที\nคำนวนเป็น : "+meters/1000/(3600/second)+"กิโลเมตร/ชั่วโมง", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        Log.d("OnResume", "OnResume");
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if (mMap != null) {
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
                mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if(mLocation != null){
                    latlng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
                    setUpMap();
                }
            }
        }
    }

    private void setUpMap() {
        Log.d("setUpMap", "setUpMap");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13.0f));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng thailand = new LatLng(13.774642,100.581704);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(thailand));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(thailand,19));
        mGoogleApiClient.connect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MapsActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation != null){
            latlng = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
            from = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
            arrLatLng[0] = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
            setUpMap();
        }
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
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        } else {
            Log.e("ggg","ssss");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Getting both the coordinates
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 13.0f));
        if(checkStart){
            second += 5.00;
            LatLng to = new LatLng(location.getLatitude(),location.getLongitude());
            arrLatLng[1] = to;
            //Calculating the distance in meters
            Double distance = SphericalUtil.computeDistanceBetween(arrLatLng[0], arrLatLng[1]);
            //Displaying the distance
            meters += distance;
            Toast.makeText(this,String.valueOf(meters+" sum "+arrLatLng[0]+" : "+distance+" Meters"+arrLatLng[1]),Toast.LENGTH_SHORT).show();
            arrLatLng[0] = arrLatLng[1];
            Log.e(mLocation.getLatitude()+" location", meters+" sum "+arrLatLng[0]+" : "+distance+" Meters"+arrLatLng[1]);
        }else{
            arrLatLng[0] = new LatLng(location.getLatitude(),location.getLongitude());
        }
    }
}