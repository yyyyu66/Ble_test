package com.example.ble_test;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

public  class BleAdapter extends BaseAdapter{
    private Context mContent;
    private List<BluetoothDevice> mBluetoothDevice;
    private List<Integer> mRssis;

    public BleAdapter(Context mContent, List<BluetoothDevice> bluetoodevcie, List<Integer> rssi){
        this.mContent = mContent;
        this.mBluetoothDevice = bluetoodevcie;
        this.mRssis = rssi;
}

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}

