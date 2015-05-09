package edu.teco.guerillaSensing.config;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.Toast;

import edu.teco.guerillaSensing.NavigationDrawerActivity;
import edu.teco.guerillaSensing.R;

/**
 * The config screen of the EnvBoard scan view.
 */
public class ServiceConfigActivity extends NavigationDrawerActivity {

    // The main view of this activity.
    private View mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get main layout and initialize nav drawer on it.
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_service_config, null);
        initNavigationDrawer(mMainLayout, false);
        setContentView(mMainLayout);

        setTitle("Service Config");
    }

    /**
     * The preference fragment. Load preferences from XML.
     */
    public static class ServiceConfigFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_service);

        }
    }

    @Override
    public void drawerItemClicked(int item) { }
}
