package edu.teco.guerillaSensing.helpers;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Singleton for requesting device network status.
 */
public class OnlineStatusHelper {

    // The instance of the singleton. Will be null until it is first used.
    private static OnlineStatusHelper mInstance = null;

    // The connectivity manager, needed to get information about the devices network status.
    private ConnectivityManager mConnectivityManager;

    // The current status.
    private boolean mOnline;

    /**
     * Returns the online status helper singleton object.
     * @return  The online status helper singleton object.
     */
    public static OnlineStatusHelper getInstance(){
        // If the singleton object does not exist yet, create it.
        if(mInstance == null)
        {
            mInstance = new OnlineStatusHelper();

        }
        return mInstance;
    }

    /**
     * Returns the current online status
     * @param context The context.
     * @return Current online status of the device.
     */
    public boolean isOnline(Context context) {
        // Request the connectivity manager the first time.
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        // If it is still null, the device might not have network system services installed.
        if (mConnectivityManager == null) {
            mOnline = false;
        } else {
            // Request device network info and return current status.
            NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();

            mOnline = false;
            if (ni == null) {
                mOnline = false;
            } else {
                mOnline = ni.isConnected();
            }
        }

        return mOnline;
    }

    /**
     * Check for Bluetooth.
     * @return True if Bluetooth is available.
     */
    public static boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
    }

    /**
     * Enable bluetooth.
     * @return True if Bluetooth is being turned on.
     */
    public static boolean enableBluetooth() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.enable();
    }
}
