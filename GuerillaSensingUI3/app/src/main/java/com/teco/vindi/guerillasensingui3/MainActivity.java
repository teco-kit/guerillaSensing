package com.teco.vindi.guerillasensingui3;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;


public class MainActivity extends ActionBarActivity{

    private static String TAG = "GuerillaSensing";

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private PagerAdapter mPagerAdapter;
    private DrawerAdapter mDrawerAdapter;
    private ViewPager mPager;
    private SlidingTabLayout mPagerTabs;
    private Toolbar mToolBar;
    private ActionBarDrawerToggle mDrawerToggle;


    String TITLES[] = {"Map", "Login", "Add Device", "Edit Device", "Search Device", "BLE Service", "Config", "Contact", "Help"};
    int ICONS[] = {R.drawable.ic_map, R.drawable.ic_login, R.drawable.ic_add, R.drawable.ic_edit, R.drawable.ic_search,
            R.drawable.ic_bluetooth, R.drawable.ic_config, R.drawable.ic_contact, R.drawable.ic_help};
    String NAME = "Vincent Diener";
    String EMAIL = "vincent.diener@gmail.com";
    //int PROFILE = R.drawable.aka;

    // Gesture detector. Only detects single tap.
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Replace action bar with material design app bar from support library.
        // Menu items will automatically get added to this bar.
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolBar);

        mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });



        mDrawerAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        mDrawerList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    int childID = recyclerView.getChildPosition(child);
                    if (childID == 0) {
                        // Header was clicked.
                        Toast.makeText(MainActivity.this, "Header clicked.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Item was clicked.
                        View v = mDrawerList.getChildAt(childID);
                        mDrawerAdapter.removeAt(childID);
                        //mDrawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this, "Opening \"" + TITLES[childID - 1] + "\"", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolBar,R.string.app_name,R.string.app_name) { // <---------- FIX THIS! TODO TODO TODO

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        }; // Drawer Toggle Object Made
        mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State



        mPager = (ViewPager) findViewById(R.id.content_pager);
        mPagerTabs = (SlidingTabLayout) findViewById(R.id.content_tabs);

        mPagerTabs.setCustomTabView(R.layout.custom_tab_layout, R.id.tab_text);

        // Set the adapter for the list view
        mDrawerList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(llm);

        mDrawerAdapter = new DrawerAdapter(TITLES,ICONS,NAME,EMAIL);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mDrawerList.setAdapter(mDrawerAdapter);
        // Set the list's click listener
        // mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mPagerTabs.setDistributeEvenly(true);

        mPagerTabs.setSelectedIndicatorColors(Color.WHITE);

        startPaging();
        mPagerTabs.setViewPager(mPager);



        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        



    }

    private void startPaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, MainContent1.class.getName()));
        fragments.add(Fragment.instantiate(this, MainContent2.class.getName()));

        mPagerAdapter = new PagerAdapter(this.getSupportFragmentManager(), fragments);

        mPager.setAdapter(mPagerAdapter);

    }



    // Menus
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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