package edu.teco.guerillaSensing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import edu.teco.envboardservicelib.IEnvBoardService;
import edu.teco.guerillaSensing.adapters.MenuCardAdapter;
import edu.teco.guerillaSensing.data.CardMenuEntry;
import edu.teco.guerillaSensing.data.MenuRecyclerTypes;
import edu.teco.guerillaSensing.helpers.OnlineStatusHelper;


public class StartMenuActivity extends NavigationDrawerActivity {

    // Recycler view for the device type list.
    private RecyclerView mDeviceTypeRecycler;

    // Adapter for the device type recycler.
    private MenuCardAdapter mDeviceTypeAdapter;

    private List<CardMenuEntry> mDeviceTypes;


    private View mMainLayout;
    private IEnvBoardService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_start_menu, null);
        initNavigationDrawer(mMainLayout, false);
        setTitle("GuerillaSensing");

        // Load device types from XML.
        // TODO: This has to be read out of XML files.
        // TODO: XML files should be downloaded from server.
        // TODO: Device images should also be downloaded, maybe from a public image hoster.

        CardMenuEntry header = new CardMenuEntry(R.drawable.ic_add, "Main Menu","Welcome to the TECO Guerilla Sensing App. You are currently NOT connected to an EnvBoard.");

        CardMenuEntry c1 = new CardMenuEntry("View Data", "View the collected data on a map", "Requires internet connection", R.drawable.main_menu_heatmap);
        CardMenuEntry c2 = new CardMenuEntry("Connect EnvBoard", "Connect to and configure an EnvBoard", "Currently selected: 9B:1D:35:7C", R.drawable.main_menu_service);
        CardMenuEntry c0 = new CardMenuEntry("For more options and help, please refer to the side menu.");
        mDeviceTypes = new ArrayList<>();


        mDeviceTypes.add(header);
        mDeviceTypes.add(c1);
        mDeviceTypes.add(c2);
        mDeviceTypes.add(c0);



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


                            if (OnlineStatusHelper.getInstance().isOnline(StartMenuActivity.this)) {
                                Intent startAc = new Intent(StartMenuActivity.this, MapViewActivity.class);
                                startActivity(startAc);
                            } else {
                                new AlertDialog.Builder(StartMenuActivity.this)
                                        .setTitle("You are offline")
                                        .setMessage("To view heatmap data, please connect this device to the internet.")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent startAc = new Intent(StartMenuActivity.this, MapViewActivity.class);
                                                startActivity(startAc);
                                            }
                                        })
                                        .setIcon(R.drawable.ic_map)
                                        .show();
                            }


                        } else if (childID == 2) {
                            if (OnlineStatusHelper.getInstance().isBluetoothAvailable()) {
                                Intent startAc = new Intent(StartMenuActivity.this, SelectEnvBoardActivity.class);
                                startActivity(startAc);
                            } else {
                                new AlertDialog.Builder(StartMenuActivity.this)
                                        .setTitle("No Bluetooth")
                                        .setMessage("This function requires Bluetooth. Do you want to turn it on?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Enable bluetooth and start activity.
                                                OnlineStatusHelper.getInstance().enableBluetooth();
                                                Intent startAc = new Intent(StartMenuActivity.this, SelectEnvBoardActivity.class);
                                                startActivity(startAc);
                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do nothing.
                                    }
                                })
                                        .setIcon(R.drawable.ic_map)
                                        .show();
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









        setContentView(mMainLayout);
    }

    @Override
    public void drawerItemClicked(int item) {
        Toast.makeText(this, "Opening " + item, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
