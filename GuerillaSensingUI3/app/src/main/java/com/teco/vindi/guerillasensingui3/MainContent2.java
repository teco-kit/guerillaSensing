package com.teco.vindi.guerillasensingui3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Vector;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MainContent2 extends Fragment {

    private static String FRAGMENT_NAME = "Graph";

    public class ContactInfo {
        protected String name;
        protected String surname;
        protected String email;
        protected static final String NAME_PREFIX = "Name_";
        protected static final String SURNAME_PREFIX = "Surname_";
        protected static final String EMAIL_PREFIX = "email_";

        public ContactInfo(String name, String surname, String email) {
            this.name = name;
            this.surname = surname;
            this.email = email;

        }
    }


    public MainContent2() {
        // Required empty public constructor
    }


    private MenuCardAdapter mMenuCardAdapter;
    private RecyclerView mCardRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_content2, container, false);

        ContactInfo c1 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c2 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c3 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c4 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c5 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c6 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c7 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c8 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c9 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");
        ContactInfo c10 = new ContactInfo("Vincent", "Diener", "vincent.diener@gmail.com");

        List mMenuItems = new Vector<ContactInfo>();
        mMenuItems.add(c1);
        mMenuItems.add(c2);
        mMenuItems.add(c3);
        mMenuItems.add(c4);
        mMenuItems.add(c5);
        mMenuItems.add(c6);
        mMenuItems.add(c7);
        mMenuItems.add(c8);
        mMenuItems.add(c9);
        mMenuItems.add(c10);

        mMenuCardAdapter = new MenuCardAdapter(mMenuItems);
        mCardRecycler = (RecyclerView) rootView.findViewById(R.id.main_card_view);
        // Set the adapter for the list view
        FragmentActivity c = getActivity();
        LinearLayoutManager llm = new LinearLayoutManager(c);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCardRecycler.setLayoutManager(llm);
        mCardRecycler.setAdapter(mMenuCardAdapter);


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public String toString() {
        return FRAGMENT_NAME;
    }


}
