package com.text.bluetooth;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private MainAdapter mMainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RxPermissions rxPermissions = new RxPermissions(this);

        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainAdapter = new MainAdapter();
        mRecyclerView.setAdapter(mMainAdapter);

        BlueToothManager.get().mDataFindDevice.observe(this, new Observer<List<BluetoothDevice>>() {
            @Override
            public void onChanged(@Nullable List<BluetoothDevice> bluetoothDevices) {
                mMainAdapter.setNewData(bluetoothDevices);
            }
        });

        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)//这里填写所需要的权限
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        BlueToothManager.get()
                                .openBlueTooth(MainActivity.this)
                                .getBlueDevices();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlueToothManager.get().destroy(this);
    }

    private class MainAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

        public MainAdapter() {
            super(R.layout.item_main, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, BluetoothDevice item) {
            StringBuilder sb = new StringBuilder();
            sb.append(item.getAddress());
            sb.append("\n");
            sb.append(item.getBondState());
            sb.append("\n");
            sb.append(item.getBluetoothClass());
            sb.append("\n");
            sb.append(item.getName());
            sb.append("\n");
            sb.append(item.getUuids());
            helper.setText(R.id.tvContent, sb.toString());
        }
    }
}
