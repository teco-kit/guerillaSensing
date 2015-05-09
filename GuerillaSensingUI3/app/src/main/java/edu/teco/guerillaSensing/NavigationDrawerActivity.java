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

/**
 * Base Activity for all Activities that want to use an navigation drawer.
 * Extending Activities have to implement {@code drawerItemClicked(int item)}, which is called when
 * an item in the drawer menu has been clicked by the user.
 */
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

    // Data. This is just example data and should be overwritten by the Activities extending this class.
    private String ITEMS[] = {"Item1", "Item2", "Item3"};
    private int ICONS[] = {R.drawable.ic_map, R.drawable.ic_login, R.drawable.ic_add};
    private String HEAD = "Main menu headline";
    private String SUB = "Main menu subline";

    // Gesture detector. Only detects single tap.
    private GestureDetector mGestureDetector;

    /**
     * Setter for headline, shown in header.
     * @param headline The headline.
     */
    protected void setHeadline (String headline) {
        this.HEAD = headline;
    }

    /**
     * Setter for subline, shown in header.
     * @param subline The subline.
     */
    protected void setSubline (String subline) {
        this.SUB = subline;
    }

    /**
     * Setter for navigation drawer menu entries. Icons are supplied ad TypedArray because that's how
     * they are returned when stored in an XML array.
     * @param entries The menu items.
     * @param icons The icons for the menu items.
     */
    protected void setEntries (String[] entries, TypedArray icons) {
        // Convert TypedArray to regular integer array holding resource IDs.
        int size = icons.length();
        int[] iconsArray = new int[size];
        for(int i = 0; i < size; i++) {
            iconsArray[i] = icons.getResourceId(i, 0);
        }

        // Set new menu entries and icons.
        this.ITEMS = entries;
        this.ICONS = iconsArray;
    }

    /**
     * Initiate the navigation drawer on the given view.
     * @param mainView This view must contain an {@link android.support.v4.widget.DrawerLayout} called {@code drawer_layout},
     *                 an {@link android.support.v7.widget.Toolbar} called {@code tool_bar} and an
     *                 {@link android.support.v7.widget.RecyclerView} called {@code left_drawer}.
     *                 If they all exist, the navigation drawer will be initiated with an hamburger menu and the
     *                 previously defined menu items.
     * @param show Navigation drawer will not be shown if this is false.
     */
    protected void initNavigationDrawer(View mainView, boolean show){
        this.mShow = show;
        mTitle = getTitle();

        // Get drawer layout located in the mainView and set its shadow.
        mDrawerLayout = (DrawerLayout) mainView.findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Replace action bar with material design app bar from support library.
        // Menu items will automatically get added to this bar.
        Toolbar mToolBar = (Toolbar) mainView.findViewById(R.id.tool_bar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        // Get the recycler view that sits inside the navigation drawer.
        mSideMenuRecycler = (RecyclerView)  mainView.findViewById(R.id.left_drawer);

        // Set recycler view to vertical.
        mSideMenuRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mSideMenuRecycler.setLayoutManager(llm);

        // Create adapter for the Recycler View in the navigation drawer.
        mSideMenuAdapter = new DrawerAdapter(ITEMS, ICONS, HEAD, SUB);
        mSideMenuRecycler.setAdapter(mSideMenuAdapter);

        // Gesture detector to detect single tap movements on the menu items.
        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        // Menu item touch listener.
        mSideMenuRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                // Get clicked view. If none was clicked, this might be null.
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                // If a child was clicked, call {@code drawerItemClicked(childID);}, which is implemented
                // by the extending classes and close the navigation drawer.
                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int childID = recyclerView.getChildPosition(child);
                    drawerItemClicked(childID);
                    mDrawerLayout.closeDrawers();

                    // Menu item was clicked.
                    return true;
                }
                // No menu item was clicked.
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) { }
        });

        // Add drawer toggle ("hamburger").
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

        // If the drawer is set to be visible, show it and enable open by swipe and burger click.
        if (mShow) {
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            // Allow swipes to open the drawer.
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


    }

    /**
     * Sets the title shown in the app bar of the activity.
     * @param title The new title.
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * Called when an item in the navigation drawer menu has been clicked.
     * This is implemented by the extending activities.
     * @param item The ID of the clicked item, with 0 being the first item in the menu.
     */
    public abstract void drawerItemClicked(int item);

}
