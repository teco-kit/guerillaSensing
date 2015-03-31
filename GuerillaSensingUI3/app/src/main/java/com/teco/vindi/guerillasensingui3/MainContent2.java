package com.teco.vindi.guerillasensingui3;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MainContent2 extends Fragment {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_SEPARATOR = 2;

    // Fragment name, needed to display view pager tab.
    private static String FRAGMENT_NAME = "Graph";

    // Recycler view for the device type list.
    private RecyclerView mDeviceTypeRecycler;

    // Adapter for the device type recycler.
    private MenuCardAdapter mDeviceTypeAdapter;

    // List of available device types.
    // TODO: Fetch from server by XML.
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


    /**
     * Required empty public constructor for the fragment.
     */
    public MainContent2() {
        //Nothing.
    }

    // Called when fragment is instantiated. Inflate interface.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate fragment.
        View rootView = inflater.inflate(R.layout.fragment_main_content2, container, false);

        // Load device types from XML.
        // TODO: This has to be read out of XML files.
        // TODO: XML files should be downloaded from server.
        // TODO: Device images should also be downloaded, maybe from a public image hoster.

        DeviceType header = new DeviceType(R.drawable.ic_add, "Select Device Type","What kind of device do you want to add?");

        DeviceType c1 = new DeviceType("bPart", "1.3", "19.07.2014", R.drawable.bpart);
        DeviceType c2 = new DeviceType("bPart", "2.0", "26.03.2015", R.drawable.bpart);
        DeviceType c3 = new DeviceType("TI SensorTag", "1.0", "12.12.2014", R.drawable.sensor_tag);
        DeviceType c0 = new DeviceType("Your device type is not in the list? Please contact example@teco.kit.edu to have it added.");
        mDeviceTypes = new ArrayList<>();


        mDeviceTypes.add(header);
        mDeviceTypes.add(c1);
        mDeviceTypes.add(c2);
        mDeviceTypes.add(c3);
        mDeviceTypes.add(c0);



        // Create adapter.
        mDeviceTypeAdapter = new MenuCardAdapter(mDeviceTypes);

        // Set recycler view to vertical.
        mDeviceTypeRecycler = (RecyclerView) rootView.findViewById(R.id.main_card_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mDeviceTypeRecycler.setLayoutManager(llm);

        // Set recycler view adapter.
        mDeviceTypeRecycler.setAdapter(mDeviceTypeAdapter);


        final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
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

                    if (mDeviceTypes.get(childID).getType() == TYPE_ITEM) {

                        Toast.makeText(getActivity(), "Adding device of type \"" + mDeviceTypes.get(childID).mName + "\".", Toast.LENGTH_SHORT).show();
                    }

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });




        // Return inflated layout for this fragment.
        return rootView;
    }

    /**
     * Returns the name of this fragment so it can be displayed as view pager tab.
     *
     * @return The name of this fragment.
     */
    @Override
    public String toString() {
        return FRAGMENT_NAME;
    }


}