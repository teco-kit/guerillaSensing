package edu.teco.guerillaSensing;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.teco.guerillaSensing.google.HeatmapTileProvider;
import edu.teco.guerillaSensing.google.WeightedLatLng;

import edu.teco.envboardservicelib.EnvBoardSensorDate;

public class SelectPositionActivity extends NavigationDrawerActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener {

    private View mMainLayout;
    private static String QUERY_URL = "http://portal.teco.edu/guerilla/guerillaSensingServer/index.php/read_data/";

    private Location mLocation;
    private GoogleMap mMap;

    private float mCurrentZoom;

    private List<EnvBoardSensorDate> mCurrentlyDisplayedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainLayout = getLayoutInflater().inflate(R.layout.activity_select_position, null);
        initNavigationDrawer(mMainLayout);
        setTitle("EnvBoard");
        setContentView(mMainLayout);


        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_select_pos)).getMap();
        mMap.setOnCameraChangeListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (mMap!=null){

            mMap.setOnMapClickListener(this);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    mLocation = LocationHelper.getInstance().getLocation(getApplicationContext());
                    LatLng pos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
                }
            });

            t.run();
        }

        String query = "select * from data;";

        QueryTSDBTask dataDownloadTask = new QueryTSDBTask();
        dataDownloadTask.execute(QUERY_URL, query);

        mCurrentZoom = 14.0f;

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
        if (id == R.id.action_metric) {
            Intent startAc = new Intent(SelectPositionActivity.this, SelectMetricActivity.class);

            startActivity(startAc);
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
    private TileOverlay mOverlay;
    private double intensity;

    private void addToHeatMap(LatLng ll) {



        if  (lll == null) {
            lll = new ArrayList<>();
        }

        Random r = new Random();
        double xA, yA;

        for (int i = 0; i < 5000; i++) {
            xA = r.nextDouble() - 0.5;
            yA = r.nextDouble() - 0.5;
            intensity = r.nextDouble();

            WeightedLatLng wll = new WeightedLatLng(new LatLng(ll.latitude + xA / 10, ll.longitude + yA / 10), intensity);
            lll.add(wll);
        }


        if (mOverlay == null) {
            mProvider = new HeatmapTileProvider.Builder().weightedData(lll).build();
            mProvider.setRadius(27);
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
            mProvider.setWeightedData(lll);
            mOverlay.clearTileCache();
        }

    }

    private void showAsHeatMap(String metric) {


        List<WeightedLatLng> dataWithPosition = new ArrayList<>();

        for (EnvBoardSensorDate date : mCurrentlyDisplayedData) {
            WeightedLatLng wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getTemperature() / 48.0);
            dataWithPosition.add(wll);
        }


        if (mOverlay == null) {
            mProvider = new HeatmapTileProvider.Builder().weightedData(dataWithPosition).build();
            mProvider.setRadius(27);
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        } else {
            mProvider.setWeightedData(dataWithPosition);
            mOverlay.clearTileCache();
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom != mCurrentZoom) {
            mCurrentZoom = cameraPosition.zoom;

            float blurFactor = mCurrentZoom / 21;
            float maxBlur = 100;
            // Zoom has changed. Adjust heat map blur. TODO: Find a good way to to this.
            // mProvider.setRadius((int) (blurFactor * blurFactor * blurFactor * maxBlur));
        }
    }



    private class QueryTSDBTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... query) {
            // Start query and return the result, a JSON string.
            String result = queryStringOverHttpGet(query[0], query[1]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("ServerConnection", "Query result: " + result);

            Gson gson = new Gson();
            SensorDataPoint[] data = gson.fromJson(result, SensorDataPoint[].class);

            Log.d("ServerConnection", "GSON result: " + data[0].toString());

            // Got all points as Object. Iterate and fill map.
            int columnIndex = 0;

            Map<String,Integer> columnIndices = new HashMap<>();

            String[] metrics = new String[]{"time","lat","lon","co2","mac","height",
                                            "co","no2","o3","dust","uv","temp","hum"};

            for(String col : data[0].columns) {
                for (String metric : metrics) {
                    if (metric.equals(col)) {
                        columnIndices.put(metric, columnIndex);
                    }
                }
                columnIndex ++;
            }

            mCurrentlyDisplayedData = new ArrayList<>();

            for (List<String> sensorValues : data[0].points) {
                EnvBoardSensorDate date = new EnvBoardSensorDate();
                for (String metric : columnIndices.keySet()) {
                    String value = sensorValues.get(columnIndices.get(metric));

                    if (metric.equals("time")) {
                        date.setTimestamp(Long.parseLong(value));
                    } else if (metric.equals("lat")) {
                        date.setLatitude(Double.parseDouble(value));
                    } else if (metric.equals("lon")) {
                        date.setLongitude(Double.parseDouble(value));
                    } else if (metric.equals("height")) {
                        date.setHeight(Double.parseDouble(value));
                    } else if (metric.equals("mac")) {
                        date.setMac(value);
                    } else if (metric.equals("co")) {
                        date.setCo(Float.parseFloat(value));
                    } else if (metric.equals("co2")) {
                        date.setCo2(Float.parseFloat(value));
                    } else if (metric.equals("no2")) {
                        date.setNo2(Float.parseFloat(value));
                    } else if (metric.equals("o3")) {
                        date.setO3(Float.parseFloat(value));
                    } else if (metric.equals("dust")) {
                        date.setDust(Float.parseFloat(value));
                    } else if (metric.equals("uv")) {
                        date.setUV(Float.parseFloat(value));
                    } else if (metric.equals("temp")) {
                        date.setTemperature(Float.parseFloat(value));
                    } else if (metric.equals("hum")) {
                        date.setHumidity(Float.parseFloat(value));
                    }
                }

                mCurrentlyDisplayedData.add(date);
            }

            showAsHeatMap("temp");
            // Reading data done. Display on map.
        }

        private String queryStringOverHttpGet(String URL, String query) {

            HttpClient httpclient = new DefaultHttpClient();
            InputStream inputStream = null;
            String result = "";


            // Add query to URL.
            try {
                URL += URLEncoder.encode(query, "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                // Start HTTP GET request and return data.
                HttpGet httpGET = new HttpGet(URL);
                HttpResponse httpResponse = httpclient.execute(httpGET);
                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null) {
                    result = convertStreamToString(inputStream);
                } else {
                    result = "Exception while writing.";
                }

            } catch (IOException e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            // Return the data the query has returned as JSON string.
            return result;
        }

}

    // Needed for logging.
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
