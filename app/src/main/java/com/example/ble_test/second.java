package com.example.ble_test;

import android.bluetooth.*;
import android.content.Context;
import android.os.*;
import android.util.Log;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public  class second extends AppCompatActivity {
    private static final String TAG ="Second_UI " ;
    private EditText Cmd_Text;                //命令文本
    private Button   button_send;            //发送按键,查询日志命令
    private Button   button_send1;           //发送按键,查询NB信号命令
    private Button   button_send2;           //发送按键,查询日志命令
    private Button   button_send3;           //发送按键,查询日志命令  button_ClearLog
    private Button   button_Disconnect;     //断开连接
    private Button   button_Connect;        //连接设备

    private Button   Btn_SaveLog;           //保存日志
    private Button   Btn_ClearLog;          //清空日志显示区

    private TextView SendLog;               //蓝牙接收的内容
    private TextView BLE_Name;              //蓝牙设备信息：名称和MAC
    private TextView BLE_State;             //蓝牙状态
    private ProgressBar BLE_Connecting;     //进度条
    private TextView BLE_DeviceLog;         //设备返回的数据

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private BluetoothAdapter mBluetoothAdapter;     //蓝牙适配器对象
    private BluetoothManager mBluetoothManager;     //蓝牙管理器
    private BluetoothGatt mBluetoothGatt;           //蓝牙协议
    private BluetoothDevice mBluetoothDevice;       //蓝牙设备
    private String BleName;
    private String BleAddress;
    boolean isConnected = false;
    //服务特征值
    private UUID write_UUID_Service;
    private UUID write_UUID_Character;
    private UUID read_UUID_Service;
    private UUID read_UUID_Character;
    private UUID notify_UUID_Service;
    private UUID notify_UUID_Character;
    private UUID indicate_UUID_Service;
    private UUID indicate_UUID_Character;
    //private
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.second);
        initSecondUI();

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        Log.e(TAG, "Hello Second UI...");
        try {
            Intent bleInfo = getIntent();
            BleName     = bleInfo.getStringExtra(EXTRAS_DEVICE_NAME);
            BleAddress  = bleInfo.getStringExtra(EXTRAS_DEVICE_ADDRESS);
            BLE_Name.setText("设备:"+BleName +","+BleAddress);
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(BleAddress);//根据蓝牙地址获取蓝牙设备
            /********连接设备*********/
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mBluetoothGatt = mBluetoothDevice.connectGatt(second.this, true,
                            gattCallBack, BluetoothDevice.TRANSPORT_LE);
                } else {
                    mBluetoothGatt = mBluetoothDevice.connectGatt(second.this, true,
                            gattCallBack);
                }
            }catch (Exception ex)
            {
                Log.e(TAG, ex.toString());
            }
            Log.e(TAG ,"BleDevice:"+ BleName + BleAddress);
        }catch(Exception ex) {
            Log.e(TAG, ex.toString());
            return;
        }
    }

    public void initSecondUI()
    {
        button_send = findViewById(R.id.button_sendData1);
        button_send1 = findViewById(R.id.button_sendData2);
        button_send2 = findViewById(R.id.button_GetKeys);
        //button_send3 = findViewById(R.id.button_sendData4);
        Btn_SaveLog = findViewById(R.id.button_SaveLog);
        Btn_ClearLog = findViewById(R.id.button_ClearLog);
        button_Disconnect = findViewById(R.id.button_Disconnect);
        button_Connect = findViewById(R.id.button_Connect);

        SendLog = findViewById(R.id.textView_LogRes);
        BLE_DeviceLog = findViewById(R.id.textView_LogInfo);//显示设备的日志信息
        BLE_Name = findViewById(R.id.textView_BLEMAC);
        BLE_State = findViewById(R.id.textView_State);
        BLE_Connecting = findViewById(R.id.progressBar_Connect);

        //重写OnClick
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second.this.writeDataToBleDevcie("0003");
                Log.e(TAG, "发送命令0003...");
            }
        });//发送查询日志命令

        //创建日志文件
        File file = new File(Environment.getExternalStorageState(),"Ble_log.txt");
        Log.e(TAG, "file:"+file);//文件操作
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Btn_SaveLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.e(TAG, "保存日志...");
            }
        });//保存日志

        Btn_ClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BLE_DeviceLog.setText(null);
                Log.e(TAG, "清空日志显示区...");
            }
        });//清空日志显示区

        button_send1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                second.this.writeDataToBleDevcie("0001");
                Log.e(TAG, "发送命令0001...");
            }
        });//发送查询日志命令

        button_Disconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mBluetoothGatt.disconnect();
                BLE_State.setText("断开连接");
                Log.e(TAG, "断开连接...");
            }
        });//断开蓝牙的连接

        button_Connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mBluetoothGatt = mBluetoothDevice.connectGatt(second.this, true,
                            gattCallBack, BluetoothDevice.TRANSPORT_LE);
                } else {
                    mBluetoothGatt = mBluetoothDevice.connectGatt(second.this, true,
                            gattCallBack);
                }
                BLE_State.setText("连接中");
                BLE_Connecting.setVisibility(View.VISIBLE);
            }
        });//蓝牙连接
    }

    /*
     **监测蓝牙状态的变化
     */
    private final BluetoothGattCallback gattCallBack = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG, "onConnectionStateChange");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "...连接中...");
                BLE_Connecting.setVisibility(View.VISIBLE);
                gatt.discoverServices();
            } else {
                mBluetoothGatt.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "连接失败");
                        BLE_State.setText("连接失败");
                        BLE_Connecting.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            isConnected = true;
            initServiceAndCharacteristic();
            boolean success = false;
            BluetoothGattCharacteristic chara = mBluetoothGatt.getService(notify_UUID_Service).getCharacteristic(notify_UUID_Character);
            success = mBluetoothGatt.setCharacteristicNotification(chara, true);
            if (success) {
                Log.e(TAG, "Notify success..." + notify_UUID_Service + notify_UUID_Character);
                for (BluetoothGattDescriptor dp : chara.getDescriptors()) {
                    if (dp != null) {
                        if ((chara.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        }
                        gatt.writeDescriptor(dp);
                    }
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "已连接");
                    BLE_State.setText("已连接");
                    BLE_Connecting.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            final byte[] value = characteristic.getValue();
            final String str = new String(value).toString();
            Log.e(TAG, "Received str:" + str);
            Log.e(TAG, "Received byte:" + value[4] + value[5] + value[6] + value[7]);
            if(str.contains("0003"))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String Valid_info = str.substring(8, str.length());
                            if (str.length() > 4) {
                                long tmp = 0;
                                for(int i = 0; i < 4; i++)
                                {
                                    if(value[4+i] > 0)    tmp += (value[4+i] << (24-8*i));
                                    else                  tmp += ((256+value[4+i]) << (24-8*i));
                                }
                                Log.e(TAG, "Received:" + tmp);
                                long timestamp = tmp * 1000L;
                                String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
                                Log.e(TAG, "Received:" + timestamp + "==" + dateString);
                                BLE_DeviceLog.append(dateString + " " + Valid_info);
                            } else
                                BLE_DeviceLog.append(Valid_info);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        }
                    }
                });
            }
            else if(str.contains("0001"))
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String Valid_info;
                            byte[] pos = new byte[2];
                            for(byte i = 0, j = 0; i < value.length; i++ )
                            {
                                if(value[i] == 38)
                                    pos[j++] = i;
                            }
                            Log.e(TAG, String.valueOf(pos[0])+String.valueOf(pos[1]));
                            Valid_info = str.substring(pos[0]+1, pos[1]);
                            Log.e(TAG, "Received:" + "Valid_info" );
                            BLE_DeviceLog.append(  "NB信号值:" + Valid_info+ "\r\n");
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        }
                    }
                });
            }
        }
    };

    /**************十六进制字符串转换为字节数组*****************/
    private byte[] HexStringToBytes(String hex)
    {
        try {
            String Date_Char = hex.substring(4, 8);
            Log.e(TAG, "Date_Char:" + Date_Char);
            Date_Char = Date_Char.toUpperCase();
            int len = Date_Char.length();
            byte[] b = new byte[len];
            char[] hc = Date_Char.toCharArray();
            for (int i = 0; i < len; i++) {
                b[i] = (byte) (charToByte(hc[i]));
                //Log.e(TAG, " b[i]:" +  b[i]);
            }
            return b;
        }catch(Exception ex)
        {
            byte[] b = new byte[8];
            b[0] = 5;b[1] = 0xc;b[2] = 2;b[3] = 0xa;
            b[4] = 3;b[5] = 0xd;b[6] = 0;b[7] = 0;
            return b;
        }
    }

    /**************字符转换为字节*****************/
    private static byte charToByte(char c) {
        byte tmp = (byte) "0123456789ABCDEF".indexOf(c);
        //Log.e(TAG, "Char:" + tmp);
        return tmp;
    }

    private void  initServiceAndCharacteristic(){
        List<BluetoothGattService> bluetoothGattServices = mBluetoothGatt.getServices();
        for(BluetoothGattService bluetoothGattService:bluetoothGattServices){
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for(BluetoothGattCharacteristic characteristic:characteristics){
                int charaProp = characteristic.getProperties();
                Log.e(TAG, "charaProp="+String.valueOf(charaProp));

                    if((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0){
                       read_UUID_Character = characteristic.getUuid();
                       read_UUID_Service = bluetoothGattService.getUuid();
                      Log.e(TAG, "read_char_service"+read_UUID_Character+"---"+read_UUID_Service);
                    }

                    if ((charaProp == BluetoothGattCharacteristic.PROPERTY_WRITE)) { //获取读UUID   8
                        write_UUID_Character=characteristic.getUuid();
                        write_UUID_Service=bluetoothGattService.getUuid();
                        Log.e(TAG,"write_chara="+write_UUID_Character+"----write_service="+write_UUID_Service);
                    }

                    if ((charaProp == BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {//获取通知UUID  16
                        notify_UUID_Character=characteristic.getUuid();
                        notify_UUID_Service=bluetoothGattService.getUuid();
                        Log.e(TAG,"notify_chara="+notify_UUID_Character+"----notify_service="+notify_UUID_Service);
                    }
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {//  32
                        indicate_UUID_Character=characteristic.getUuid();
                        indicate_UUID_Service=bluetoothGattService.getUuid();
                        Log.e(TAG,"indicate_chara="+indicate_UUID_Character+"----indicate_service="+indicate_UUID_Service);
                    }
            }
        }
    };

/************************************************************************
 ********************向蓝牙设备发送数据或指令
 ************************************************************************/
    public void writeDataToBleDevcie(String cmd){
        try {
            BluetoothGattService service = mBluetoothGatt.getService(write_UUID_Service);
            BluetoothGattCharacteristic charaWrite = service.getCharacteristic(write_UUID_Character);
            Log.e(TAG,write_UUID_Service.toString() + write_UUID_Character.toString());
            charaWrite.setValue(cmd);
            mBluetoothGatt.writeCharacteristic(charaWrite);
            SendLog.append(cmd);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


}