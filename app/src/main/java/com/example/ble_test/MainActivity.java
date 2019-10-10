package com.example.ble_test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.*;
import android.bluetooth.*;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.app.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import com.example.ble_test.BleAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="ble_tag" ;
    private List<BluetoothDevice> mDatas;           //蓝牙设备
    private List<Integer> mRssis;                   //信号强度
    private ListView MyBleList;                    //蓝牙信息列表
    private ArrayAdapter mAdapter;
    private TextView Nums;
    private Button btn;
    private List mList;
    private ProgressBar pbSearchBle;
    private static int BleNum = 0;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private boolean isScaning=false;
    private BluetoothGatt mBluetoothGatt;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        MyBleList = findViewById(R.id.BLE_List);
        initData();
        Get_Permissions();
    }
    /*
    **
     */
    private void initData(){
        mRssis = new ArrayList<>();
        mDatas = new ArrayList<>(); //设备地址信息
        //mAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, mDatas);
        mAdapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1, mDatas);
        MyBleList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();//刷新数据
        pbSearchBle=findViewById(R.id.progressBar_Search);
        Nums = findViewById(R.id.textView_Num);
        btn = findViewById(R.id.button_scan);
        pbSearchBle.setVisibility(View.GONE);


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

    public void Get_Permissions()
    {
        //申请读写权限
        if (ContextCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            Log.e(TAG, "申请文件操作权限...");
        }else
        {
            Log.e(TAG, "已允许进行文件操作...");
        }

        //申请打开蓝牙
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
    /**
     * 停止扫描设备
     */
    public void stopScanDevice(){
        isScaning = false;
        BleNum = 0;
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
        Log.e(TAG, "devcie:" + device.getName()+ " "+ rssi);
        //Log.e(TAG, "mm:" + mDatas.toString());
        try {
            if (!mDatas.contains(device)) {
                Log.e(TAG, "exist");
                BleNum++;
                Nums.setText("共"+BleNum+"个");
                mDatas.add(device);
                String name = device.getName();
                if(name == null)
                    name = "Unknow Device";
                mRssis.add(rssi);
                MyBleList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();//刷新数据
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }
};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

