package com.example.ble_test;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public  class BleAdapter extends BaseAdapter{
    private Context mContent;
    private List<BluetoothDevice> mBluetoothDevices;
    private List<Integer> mRssis;

    public BleAdapter(Context mContent, List<BluetoothDevice> bluetoodevcie, List<Integer> rssi){
        this.mContent = mContent;
        this.mBluetoothDevices = bluetoodevcie;
        this.mRssis = rssi;
    }

    @Override
    public int getCount() {
        return mBluetoothDevices.size();
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
        ViewHolder holder;
/*
        if(view == null)
        {
            view = mInflater.inflate(R.layout.activity_main, null);
            holder = new ViewHolder();
            holder.name = (TextView)view.findViewById(R.id.textView_name);
            holder.Address = (TextView)view.findViewById(R.id.textViewAddress);
            holder.Rssi  = (TextView)view.findViewById(R.id.textViewRssis);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        String name = mBluetoothDevices.get(i).getName();
        if(name == null)
            name = "Unknow Device";
        holder.name.setText(name);
        holder.Address.setText("地址:"+mBluetoothDevices.get(i).getAddress());
        holder.Rssi.setText("信号:"+ mRssis.get(i).toString());
*/
        return view;
    }
    public class ViewHolder{
        public TextView Address;
        public TextView name;
        public TextView Rssi;
    }

    public void clear() {
        mBluetoothDevices.clear();
        mRssis.clear();
        this.notifyDataSetChanged();
    }
}

