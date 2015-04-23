package com.teco.vindi.guerillasensingui3;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.teco.vindi.guerillasensingui3.google.HeatmapTileProvider;
import com.teco.vindi.guerillasensingui3.google.WeightedLatLng;


import java.util.ArrayList;
import java.util.List;


public class SelectPositionActivity extends ActionBarActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener {

    private LatLng HAMBURG = new LatLng(53.558, 9.927);
    private LatLng KIEL = new LatLng(53.551, 9.993);
    private Location mLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position);



        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_select_pos)).getMap();
        mMap.setOnCameraChangeListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mMap!=null){
            // Marker hamburg = mMap.addMarker(new MarkerOptions().position(HAMBURG).title("Hamburg"));
            // Marker kiel = mMap.addMarker(new MarkerOptions().position(KIEL).title("Kiel").snippet("Kiel is cool").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mypos)));

            mMap.setOnMapClickListener(this);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    mLocation = LocationHelper.getInstance().getLocation(getApplicationContext());
                    LatLng pos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));

                    mMap.addMarker(new MarkerOptions().position(pos).title("curPos").draggable(true));

                }
            });

            t.run();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationHelper.getInstance().stopLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_position, menu);
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
    public void onMapClick(LatLng latLng) {
        // mMap.addMarker(new MarkerOptions().position(latLng).title("Sensor Position").draggable(true).snippet("Press and drag to change location."));
        addToHeatMap(latLng);
    }


    private List<WeightedLatLng>lll;
    private HeatmapTileProvider mProvider;
    TileOverlay mOverlay;
    private float intensity;
    private float count = 0;

    private void addToHeatMap(LatLng ll) {



        if  (lll == null) {
            lll = new ArrayList<>();
        }



        if (count < 3) {
            intensity = 1.0f;
        } else {
            intensity = 0.5f;
        }
        count++;


        WeightedLatLng wll = new WeightedLatLng(ll, intensity);
        lll.add(wll);


        if (mOverlay == null) {
            mProvider = new HeatmapTileProvider.Builder().weightedData(lll).build();
            mProvider.setRadius(50);
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
            mProvider.setWeightedData(lll);
            mOverlay.clearTileCache();
        }

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.d("zoom", cameraPosition.zoom + "");

        //if (mProvider != null)
       //   mProvider.setRadius((int) (10 * cameraPosition.zoom));
    }
}
