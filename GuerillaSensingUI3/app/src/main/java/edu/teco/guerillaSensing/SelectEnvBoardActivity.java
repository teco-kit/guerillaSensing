package edu.teco.guerillaSensing;

import android.app.NotificationManager;
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
import android.support.v4.app.NotificationCompat;
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

    // The main view of this activity.
    private View mMainLayout;

    // The EnvBoard service.
    private IEnvBoardService service;

    // Recycler view for menu list.
    private RecyclerView mMenuEntriesRecycler;

    // Adapter for the menu list recycler.
    private MenuCardAdapter mMenuEntriesAdapter;

    // List of menu entries in the main menu.
    private List<CardMenuEntry> mMenuEntries;

    // The header entry in the menu, used to show status info.
    private CardMenuEntry header;

    // Boolean indicating if the bluetooth scan has already been started.
    private boolean mIsScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get main layout and initialize nav drawer on it.
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_start_menu, null);
        initNavigationDrawer(mMainLayout, false);
        setTitle("Select EnvBoard");
        setContentView(mMainLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NotificationCompat.Builder mBuilder =     new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_bluetooth)
                .setContentTitle("My notification")
                .setContentText("Hello World!");

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        // When activity is resumed, no scan is in progress.
        mIsScanning = false;

        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        // Start service
        Intent startServiceIntent = new Intent(this, EnvBoardService.class);
        startService(startServiceIntent);
        bindService(startServiceIntent, this, BIND_AUTO_CREATE);

        // Header indicates that Bluetooth scan has been started.
        header = new CardMenuEntry(R.drawable.ic_add, "Scanning...","Please wait while we scan for EnvBoards");

        // First menu entry: Stop measurements.
        CardMenuEntry menuEntryStopMeasurements = new CardMenuEntry("Stop data collection", "Stops the background data collection", "", R.drawable.main_menu_no_bt);

        // Add entries to menu.
        mMenuEntries = new ArrayList<>();
        mMenuEntries.add(header);
        mMenuEntries.add(menuEntryStopMeasurements);

        // Create adapter.
        mMenuEntriesAdapter = new MenuCardAdapter(mMenuEntries);

        // Set recycler view to vertical.
        mMenuEntriesRecycler = (RecyclerView) mMainLayout.findViewById(R.id.main_menu_card_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mMenuEntriesRecycler.setLayoutManager(llm);

        // Set recycler view adapter.
        mMenuEntriesRecycler.setAdapter(mMenuEntriesAdapter);


        // Gesture detector to detect single tap movements on the menu items.
        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        // Menu item touch listener.
        mMenuEntriesRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                // Get clicked view. If none was clicked, this might be null.
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    // A view in the menu was clicked. get the ID.
                    int childID = recyclerView.getChildPosition(child);

                    // Check if the clicked view was a menu item (might have been header or footer).
                    if (mMenuEntries.get(childID).getType() == MenuRecyclerTypes.TYPE_ITEM) {

                        // Check ID and handle accordingly.
                        if (childID == 1) {
                            // ID 1 stops measurements
                            try {
                                service.stopMeasurement();
                                Toast.makeText(SelectEnvBoardActivity.this, "Measurements stopped.", Toast.LENGTH_SHORT).show();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        } else {
                            // All other IDs are EnvBoards. Clicking them will start measurements with the
                            // upload mode and interval set in the preferences.
                            try {
                                // Start measurements. Interval is given in minutes, so we multiply by 60.
                                service.startMeasurement(mMenuEntries.get(childID).getSecondLine());
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SelectEnvBoardActivity.this);
                                service.setMeasurementInterval(60 * preferences.getInt("pref_measurement_interval", 10));
                                Toast.makeText(SelectEnvBoardActivity.this, "Device \"" + mMenuEntries.get(childID).getSecondLine()
                                        + "\" is now active.", Toast.LENGTH_SHORT).show();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    // Menu item was clicked.
                    return true;
                }
                // No menu item was clicked.
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) { }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cancel Bluetooth device discovery if activity is paused.
        if (this.service != null) {
            try {
                this.service.cancelDiscovery();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        // Unregister Bluetooth broadcast receiver.
        unregisterReceiver(mReceiver);
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
        // Get service.
        this.service = IEnvBoardService.Stub.asInterface((IBinder) service);

        // Register callbacks and set upload mode.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            this.service.registerServiceCallback(this);
            this.service.setUploadMode(preferences.getBoolean("pref_upload_mode_wifi_only", true));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Start scan for Bluetooth devices.
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
            // No EnvBoards found.
            header.setHeadLine("Scan done");
            header.setSubLine("No EnvBoards could be found. Please check if your EnvBoard is turned on and near enough to the smartphone.");
            mMenuEntriesAdapter.hasChanged(0);
        } else {
            // EnvBoards found. Show in list and make sure the adapter is updated.
            for (String device : list) {
                header.setHeadLine("Scan done");
                header.setSubLine("Please select one of the following EnvBoards. By doing so, this app " +
                        "will periodically read out the sensor values and pass them to the server once your smartphone has WiFi.");
                mMenuEntriesAdapter.hasChanged(0);

                CardMenuEntry newItem = new CardMenuEntry("EnvBoard", device, "Currently not connected", R.drawable.main_menu_service);
                mMenuEntriesAdapter.addItem(newItem);
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

    /**
     * Broadcasts are received when the Bluetooth status changes.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Bluetooth event occurred. Check type and handle accordingly.
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

    @Override
    public void drawerItemClicked(int item) { }

}
