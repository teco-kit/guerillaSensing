package com.teco.vindi.guerillasensingui3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
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


public class StartMenuActivity extends NavigationDrawerActivity {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_SEPARATOR = 2;

    // Recycler view for the device type list.
    private RecyclerView mDeviceTypeRecycler;

    // Adapter for the device type recycler.
    private MenuCardAdapter mDeviceTypeAdapter;

    private List<DeviceType> mDeviceTypes;


    // TODO: Change this mess to subclasses!
    /**
     * Device types, including header and separators in the list.
     */
    public class DeviceType {
        // The data type (header, item or separator).
        private int TYPE;

        // For the "headers".
        protected int mHeaderImage;
        protected String mHeadLine;
        protected String mSubLine;

        // For the "items".
        protected int mPicture;
        protected String mName;
        protected String mVersion;
        protected String mCreationDate;

        // For the "separators".
        protected String mSeparator;

        /**
         * Constructor for the header.
         * @param headerImage The header image.
         * @param headLine The header head line.
         * @param subLine The header sub line.
         */
        public DeviceType(int headerImage, String headLine, String subLine) {
            this.TYPE = TYPE_HEADER;
            this.mHeaderImage = headerImage;
            this.mHeadLine = headLine;
            this.mSubLine = subLine;
        }

        /**
         * Constructor for device type.
         * @param name The device name.
         * @param version The device version.
         * @param creationDate The device creation date.
         * @param picture Reference to Drawable image of device.
         */
        public DeviceType(String name, String version, String creationDate, int picture) {
            this.TYPE = TYPE_ITEM;
            this.mPicture = picture;
            this.mName = name;
            this.mVersion = version;
            this.mCreationDate = creationDate;
        }

        /**
         * Constructor for the separator.
         * @param separator
         */
        public DeviceType(String separator) {
            this.TYPE = TYPE_SEPARATOR;
            this.mSeparator = separator;
        }

        public int getType() {
            return TYPE;
        }
    }


    private View mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_start_menu, null);
        initNavigationDrawer(mMainLayout);
        setTitle("GuerillaSensing");









        // Load device types from XML.
        // TODO: This has to be read out of XML files.
        // TODO: XML files should be downloaded from server.
        // TODO: Device images should also be downloaded, maybe from a public image hoster.

        DeviceType header = new DeviceType(R.drawable.ic_add, "Main Menu","Welcome to the TECO Guerilla Sensing App. You are currently NOT connected to an EnvBoard.");

        DeviceType c1 = new DeviceType("View Data", "View heatmap data", "Requires internet connection", R.drawable.bpart);
        DeviceType c2 = new DeviceType("Add Data", "Read data from EnvBoard and upload it to the database", "Currently selected: 9B:1D:35:7C", R.drawable.bpart);
        DeviceType c3 = new DeviceType("Configuration", "Change upload mode.", "", R.drawable.bpart);
        DeviceType c0 = new DeviceType("For more option and help, please refer to the side menu.");
        mDeviceTypes = new ArrayList<>();


        mDeviceTypes.add(header);
        mDeviceTypes.add(c1);
        mDeviceTypes.add(c2);
        mDeviceTypes.add(c3);
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

        //mDeviceTypeRecycler.addItemDecoration(new SimpleLineDecoration(getActivity()));

        mDeviceTypeRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int childID = recyclerView.getChildPosition(child);

                    if (mDeviceTypes.get(childID).getType() == TYPE_ITEM) {

                        Toast.makeText(StartMenuActivity.this, "Adding device of type \"" + mDeviceTypes.get(childID).mName + "\".", Toast.LENGTH_SHORT).show();
                        Intent startAc = new Intent(StartMenuActivity.this, SelectPositionActivity.class);

                        startActivity(startAc);
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
