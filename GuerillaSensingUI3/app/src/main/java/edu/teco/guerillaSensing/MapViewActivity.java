package edu.teco.guerillaSensing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

import edu.teco.envboardservicelib.EnvBoardSensorDate;
import edu.teco.guerillaSensing.config.SelectMetricActivity;
import edu.teco.guerillaSensing.data.SensorDataPoint;
import edu.teco.guerillaSensing.google.HeatmapTileProvider;
import edu.teco.guerillaSensing.google.WeightedLatLng;
import edu.teco.guerillaSensing.helpers.OnlineStatusHelper;

public class MapViewActivity extends NavigationDrawerActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener {

    // The main view of this activity.
    private View mMainLayout;

    // The map.
    private GoogleMap mMap;

    // The current zoom level.
    private float mCurrentZoom;

    // The currently displayed data.
    private List<EnvBoardSensorDate> mCurrentlyDisplayedData = new ArrayList<>();

    // The "fake" data to visualize the heat map. This will be removed later.
    private List<WeightedLatLng> fakeDataList;

    // The tile provider for the heatmap.
    // TODO: Report bug where tiles suddenly turn white.
    private HeatmapTileProvider mProvider;

    // The map overlay.
    private TileOverlay mOverlay;

    // The REST API URL to send requests to.
    private static String QUERY_URL = "http://portal.teco.edu/guerilla/guerillaSensingServer/index.php/read_data/";

