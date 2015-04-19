package com.schematical.adam;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.schematical.adam.comm.socket.AdamSocketClient;
import com.schematical.adam.location.AdamLocation;
import com.schematical.adam.signal.AdamSignalDriver;
import com.schematical.adam.signal.gps.AdamGPSDriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class AdamWorldActivity extends FragmentActivity {
    private static AdamWorldActivity instance;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected AdamSocketClient client;
    private AdamLocation lastLocation;
    private AdamSignalDriver signalDriver;


    public static AdamWorldActivity getInstance(){
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_adam_maps);
        setUpMapIfNeeded();

        AdamGPSDriver gpsDriver = new AdamGPSDriver();
        signalDriver = new AdamSignalDriver();
        AdamSignalDriver.Connect();
        client = new AdamSocketClient();
        try {
            client.Connect();
        } catch (URISyntaxException e) {
            Log.d("Adam","FAILED:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
    public void addPin(LatLng latLng){

    }

    public void onLocationChanged(AdamLocation aLocation) {

        lastLocation = aLocation;

        JSONObject jObj = new JSONObject();
        try {
            jObj.put("listener", lastLocation.toJSON());
            jObj.put("pings", new JSONArray(AdamSignalDriver.GetResultsArray()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        client.Send("ping", jObj);
        Log.d("Adam", "Sending Data");
    }
}
