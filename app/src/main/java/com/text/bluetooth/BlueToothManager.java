package com.text.bluetooth;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Zhu TingYu on 2018/11/26.
 */

public class BlueToothManager {

    private static final String TAG = "BlueToothManager";

    private static final int REQUEST_ENABLE_BT = 0x999;
    private static BlueToothManager mBlueToothManager;
    private static BluetoothAdapter mBluetoothAdapter;

    private Activity mActivity;

    List<BluetoothDevice> mDeviceList = new ArrayList<>();
    public MutableLiveData<List<BluetoothDevice>> mDataFindDevice = new MutableLiveData<>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if(!mDeviceList.contains(device)){
                    mDeviceList.add(device);
                }
                mDataFindDevice.setValue(mDeviceList);
                Log.e(TAG, "找到设备：" + device.toString());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.e(TAG, "扫描中。。。");
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Log.e(TAG, "扫描结束。。。");
            }
        }
    };

    public static BlueToothManager get() {
        synchronized (BlueToothManager.class) {
            if (mBlueToothManager == null) {
                mBlueToothManager = new BlueToothManager();
            }
            if (mBluetoothAdapter == null) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }
        return mBlueToothManager;
    }

    public BlueToothManager openBlueTooth(Activity activity) {

        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        return this;
    }

    public void getBlueDevices() {
        mBluetoothAdapter.startDiscovery();
    }

    public void destroy(Activity activity) {
        activity.unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }
}
