package com.teco.vindi.guerillasensingui3;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MainContent2 extends Fragment {

    // Fragment name, needed to display view pager tab.
    private static String FRAGMENT_NAME = "Graph";

    // Recycler view for the device type list.
    private RecyclerView mDeviceTypeRecycler;

    // Adapter for the device type recycler.
    private MenuCardAdapter mDeviceTypeAdapter;

    // List of available device types.
    // TODO: Fetch from server by XML.
    private List mDeviceTypes;


    /**
     * Device types.
     * TODO: Put in its own class with full info (sensors).
     */
    public class DeviceType {
        protected String mName;
        protected String mVersion;
        protected String mCreationDate;
        protected int mPicture;

        public DeviceType(String name, String version, String creationDate, int picture) {
            this.mName = name;
            this.mVersion = version;
            this.mCreationDate = creationDate;
            this.mPicture = picture;

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
        DeviceType c1 = new DeviceType("bPart", "1.3", "19.07.2014", R.drawable.ic_bluetooth);
        DeviceType c2 = new DeviceType("bPart", "2.0", "26.03.2015", R.drawable.ic_login);
        DeviceType c3 = new DeviceType("TI SensorTag", "1.0", "12.12.2014", R.drawable.ic_config);
        mDeviceTypes = new ArrayList();

        mDeviceTypes.add(c1);
        mDeviceTypes.add(c2);
        mDeviceTypes.add(c3);

        // Create adapter.
        mDeviceTypeAdapter = new MenuCardAdapter(mDeviceTypes);

        // Set recycler view to vertical.
        mDeviceTypeRecycler = (RecyclerView) rootView.findViewById(R.id.main_card_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDeviceTypeRecycler.setLayoutManager(llm);

        // Set recycler view adapter.
        mDeviceTypeRecycler.setAdapter(mDeviceTypeAdapter);

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