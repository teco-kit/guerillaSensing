package com.teco.vindi.guerillasensingui3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Vincent Diener on 25.03.2015.
 */
public class PagerAdapter extends FragmentPagerAdapter{
    private final List<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return this.fragments.get(i);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int i) {
        return this.fragments.get(i).toString();
    }
}
