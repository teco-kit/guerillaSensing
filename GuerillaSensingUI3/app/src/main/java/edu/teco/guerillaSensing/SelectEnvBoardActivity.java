package edu.teco.guerillaSensing;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.teco.envboardservicelib.EnvBoardService;
import edu.teco.envboardservicelib.IEnvBoardService;
import edu.teco.envboardservicelib.IServiceCallback;
import edu.teco.guerillaSensing.adapters.MenuCardAdapter;
import edu.teco.guerillaSensing.config.ServiceConfigActivity;
import edu.teco.guerillaSensing.data.CardMenuEntry;
import edu.teco.guerillaSensing.data.MenuRecyclerTypes;
import edu.teco.guerillaSensing.helpers.OnlineStatusHelper;

public class SelectEnvBoardActivity extends NavigationDrawerActivity implements ServiceConnection, IServiceCallback {


    // Recycler view for the device type list.
    private RecyclerView mDeviceTypeRecycler;

    // Adapter for the device type recycler.
    private MenuCardAdapter mDeviceTypeAdapter;

    private List<CardMenuEntry> mDeviceTypes;


    private View mMainLayout;
    private IEnvBoardService service;
    private CardMenuEntry header;
    private boolean mIsScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_start_menu, null);
        initNavigationDrawer(mMainLayout, false);
        setTitle("Select EnvBoard");
        setContentView(mMainLayout);



    }

    @Override
    protected void onResume() {
        super.onResume();

        // When activity is resumed, no scan is in progress.
        mIsScanning = false;

        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        // Start service
        Intent startServiceIntent = new Intent(this, EnvBoardService.class);
        startService(startServiceIntent);
        bindService(startServiceIntent, this, BIND_AUTO_CREATE);

        header = new CardMenuEntry(R.drawable.ic_add, "Scanning...","Please wait while we scan for EnvBoards");

        CardMenuEntry c1 = new CardMenuEntry("Stop data collection", "Stops the background data collection", "", R.drawable.main_menu_no_bt);
        mDeviceTypes = new ArrayList<>();
        mDeviceTypes.add(header);
        mDeviceTypes.add(c1);

        // Create adapter.
        mDeviceTypeAdapter = new MenuCardAdapter(mDeviceTypes);

        // Set recycler view to vertical.
        mDeviceTypeRecycler = (RecyclerView) mMainLayout.findViewById(R.id.main_menu_card_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mDeviceTypeRecycler.setLayoutManager(llm);

        // Set recycler view adapter.
        mDeviceTypeRecycler.setAdapter(mDeviceTypeAdapter);


        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        mDeviceTypeRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int childID = recyclerView.getChildPosition(child);

                    if (mDeviceTypes.get(childID).getType() == MenuRecyclerTypes.TYPE_ITEM) {

                        if (childID == 1) {
                            try {
                                service.stopMeasurement();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {

                            try {
                                service.startMeasurement(mDeviceTypes.get(childID).getSecondLine());

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SelectEnvBoardActivity.this);
                                service.setMeasurementInterval(60 * preferences.getInt("pref_measurement_interval", 10));

                                Toast.makeText(SelectEnvBoardActivity.this, "Device \"" + mDeviceTypes.get(childID).getSecondLine()
                                        + "\" is now active.", Toast.LENGTH_SHORT).show();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
    }

    @Override
    public void drawerItemClicked(int item) {
        Toast.makeText(this, "Opening " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_env_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Start settings activity if the menu entry was selected.
        if (id == R.id.action_service_config) {
            Intent startSettingsIntent = new Intent(SelectEnvBoardActivity.this, ServiceConfigActivity.class);
            startActivity(startSettingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();

        //Intent stopServiceIntent = new Intent(this, EnvBoardService.class);
        //stopService(stopServiceIntent);
        if (this.service != null) {
            try {
                this.service.cancelDiscovery();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(this);
        unregisterReceiver(mReceiver);
    }

    /**
     * Called when a connection to the Service has been established, with
     * the {@link IBinder} of the communication channel to the
     * Service.
     *
     * @param name    The concrete component name of the service that has
     *                been connected.
     * @param service The IBinder of the Service's communication channel,
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = IEnvBoardService.Stub.asInterface((IBinder) service);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            this.service.registerServiceCallback(this);
            this.service.setUploadMode(preferences.getBoolean("pref_upload_mode_wifi_only", true));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        startBluetoothScanIfReady();
    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {

        this.service = null;
    }

    /**
     * Called, when discovery has finished
     */
    @Override
    public void discoveryFinished(String[] list) throws RemoteException {
        if (list == null || list.length == 0) {
            header.setHeadLine("Scan done");
            header.setSubLine("No EnvBoards could be found. Please check if your EnvBoard is turned on and near enough to the smartphone.");
            mDeviceTypeAdapter.hasChanged(0);
        } else {
            for (String device : list) {
                header.setHeadLine("Scan done");
                header.setSubLine("Please select one of the following EnvBoards. By doing so, this app " +
                        "will periodically read out the sensor values and pass them to the server once your smartphone has WiFi.");
                mDeviceTypeAdapter.hasChanged(0);

                CardMenuEntry newItem = new CardMenuEntry("EnvBoard", device, "Currently not connected", R.drawable.main_menu_service);
                mDeviceTypeAdapter.addItem(newItem);
            }
        }
    }

    /**
     * Called, when the number of sensor values stored has changed
     *
     * @param count
     */
    @Override
    public void sensorDataCountChanged(final int count) throws RemoteException {

    }

    /**
     * Called, when some sensor values have been successfully uploaded to the server
     *
     * @param count
     */
    @Override
    public void sensorDatesUploaded(int count) throws RemoteException {

    }

    /**
     * Retrieve the Binder object associated with this interface.
     * You must use this instead of a plain cast, so that proxy objects
     * can return the correct result.
     */
    @Override
    public IBinder asBinder() {
        return null;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        // TODO
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // TODO
                        break;
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth is now enabled. Scan may be started.
                        startBluetoothScanIfReady();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // TODO
                        break;
                }
            }
        }
    };

    /**
     * Starts bluetooth scan for EnvBoards if the service is connected and bluetooth is ready
     * and not already scanning for EnvBoards.
     */
    private void startBluetoothScanIfReady() {
        if (!mIsScanning && this.service != null && OnlineStatusHelper.getInstance().isBluetoothAvailable()) {
            mIsScanning = true;
            try {
                this.service.startDiscovery();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
