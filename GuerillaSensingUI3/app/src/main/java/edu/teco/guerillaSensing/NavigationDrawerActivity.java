package edu.teco.guerillaSensing;

import android.content.res.TypedArray;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import edu.teco.guerillaSensing.adapters.DrawerAdapter;


public abstract class NavigationDrawerActivity extends ActionBarActivity {

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

    // If this is false, the drawer and the toggle icon are not shown.
    private boolean mShow;

    // Title
    private CharSequence mTitle;

    // Data
    private String TITLES[] = {"Map", "Login", "Add Device", "Edit Device", "Search Device", "BLE Service", "Config", "Contact", "Help"};
    private int ICONS[] = {R.drawable.ic_map, R.drawable.ic_login, R.drawable.ic_add, R.drawable.ic_edit, R.drawable.ic_search,
            R.drawable.ic_bluetooth, R.drawable.ic_config, R.drawable.ic_contact, R.drawable.ic_help};
    private String NAME = "Main menu headline";
    private String EMAIL = "Main menu subline";

    // Gesture detector. Only detects single tap.
    private GestureDetector mGestureDetector;

    protected void setHeadline (String headline) {
        this.NAME = headline;
    }

    protected void setSubline (String subline) {
        this.EMAIL = subline;
    }

    protected void setEntries (String[] entries, TypedArray icons) {
        this.TITLES = entries;

        int size = icons.length();

        int[] iconsArray = new int[size];
        for(int i = 0; i < size; i++) {
            iconsArray[i] = icons.getResourceId(i, 0);
        }

        this.ICONS = iconsArray;
    }

    protected void initNavigationDrawer(View mainView, boolean show){

        this.mShow = show;

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
                    drawerItemClicked(childID);

                    mDrawerLayout.closeDrawers();

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
        if (mShow) {
            mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
            mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

            // Allow swipes to open the drawer.
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


    }

    public abstract void drawerItemClicked(int item);

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


}
