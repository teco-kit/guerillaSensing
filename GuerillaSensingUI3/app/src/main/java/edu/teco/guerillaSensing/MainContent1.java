package edu.teco.guerillaSensing;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MainContent1 extends Fragment {

    private static String FRAGMENT_NAME = "Map";

    public MainContent1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_content1, container, false);
    }

    @Override
    public String toString() {
        return FRAGMENT_NAME;
    }

}