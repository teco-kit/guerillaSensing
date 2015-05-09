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

/**
 * The startup activity, showing the start menu.
 */
public class StartMenuActivity extends NavigationDrawerActivity {

    // The main view of this activity.
    private View mMainLayout;

    // Recycler view for menu list.
    private RecyclerView mMenuEntriesRecycler;

    // Adapter for the menu list recycler.
    private MenuCardAdapter mMenuEntriesAdapter;

    // List of menu entries in the main menu.
    private List<CardMenuEntry> mMenuEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get main layout and initialize nav drawer on it.
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_start_menu, null);
        initNavigationDrawer(mMainLayout, false);

        setTitle("GuerillaSensing");

        // Add menu entries to the main menu.
        CardMenuEntry header = new CardMenuEntry(R.drawable.ic_add, "Main Menu","Welcome to the TECO Guerilla Sensing App. You are currently NOT connected to an EnvBoard.");
        CardMenuEntry c1 = new CardMenuEntry("View Data", "View the collected data on a map", "Requires internet connection", R.drawable.main_menu_heatmap);
        CardMenuEntry c2 = new CardMenuEntry("Connect EnvBoard", "Connect to and configure an EnvBoard", "Requires Bluetooth", R.drawable.main_menu_service);
        CardMenuEntry c0 = new CardMenuEntry("For more options and help, please refer to the side menu.");
        mMenuEntries = new ArrayList<>();
        mMenuEntries.add(header);
        mMenuEntries.add(c1);
        mMenuEntries.add(c2);
        mMenuEntries.add(c0);

        // Create adapter for the main menu.
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
                            // ID 1 starts the map view but shows the user an alert if the device is offline.
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
                            // ID 2 starts the EnvBoard scan activity, but prompts the user if Bluetooth is turned off.
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
                                        // If the user doesn't want to enable Bluetooth, do nothing.
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
    public void drawerItemClicked(int item) { }
}
