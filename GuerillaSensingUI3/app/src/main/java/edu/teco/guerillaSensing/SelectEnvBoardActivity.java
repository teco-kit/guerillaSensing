package edu.teco.guerillaSensing;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.teco.envboardservicelib.EnvBoardService;
import edu.teco.envboardservicelib.IServiceCallback;
import edu.teco.envboardservicelib.IEnvBoardService;

public class SelectEnvBoardActivity extends NavigationDrawerActivity implements ServiceConnection, IServiceCallback {

    private View mMainLayout;
    private IEnvBoardService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = getLayoutInflater().inflate(R.layout.activity_start_menu, null);
        initNavigationDrawer(mMainLayout);
        setTitle("EnvBoard");
        setContentView(mMainLayout);

        Intent startServiceIntent = new Intent(this, EnvBoardService.class);
        startService(startServiceIntent);
        bindService(startServiceIntent, this, BIND_AUTO_CREATE);
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


    @Override
    protected void onPause() {
        super.onPause();

        Intent stopServiceIntent = new Intent(this, EnvBoardService.class);
        stopService(stopServiceIntent);
        unbindService(this);
    }

    /**
     * Called when a connection to the Service has been established, with
     * the {@link IBinder} of the communication channel to the
     * Service.
     *
     * @param name    The concrete component name of the service that has
     *                been connected.
     * @param service The IBinder of the Service's communication channel,
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        this.service = IEnvBoardService.Stub.asInterface((IBinder) service);

        try {
            this.service.registerServiceCallback(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {

        this.service = null;
    }

    /**
     * Called, when discovery has finished
     */
    @Override
    public void discoveryFinished(String[] list) throws RemoteException {

    }

    /**
     * Called, when the number of sensor values stored has changed
     *
     * @param count
     */
    @Override
    public void sensorDataCountChanged(final int count) throws RemoteException {

    }

    /**
     * Called, when some sensor values have been successfully uploaded to the server
     *
     * @param count
     */
    @Override
    public void sensorDatesUploaded(int count) throws RemoteException {

    }

    /**
     * Retrieve the Binder object associated with this interface.
     * You must use this instead of a plain cast, so that proxy objects
     * can return the correct result.
     */
    @Override
    public IBinder asBinder() {
        return null;
    }
}
