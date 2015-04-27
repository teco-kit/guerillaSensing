package edu.teco.guerillaSensing;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class NavigationDrawerActivity extends ActionBarActivity {

    // The support app bar.
    private Toolbar mToolBar;

    // Top layout. Used to add the navigation drawer.
    private DrawerLayout mDrawerLayout;

    // Recycler view for the navigation drawer list.
    private RecyclerView mSideMenuRecycler;

    // Adapter for the navigation drawer recycler view.
    private DrawerAdapter mSideMenuAdapter;

    // App bar toggle icon ("hamburger").
    private ActionBarDrawerToggle mDrawerToggle;

    // Title
    private CharSequence mTitle;

    // Data
    private String TITLES[] = {"Map", "Login", "Add Device", "Edit Device", "Search Device", "BLE Service", "Config", "Contact", "Help"};
    private int ICONS[] = {R.drawable.ic_map, R.drawable.ic_login, R.drawable.ic_add, R.drawable.ic_edit, R.drawable.ic_search,
            R.drawable.ic_bluetooth, R.drawable.ic_config, R.drawable.ic_contact, R.drawable.ic_help};
    private String NAME = "Vincent Diener";
    private String EMAIL = "vincent.diener@gmail.com";

    // Gesture detector. Only detects single tap.
    private GestureDetector mGestureDetector;

    protected void initNavigationDrawer(View mainView){

        mDrawerLayout = (DrawerLayout) mainView.findViewById(R.id.drawer_layout);

        mTitle = getTitle();

        // Replace action bar with material design app bar from support library.
        // Menu items will automatically get added to this bar.
        Toolbar mToolBar = (Toolbar) mainView.findViewById(R.id.tool_bar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        // Create
        mSideMenuAdapter = new DrawerAdapter(TITLES, ICONS, NAME, EMAIL);

        mDrawerLayout = (DrawerLayout)  mainView.findViewById(R.id.drawer_layout);
        mSideMenuRecycler = (RecyclerView)  mainView.findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mSideMenuRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mSideMenuRecycler.setLayoutManager(llm);

        mSideMenuAdapter = new DrawerAdapter(TITLES, ICONS, NAME, EMAIL);
        mSideMenuRecycler.setAdapter(mSideMenuAdapter);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);


        mGestureDetector = new GestureDetector(NavigationDrawerActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });




        mSideMenuRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int childID = recyclerView.getChildPosition(child);
                    if (childID == 0) {
                        // Header was clicked.
                        Toast.makeText(NavigationDrawerActivity.this, "Header clicked.", Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();

                        Intent startAc = new Intent(NavigationDrawerActivity.this, SelectEnvBoardActivity.class);
                        NavigationDrawerActivity.this.startActivity(startAc);


                    } else {
                        // Item was clicked.
                        View v = mSideMenuRecycler.getChildAt(childID);
                        //mSideMenuAdapter.removeAt(childID);
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(NavigationDrawerActivity.this, "Opening \"" + TITLES[childID - 1] + "\"", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });







        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Drawer was opened.
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Drawer was closed.
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        // Allow swipes to open the drawer.
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_drawer, menu);
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
