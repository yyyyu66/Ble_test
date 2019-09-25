package com.example.ble_test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.bluetooth.*;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="ble_tag" ;
    private List<BluetoothDevice> mDatas;
    private List<Integer> mRssis;                   //信号强度
    private ListView MyBleList;                    //蓝牙信息列表
    private ListView testList;
    private TextView Nums;
    private Button btn;
    private List mList;
    private ProgressBar pbSearchBle;
    private static int BleNum = 0;

    private ArrayAdapter mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private boolean isScaning=false;
    private BluetoothGatt mBluetoothGatt;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        MyBleList = findViewById(R.id.BLE_List);
        initData();

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null)
        {
            Log.e(TAG, "No BluetoothAdapter...");
            return;
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,0);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
            }
    }
    /*
    **
     */
    private void initData(){
        mRssis = new ArrayList<>();
        mDatas = new ArrayList<>(); //设备地址信息
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, mDatas);
        pbSearchBle=findViewById(R.id.progressBar_Search);
        Nums = findViewById(R.id.textView_Num);
        btn = findViewById(R.id.button_scan);


        pbSearchBle.setVisibility(View.GONE);
        MyBleList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();//刷新数据

        /****重写监听事件****/
        MyBleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "ClickListener...");
                if(isScaning){
                    pbSearchBle.setVisibility(View.GONE);
                    isScaning = false;
                    btn.setText("搜索设备");
                    stopScanDevice();//停止扫描
                }
                BluetoothDevice BleDevice = mDatas.get(position);  //获取蓝牙设备
                //进入第二个界面
                Intent intent2 =new Intent(MainActivity.this, second.class); //新建Intent对象
                intent2.putExtra(second.EXTRAS_DEVICE_NAME, BleDevice.getName());
                intent2.putExtra(second.EXTRAS_DEVICE_ADDRESS, BleDevice.getAddress());
                Log.e(TAG, "BleDevice:"+BleDevice.getName() + BleDevice.getAddress());
                startActivity(intent2);
            }
        });
    }

    /**
     * 停止扫描设备
     */
    public void stopScanDevice(){
        isScaning = false;
        pbSearchBle.setVisibility(View.GONE);
        mBluetoothAdapter.stopLeScan(scanCallback);
    }

    /**
     * 搜索设备
     */
    public void scanDevice(View view) {
            Log.e(TAG, "start scaning...");
            if(isScaning == false) {
                pbSearchBle.setVisibility(View.VISIBLE);
                isScaning = true;
                btn.setText("停止搜索");
                mBluetoothAdapter.startLeScan(scanCallback);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothAdapter.stopLeScan(scanCallback);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isScaning = false;
                                Log.e(TAG, "stop scaning...");
                            }
                        });
                    }
                }, 10000);
            }else {
                isScaning = true;
                pbSearchBle.setVisibility(View.GONE);
                stopScanDevice();
                btn.setText("搜索设备");
            }
    }
/*
**扫描设备的回调函数，实现动态地显示添加设备
 */
    BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.e(TAG, "run: scaning...");
        Log.e(TAG, "devcie:" + device);
        Log.e(TAG, "mm" + mDatas.toString());
        try {
            if (!mDatas.contains(device)) {
                Log.e(TAG, "exist");
                BleNum++;
                Nums.setText("共"+BleNum+"个");
                mDatas.add(device);
                mRssis.add(rssi);
                MyBleList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();//刷新数据
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }
};
}

