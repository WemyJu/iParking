package com.bubble_gray.iparkingapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class GoogleMap extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    private String name,gps,describe;
    com.google.android.gms.maps.GoogleMap mMap;
    LatLng location;
    GoogleApiClient mGoogleApiClient;

    void BuildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        Intent it = getIntent();
        name = "車位";
        gps = it.getStringExtra("GPS");
        describe = it.getStringExtra("describe");
        Log.e("gps:", gps);
        int pos = gps.indexOf(",");
        Log.e("pos:", pos + "");
        String latitude = gps.substring(0, pos);
        String longtitude = gps.substring(pos + 1);
        Log.e("gps:", gps);

        Log.e(latitude, longtitude);
        location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude));


        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);     //better appearence of map
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);       //set compass
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                MoveToLocation(location);
                DrawMarker();
            }
        });
        BuildGoogleApiClient();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    private void DrawMarker() {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(name)
                .snippet(describe)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking)));
    }

    private void MoveToLocation(LatLng location) {
        if (mMap != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location, 17);
            mMap.moveCamera(update);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {

    }
}