    // The query. For now, just query all data from the DB.
    private static String QUERY = "select * from data;";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get main layout and initialize nav drawer on it.
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_view_map, null);

        // Set the navigation drawer menu.
        this.setHeadline("Heatmap Data");
        this.setSubline("Please select a metric");
        String[] allMetrics = getResources().getStringArray(R.array.array_metrics_string);
        TypedArray allMetricIcons = getResources().obtainTypedArray(R.array.array_metrics_icon);
        this.setEntries(allMetrics, allMetricIcons);
        initNavigationDrawer(mMainLayout, true);

        setContentView(mMainLayout);

        // Get MapFragment from support library to stay compatible.
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_select_pos)).getMap();

        // Register for map movement events by the user.
        mMap.setOnCameraChangeListener(this);

        // Settings for the map: Terrain Mode with zoom controls and location button.
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Set zoom level to 14. At this level, you can see most of Karlsruhe.
        mCurrentZoom = 14.0f;

        // Start query in the background. Data is shown as soon as it becomes available.
        // Query is only started if device is online.
        if (OnlineStatusHelper.getInstance().isOnline(this)) {
            QueryTSDBTask dataDownloadTask = new QueryTSDBTask();
            dataDownloadTask.execute(QUERY_URL, QUERY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update the heat map with the data that is currently available.
        this.updateHeatMap();
    }

    /**
     * Update the heat map with the data that is currently available.
     */
    private void updateHeatMap() {
        if (OnlineStatusHelper.getInstance().isOnline(this)) {
            // Get active metric and show it if the heatmap is enabled. Also set the activity title accodringly.
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean("pref_show_heatmap", false)) {
                String metric = preferences.getString("pref_select_metric", "air");
                setTitle("Showing \"" + metric + "\"");
                showAsHeatMap(metric);
            } else {
                setTitle("Showing map");
            }
        } else {
            setTitle("Offline");
        }

        // Draw scale if it is enabled.
        this.drawScaleBitmap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Start settings activity if the menu entry was selected.
        if (id == R.id.action_metric) {
            Intent startSettingsIntent = new Intent(MapViewActivity.this, SelectMetricActivity.class);
            startActivity(startSettingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show the given metric on the heat map after some arbitrary conversions.
     * TODO: Find out what values are considered high and low and convert accordingly.
     * @param metric The metric as string.
     */
    private void showAsHeatMap(String metric) {

        List<WeightedLatLng> dataWithPosition = new ArrayList<>();

        for (EnvBoardSensorDate date : mCurrentlyDisplayedData) {
            WeightedLatLng wll = null;

            switch (metric) {
                case "air": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()),
                        (date.getCo() + date.getCo2() + date.getO3()) / 48.0);
                    break;
                case "temp": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getTemperature() / 48.0);
                    break;
                case "co": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getCo() / 48.0);
                    break;
                case "co2": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getCo2() / 48.0);
                    break;
                case "o3": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getO3() / 48.0);
                    break;
                case "no2": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getNo2() / 48.0);
                    break;
                case "dust": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getDust() / 48.0);
                    break;
                case "uv": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getUV() / 48.0);
                    break;
                case "hum": wll = new WeightedLatLng(new LatLng(date.getLatitude(), date.getLongitude()), date.getHumidity() / 48.0);
                    break;

            }

            dataWithPosition.add(wll);
        }


        // After first start, no data is present.
        if (dataWithPosition.isEmpty())
            return;

        // If data is present, show it on the heatmap.
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
        // If the camera zoom has changed, it might be a good idea to adjust the blur.
        // TODO: Find a good way to do this.
        //if (cameraPosition.zoom != mCurrentZoom) {
        //    mCurrentZoom = cameraPosition.zoom;
        //
        //    float blurFactor = mCurrentZoom / 21;
        //    float maxBlur = 100;
        //    mProvider.setRadius((int) (blurFactor * blurFactor * blurFactor * maxBlur));
        //}
    }

    @Override
    public void onMapClick(LatLng latLng) { }


    /**
     * Async task that queries the TSDB.
     */
    private class QueryTSDBTask extends AsyncTask<String, String, String> {

        // The first string is the URL, the second one the query.
        @Override
        protected String doInBackground(String... query) {
            // Start query and return the result, a JSON string.
            String result = queryStringOverHttpGet(query[0], query[1]);
            return result;
        }

        /**
         *  Take query results and create a GSON object from it.
         *  Then parse the GSON object into something a little easier to use: {@link EnvBoardSensorDate}.
         */
        @Override
        protected void onPostExecute(String result) {

            Gson gson = new Gson();
            SensorDataPoint[] data = gson.fromJson(result, SensorDataPoint[].class);

            // Got all points as Object. Iterate and fill map.
            int columnIndex = 0;
            Map<String,Integer> columnIndices = new HashMap<>();
            String[] metrics = new String[]{"time","lat","lon","co2","mac","height",
                                            "co","no2","o3","dust","uv","temp","hum"};

            // This happens when the connection drops mid-download.
            if (data == null)
                return;

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

            updateHeatMap();
            // Reading data done. Display on map.
        }

        /**
         * Send the given query to the given URL and return the results as string.
         * @param URL The URL of the REST API.
         * @param query The query to send.
         * @return The returned string.
         */
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

    /**
     * Converts an InputStream to a string.
     * @param is The InputStream
     * @return The given InputStream, converted to a string.
     */
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

    @Override
    public void drawerItemClicked(int item) {
        // Set metric according to clicked item. Subtract 1 from item ID because we do not count the header.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String clickedMetric = getResources().getStringArray(R.array.array_metrics_value)[item - 1];
        preferences.edit().putString("pref_select_metric", clickedMetric).commit();
        Toast.makeText(this, "Showing metric \"" + clickedMetric + "\".", Toast.LENGTH_SHORT).show();

        // Update the heatmap with the new data.
        updateHeatMap();
    }


    //-----------------------------------
    // SCALE OVERLAY CODE
    //-----------------------------------

    private void drawScaleBitmap() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String metric = preferences.getString("pref_select_metric", "air");

        ImageView view = (ImageView)findViewById(R.id.scale_image);

        if (preferences.getBoolean("pref_show_scale", true))
            view.setVisibility(View.VISIBLE);
        else {
            view.setVisibility(View.INVISIBLE);
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Bitmap bitmap = Bitmap.createBitmap(displayMetrics.widthPixels - 100,
                (int)(displayMetrics.density * 50), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();

        float pos_x = displayMetrics.density;
        float width = (displayMetrics.widthPixels - 100) / 100F;


        canvas.drawRect(0, 0, width * 100F, 25 * displayMetrics.density, paint);

        for (int i = 0; i < 100; i++) {

            paint.setColor(getScaleColor(2, 98, i));
            canvas.drawRect(pos_x, displayMetrics.density, pos_x+width, 24 * displayMetrics.density, paint);
            pos_x += width * 0.9935F;

        }

        paint.setColor(Color.BLACK);
        paint.setTextSize(15 * displayMetrics.density);

        String[] labelStrings = getResources().getStringArray(R.array.scale_labels);

        int ordinal = ordinalFromMetric(metric);

        canvas.drawText(labelStrings[ordinal*2], 0, 40 * displayMetrics.density, paint);
        canvas.drawText(labelStrings[ordinal*2+1],
                pos_x- paint.measureText(labelStrings[ordinal*2+1]),
                40 * displayMetrics.density, paint);

        String[] metricStrings  = getResources().getStringArray(R.array.array_metrics_string);

        canvas.drawText(metricStrings[ordinal],
                width*50- paint.measureText(metricStrings[ordinal]) / 2F,
                40 * displayMetrics.density, paint);

        canvas.save();
        view.setImageBitmap(bitmap);
    }

    private int getScaleColor(float min, float max, float val) {
        if (val <= min)
            return Color.GREEN;
        else if (val >= max)
            return Color.RED;
        else {
            // TODO: Proper interpolation.
            float p = 0.27f + (val - min)/(max - min) / 2.4f;

            if (p < 1F / 6)
                return Color.argb(255, 0, (int)((p*6)*255F) , 255);
            else if (p < 2F / 6)
                return Color.argb(255, 0, 255, 255 - (int)((p * 6F - 1F) * 255F));
            else if (p < 3F / 6)
                return Color.argb(255, (int)((p * 6F - 2F) * 255F), 255 , 0);
            else if (p < 4F / 6)
                return Color.argb(255, 255, 255 - (int)((p * 6F - 3F) * 255F), 0);
            else if (p < 5F / 6)
                return Color.argb(255, 255, 0, (int)((p * 6F - 4F) * 255F));
            else
                return Color.argb(255, 255,(int)((p * 6F - 5F) * 255F), 255);
        }
    }

    private int ordinalFromMetric(String metric) {
        switch (metric) {
            case "air":
                return 0;
            case "temp":
                return 1;
            case "co":
                return 2;
            case "co2":
                return 3;
            case "o3":
                return 4;
            case "no2":
                return 5;
            case "dust":
                return 6;
            case "uv":
                return 7;
            case "hum":
                return 8;
            default:
                return 0;
        }
    }

}
