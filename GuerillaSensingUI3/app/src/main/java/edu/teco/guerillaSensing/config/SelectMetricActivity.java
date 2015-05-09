package edu.teco.guerillaSensing.config;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toast;

import edu.teco.guerillaSensing.NavigationDrawerActivity;
import edu.teco.guerillaSensing.R;

/**
 * The config screen of the main map view.
 */
public class SelectMetricActivity extends NavigationDrawerActivity {

    // The main view of this activity.
    private View mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get main layout and initialize nav drawer on it.
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_select_metric, null);
        initNavigationDrawer(mMainLayout, false);
        setContentView(mMainLayout);

        setTitle("Map Config");
    }

    /**
     * The preference fragment. Load preferences from XML.
     */
    public static class SelectMetricFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_metric);

        }
    }

    @Override
    public void drawerItemClicked(int item) { }
}
