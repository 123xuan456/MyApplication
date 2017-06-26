package com.example.administrator.paipai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.paipai.adapter.MyListAdapter;
import com.example.administrator.paipai.data.BluetoothDeviceDetail;
import com.example.administrator.paipai.shareFile.FEShare;
import com.example.administrator.paipai.utils.MyLog;
import com.example.administrator.paipai.utils.SharedPreferencesUtil;
import com.example.administrator.paipai.view.RefreshableView;

public class MainActivity extends Activity {
    private TextView tv_state, tv_search_model;
    private RefreshableView refreshableView;
    private ListView listView_devices;
    private Button btn_search;
    //
    private MyListAdapter adapter;

    // BLE
    private int theSameCount = 0;

    private FEShare share = FEShare.getInstance();
    private boolean isReceiver=false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TextView
        tv_state = (TextView) findViewById(R.id.tv_state);
        tv_search_model = (TextView) findViewById(R.id.tv_search_model);

        // RefreshableView
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);

        // ListView
        listView_devices = (ListView) findViewById(R.id.listView_devices);

        // Button
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new MyClickListener());

//        if (!share.bluetoothAdapter.isEnabled()) {
//            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(turnOn, 1);
//            Toast.makeText(getApplicationContext(),
//                    getResources().getText(R.string.on), Toast.LENGTH_SHORT)
//                    .show();
//        } else {
//            Toast.makeText(getApplicationContext(),
//                    getResources().getText(R.string.bt_turned_on),
//                    Toast.LENGTH_SHORT).show();
//        }

        String address = SharedPreferencesUtil.getString(MainActivity.this, "address", "");
        Log.d("MainActivity","address="+ address);
        //判断有没有保存蓝牙地址
//        if (!TextUtils.isEmpty(address)){
//            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish();
//        }else {
            adapter = new MyListAdapter(share.devices, this, getLayoutInflater());
            listView_devices.setAdapter(adapter);
            // 点击列表
            listView_devices
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            MyLog.i("点击列表", ".");
                            // System.out.println(Integer.toString(position));

                            // 获取蓝牙设备的连接状态
                            // connetedAddr = deviceAddrs.get(position);
                            BluetoothDeviceDetail deviceDetail = share.devices
                                    .get(position);
                            if (!share.bluetoothAdapter.isEnabled()) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        getResources().getText(R.string.turn_on_bt),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //蓝牙设备地址
                            String addre = deviceDetail.address;
                            String name = deviceDetail.name;//设备名称
                            SharedPreferencesUtil.saveString(MainActivity.this, "address", addre);
                            SharedPreferencesUtil.saveString(MainActivity.this, "deviceName", name);
                            share.device = share.bluetoothAdapter
                                    .getRemoteDevice(addre);
                            share.intent.setClass(MainActivity.this,
                                    HomeActivity.class);
                            MyLog.i("创建", "intent3");
                            startActivity(share.intent);
                            MyLog.i("创建", "intent4");
                            finish();
                        }
                    });

            refreshableView.setOnRefreshListener(
                    new RefreshableView.PullToRefreshListener() {
                        @Override
                        public void onRefresh() {
                            MyLog.i("下拉刷新", "回调");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // MyLog.i("setOnRefreshListener", "---------");
                                    refreshableView.finishRefreshing();
                                    btn_search.performClick();

                                }
                            });

                        }
                    }, 0);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播接收器，接收并处理搜索结果
        registerReceiver(receiver, share.getIntentFilter());
        /**
         * 设置搜索模式 share.model_default() 获取一般搜索模式字符串, 只有SPP或BLE
         */
        tv_search_model.setText(share.model_default());
        btn_search.performClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_search: {
                    share.search();
                    break;
                }
                default:
                    break;
            }
        }
    }

    // 监听搜索设备
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            // if (share.tabId != R.d.communication) return;
            isReceiver=true;
            final String action = intent.getAction();
            // MyLog.i("main蓝牙回调", String.valueOf(intent));
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 找到设备
                final BluetoothDevice deviceGet = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                MyLog.i("找到设备", deviceGet.getAddress());
                    // 信号强度。
                    final int rssi = intent.getExtras().getShort(
                            BluetoothDevice.EXTRA_RSSI);
                    MyLog.i("设备信号强度", rssi + "");
                if (!TextUtils.isEmpty(deviceGet.getName())){
//                    String d = deviceGet.getName().substring(0, 7);
                    Log.d("MainActivity", "设备名称=" + deviceGet.getName());
//                    if (d.equals("INSPIRY")) {
                        deviceFound(deviceGet, rssi);
//                    } else {
//                        Log.d("MainActivity", "设备不一样" + deviceGet.getName());
//                    }

                }else {
                    Toast.makeText(context, "没搜到设备", Toast.LENGTH_SHORT).show();
                }

                // System.out.println(strShow);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                tv_state.setText(getResources().getText(R.string.searched));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                tv_state.setText(getResources().getText(R.string.searching));
            }
        }
    };

    @SuppressLint("NewApi")
    private void deviceFound(final BluetoothDevice device, final int rssi) {
        if (share.devices.size() > 0) {
            if (share.devices_addrs.contains(device.getAddress())) {
                theSameCount++;
                if (theSameCount > 30) {
                    theSameCount = 0;
                    final int index = share.devices_addrs.indexOf(device
                            .getAddress());
                    // MyLog.i(device.getAddress(), "更新信号");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                share.devices.get(index)
                                        .setDetail(device.getName(),
                                                device.getAddress(), rssi);
                                // MyLog.i("5555", "4");
                                adapter.notifyDataSetChanged();
                        }
                    });

                }
                return;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // MyLog.i("6666", "1"+share.devices.size());
                share.addDevice(new BluetoothDeviceDetail(device.getName(),
                        device.getAddress(), rssi));
                // MyLog.i("6666", "2");
                adapter.notifyDataSetChanged();
                // MyLog.i("6666", "3");

            }
        });
    }

    // @Override
    // public void onDetachedFromWindow() {
    // super.onDetachedFromWindow();
    // if (share.BA.isDiscovering()){
    // share.BA.cancelDiscovery();
    // }
    // }
    public Resources getSResources() {
        // TODO Auto-generated method stub
        return getResources();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        share.stopSearch();
        if(isReceiver){
            unregisterReceiver(receiver);
        }
    }
}
