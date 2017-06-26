package com.example.administrator.paipai;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.paipai.adapter.MyAdapter;
import com.example.administrator.paipai.data.DataModel;
import com.example.administrator.paipai.data.DealData;
import com.example.administrator.paipai.shareFile.FEShare;
import com.example.administrator.paipai.utils.FileClass;
import com.example.administrator.paipai.utils.MyLog;
import com.example.administrator.paipai.utils.SharedPreferencesUtil;
import com.example.administrator.paipai.view.AnnotationDialog;
import com.example.administrator.paipai.view.SetDialog;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class HomeActivity extends Activity implements View.OnClickListener {
    private final static String TAG = HomeActivity.class.getSimpleName();
    /**
     * isTouchItem值的改变在
     * com.excel.tool.MyHScrollView中去判断--解决ListView很容易触发点击事件
     * 里面处理 控制表格滑动很容易触发的点击事件问题
     */
    public static boolean isTouchItem = false;
    Context context;
    private ListView listView;
    private LinearLayout toplayout;
    private MyAdapter myAdapter;
    public List<DataModel> BaseData;
    private HorizontalScrollView horizontalScrollView;
    private EditText editText, editText1;
    private ImageView iv;
    private View view_pop;
    private PopupWindow mPopupwinow;
    private TextView toll;
    private TextView gallery;
    private TextView tv1, tv3, tv4;
    private TextView tv_connectState;
    private SetDialog dialog;
    private DisplayMetrics displayMetrics;
    private Button btn;
    private String location;
    private String number;
    private String money;
    private String Pay;//支付方式
    private static final String PAYMENT_SUCCESS = "55FF55010003021103FFDE";//支付成功
    private static final String PAYMENT_FAILURE = "55FF550100030211048F39";//支付失败
    private static final String START_SCAN = "55FF55010003021102EFFF";//开始扫描
    private static final String CONNECTION_SUCCESS = "55FF55010003021101DF9C";//连接成功
    private DataModel model;

    private static final String url = "http://192.168.3.110:8081/ets/f/paymentrecords/payRecordsInter/saveRecords";

    private BluetoothGattCallback mGattCallback;

    private FEShare share = FEShare.getInstance();
    private boolean isRunning;
    private byte[] byte_send;
    private String annotation;
    private boolean isPay;// 微信/支付宝
    //判断是否连接成功
    private boolean isConnect = false;
    private boolean isReciver = false;
    private String address;//MainActivity中传来的地址
    private BroadcastReceiver mItemViewListClickReceiver1;
    private LocalBroadcastManager broadcastManager;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private boolean isThread=false;

    /**
     * 1.不使用横屏，横屏回执行两遍onCreate方法
     * 2.displayMetrics用来控制PopupWindow的宽度
     * 3.三个广播接收器方便自动连接
     *  3.1搜索设备：
     *      3.1.1   找到蓝牙设备，判断名称是否包含INSPIRY
     *      3.1.2   判断设备地址与sp中保存的地址是否相同
     *          3.1.2.1相同再发一个广播，开启线程进行连接设备
     *  3.2连接设备：
     *      3.2.1   监听设备连接的情况，断开/连接中
     * 4.判断address(设备地址)有没有保存，（在MainActivity中通过SP保存）
     * 5.address没数据说明是第一次打开或没连接过设备，要跳到MainActivity中进行设备的选择
     * 6.有数据：
     *  6.1判断是否打开蓝牙，两种方式（1.弹出对话框共用户选择，用户选择取消，直接退出程序2.直接打开蓝牙，没有提示）
     *  6.2本地数据库操作，显示之前保存的数据
     *  6.3只有Sp地址与搜到的地址相同才能进行连接(使用广播通知设备的连接)
     *  6.4通过设备地址获取到BluetoothDevice device
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        //        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        //获取屏幕宽度
        displayMetrics = new DisplayMetrics();
        (HomeActivity.this).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        setContentView(R.layout.activity_home);
        //直接打开蓝牙不询问用户
        share.bluetoothAdapter.enable();
        context = HomeActivity.this;
        share.context = this;
        share.init();
        //保存的蓝牙地址
        address = SharedPreferencesUtil.getString(HomeActivity.this, "address", "");
        //判断有没有保存蓝牙地址
        if (TextUtils.isEmpty(address)) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            goBackToDo();
        } else {
            //已经打开蓝牙、、
            Toast.makeText(getApplicationContext(),
                    getResources().getText(R.string.bt_turned_on),
                    Toast.LENGTH_SHORT).show();
            viewInit();

            SQLiteDatabase db = Connector.getDatabase();//数据库操作
            BaseData = new ArrayList<DataModel>();
            BaseData = DataSupport.findAll(DataModel.class);
            myAdapter = new MyAdapter(context, toplayout);
            if (BaseData != null) {
                myAdapter.setHostBaseData(BaseData);
                listView.setAdapter(myAdapter);
            }
            byte_send = new byte[0];
            isRunning = true;
            setUpBLECallBack();
            MyLog.i(TAG, "onCreat");
        }
    }


    //用户选择是否打开蓝牙
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),
                        getResources().getText(R.string.on),
                        Toast.LENGTH_SHORT).show();
            }else if (resultCode==RESULT_CANCELED){
                goBackToDo();
            }
        }

    }

    @Override
    protected void onStart() {
        MyLog.i(TAG, "onStart");
        super.onStart();
    }
    @Override
    protected void onResume() {
        MyLog.i(TAG, "onResume");
        share.search();//搜索设备操作
        /**
         * 设置为横屏
         */
        //        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        //            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //        }
        registerReceiver(receiverDevice, share.getIntentFilter());//搜索设备
        registerReceiver(receiver,share.getIntentFilter());//连接设备
        broadcast();//接受搜索成功的广播
        isReciver=true;
        super.onResume();
    }


    private void readData() {
        if (share.isSPP) {
            new Thread(new Runnable() {
                // private Scanner r = new Scanner(mmInStream);
                @Override
                public void run() {
                    byte[] buffer = new byte[1000];// 缓冲数据流
                    int bytes = 0;// 返回读取到的数据
                    while (isRunning) {
                        try {
                            bytes = share.inputStream.read(buffer);
                            // 此处处理数据……
                            String strBuf = new String(buffer, 0, bytes);
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("strBuf", strBuf);
                            msg.setData(bundle);
                            ConnectHandler.sendMessage(msg);
                        } catch (IOException e) {
                            // e.printStackTrace();
                            isRunning = false;
                            // goBackToDo();
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("value", "0");
                            msg.setData(bundle);
                            disConnectHandler.sendMessage(msg);
                        }

                    }
                }
            }).start();
        }

    }
    private void Popupwindow() {
        if (mPopupwinow == null) {
            //新建一个popwindow
            mPopupwinow = new PopupWindow(view_pop,
                    displayMetrics.widthPixels / 2,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
            //设置popwindow的背景颜色,不设置无法获取以外控件焦点
            mPopupwinow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.colorselocter_bg));
        }
        mPopupwinow.setOutsideTouchable(true);//触碰PopupWindow以外的布局可以获得触摸事件
        //设置popwindow的位置  tv:为微信右上角+号view，居于+号下方
        mPopupwinow.showAsDropDown(iv, 0, 0);
        LinearLayout line_toll = (LinearLayout) view_pop.findViewById(R.id.line_toll);
        LinearLayout line_gallery = (LinearLayout) view_pop.findViewById(R.id.line_gallery);
        LinearLayout line_connection = (LinearLayout) view_pop.findViewById(R.id.line_connection);
        toll = (TextView) view_pop.findViewById(R.id.tv2);
        gallery = (TextView) view_pop.findViewById(R.id.textView2);
        tv1 = (TextView) view_pop.findViewById(R.id.tv1);
        tv3 = (TextView) view_pop.findViewById(R.id.tv3);

        line_toll.setOnClickListener(this);
        line_gallery.setOnClickListener(this);
        line_connection.setOnClickListener(this);
        toll.setText(location);
        gallery.setText(number);
    }

    private void broadcast() {
        broadcastManager = LocalBroadcastManager.getInstance(HomeActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DEVICE_CONNECTION");//
        mItemViewListClickReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String name = intent.getStringExtra("Name");
                //确定连接成功后点击设置按钮显示连接的设备名称
                tv4.setText(name);
                System.out.println("来了！！！Name");
                     /*
                        *1.必须已经打开蓝牙，
                        * 2.必须先搜索到蓝牙设备
                        * 3.sp保存的蓝牙设备地址必须与搜到的地址相同
                     */
                share.device = share.bluetoothAdapter
                        .getRemoteDevice(address);
                //连接设备
                new Thread(mRunnable).start();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver1, intentFilter);

    }
    private BroadcastReceiver receiverDevice = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 找到设备
                final BluetoothDevice deviceGet = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                MyLog.i("找到设备", deviceGet.getAddress());
                //                String d = deviceGet.getName().substring(0, 7);
                if (!TextUtils.isEmpty(deviceGet.getName())) {
                    //                    if (d.equals("INSPIRY")) {//首先判断首字母是否是为INSPIRY
                    String addres = deviceGet.getAddress();
                    if (addres.equals(address)) {
                        Intent intent1 = new Intent("android.intent.action.DEVICE_CONNECTION");
                        intent1.putExtra("Name", deviceGet.getName());
                        System.out.println("过去！！Name");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
                        Toast.makeText(HomeActivity.this, "找到设备等待连接。。。", Toast.LENGTH_SHORT).show();
                        //                        } else {
                        //                            Toast.makeText(HomeActivity.this, "请检查设备", Toast.LENGTH_SHORT).show();
                        //                        }

                    }
                }else {
                    Toast.makeText(HomeActivity.this, "设备没找到", Toast.LENGTH_SHORT).show();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                Log.d(TAG, "搜索完毕");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "搜索中");
            }

        }
    };
    //监听设备连接
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.i("comm蓝牙回调", String.valueOf(intent));
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                isConnect=true;
                // 连接成功
                Toast.makeText(HomeActivity.this, "连接成功！",
                        Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(HomeActivity.this, "连接失败！",
                        Toast.LENGTH_SHORT).show();
                // 断开连接
                tv_connectState.setText(getResources().getText(
                        R.string.disConnect));
                tv_connectState.setTextColor(getResources().getColor(
                        R.color.red));
                Toast.makeText(HomeActivity.this, "设备断开！", Toast.LENGTH_SHORT)
                        .show();
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    MyLog.i("蓝牙", "关闭");
                }
            }
        }
    };
    private void viewInit() {
        toplayout = (LinearLayout) findViewById(R.id.toplayout);
        toplayout.setOnTouchListener(onTouchListener);
        horizontalScrollView = (HorizontalScrollView) toplayout
                .findViewById(R.id.horizontalScrollView_excel);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnTouchListener(onTouchListener);
        editText = (EditText) findViewById(R.id.editText);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText1 = (EditText) findViewById(R.id.editText1);
        iv = (ImageView) findViewById(R.id.iv);
        iv.setOnClickListener(this);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        view_pop = LayoutInflater.from(HomeActivity.this).inflate(
                R.layout.pop_menu, null);
        tv_connectState=(TextView)findViewById(R.id.tv_connectState);
        location = SharedPreferencesUtil.getString(context, "toll", "无");
        number = SharedPreferencesUtil.getString(context, "gallery", "无");
        tv4 = (TextView) view_pop.findViewById(R.id.tv4);
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            if (isTouchItem == false)
                return;
            AnnotationDialog adialog=new AnnotationDialog(context,R.style.MyDialog,BaseData.get(position).getTime());
            adialog.show();
            Toast.makeText(context, "你点击的是" + position, Toast.LENGTH_SHORT).show();
        }

    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.toplayout:
                    horizontalScrollView.onTouchEvent(event);
                    return false;
                case R.id.listView:
                    horizontalScrollView.onTouchEvent(event);
                    return false;
            }
            return false;
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPopupwinow != null && mPopupwinow.isShowing()) {
            mPopupwinow.dismiss();
            mPopupwinow = null;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.line_toll:
                dialog = new SetDialog(context, tv1.getText().toString(), R.style.MyDialog, new SetDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String name) {
                        SharedPreferencesUtil.saveString(context, "toll", name);
                        toll.setText(name);
                    }
                });
                dialog.show();
                break;
            case R.id.line_gallery:
                dialog = new SetDialog(context, tv3.getText().toString(), R.style.MyDialog, new SetDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String name) {
                        SharedPreferencesUtil.saveString(context, "gallery", name);
                        gallery.setText(name);
                    }
                });
                dialog.show();
                break;
            case R.id.line_connection://连接设备
                share.intent.setClass(HomeActivity.this,
                        MainActivity.class);
                startActivity(share.intent);
                break;
            case R.id.iv:
                Popupwindow();
                break;
            case R.id.btn:
                if (isConnect) {
                    String m = editText.getText().toString();
                    String a = editText1.getText().toString();
                    SharedPreferencesUtil.saveString(context, "money", m);
                    SharedPreferencesUtil.saveString(context, "annotation", a);
                    if (TextUtils.isEmpty(m)) {
                        Toast.makeText(this, "输入金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btn.setText("等待付款");
                    btn.setBackgroundResource(R.drawable.shape_cornering);
                    btn.setEnabled(false);
                    //情开始扫码
                    sendData(START_SCAN);
                    break;
                }
                else {
                    Toast.makeText(this, "请先连接设备", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void sendData(final String startScan) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                byte_send = FileClass.hexToByte(startScan);
                if (share.socket == null && share.isSPP) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                        && share.isSPP) {
                    if (!share.socket.isConnected()) {
                        Log.d(TAG, "没有连接？");
                        return;
                    }
                }
                //发送数据
                int packets = share.write(byte_send);
                if (packets > 0) {
                    Log.d(TAG, "发送成功");
                }
            }
        }.start();
    }
    private void sendData1(final String startScan) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (isThread) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte_send = FileClass.hexToByte(startScan);
                    if (share.socket == null && share.isSPP) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                            && share.isSPP) {
                        if (!share.socket.isConnected()) {
                            Log.d(TAG, "没有连接？");
                            return;
                        }
                    }
                    //发送数据
                    int packets = share.write(byte_send);
                    if (packets > 0) {
                        Log.d(TAG, "发送成功");
                    }


                }
            }
        }.start();
    }

    /*
       保存数据库
    */
    private void dataInit() {
        if (isPay){
            money = SharedPreferencesUtil.getString(context, "money", "0");
            annotation = SharedPreferencesUtil.getString(context, "annotation", "无");
            Pay = SharedPreferencesUtil.getString(context, "Pay", "无");
            new Thread(new Runnable(){
                @Override
                public void run() {
                    //获取当前时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate);
                    model = new DataModel();
                    model.setNumber(number);
                    model.setLocation(location);
                    model.setMoney(money);
                    model.setState("完成");
                    model.setPay(Pay);
                    model.setOrder("1");
                    model.setTime(str);
                    model.setAnnotation(annotation);
                    //            BaseData.add(model);
                    model.save();//保存数据库,未成功，回来再测
                    Message message=new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("value", "0");
                    message.setData(bundle);
                    adapterHandler.sendMessage(message);
                }
            }).start();
        }else {
            Toast.makeText(this, "没有支付完成", Toast.LENGTH_SHORT).show();

        }
    }


    private void scanning(String s) {
        Log.d(TAG, "s="+s);
        SharedPreferencesUtil.saveString(context, "money", editText.getText().toString());
        money = SharedPreferencesUtil.getString(context, "money", "0");
        Pay = SharedPreferencesUtil.getString(context, "Pay", "无");
        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);

        OkGo.post(url).tag(this)
                .params("orderMoney",money)
                .params("payType",Pay)
                .params("sallerName",location)
                .params("chargeStation",location)
                .params("passagewayNo",number)
                .params("remarks",str)
                .params("payNumber","12313131")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        isThread=true;
                        Gson gson=new Gson();
                        DealData deal = gson.fromJson(s, DealData.class);
                        String res = deal.getResult();
                        //成功
                        if ("2".equals(res)) {
                            Toast.makeText(context, deal.getMsg(), Toast.LENGTH_SHORT).show();
                            sendData1(PAYMENT_SUCCESS);
                        }else if("0".equals(res)){
                            sendData1(PAYMENT_FAILURE);
                            Toast.makeText(context, deal.getMsg(), Toast.LENGTH_SHORT).show();
                        }else if("1".equals(res)){
                            sendData1(PAYMENT_FAILURE);
                            Toast.makeText(context, deal.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        //数据库操作
                        dataInit();
                        btn.setText("开始扫码");
                        btn.setEnabled(true);
                        btn.setBackgroundResource(R.drawable.shape_corner);

                    }
                });
    }


    private boolean stopThread=true;
    Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            if (stopThread) {
                if (share.isSPP) {
                    MyLog.i("连接", "1");
                    Message msg1=new Message();
                    ConnectingHandler.sendMessage(msg1);
                    boolean a = share.connect(share.device);
                    Log.d(TAG, "a:" + a);
                    if (a) {
                        isConnect=true;
                        MyLog.i("连接", "2");
                        // 连接成功
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("strBuf", "连接成功");
                        msg.setData(bundle);
                        ConnectSucceedHandler.sendMessage(msg);
                        sendData(CONNECTION_SUCCESS);//设备播放语音
                        readData();
                    } else {
                        MyLog.i("连接失败", "3");
                        Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
                        // 连接失败
                        goBackToDo();
                    }
                    // 停止搜索
                    share.stopSearch();
                } else {
                }
            }
        }
    };




    final Handler adapterHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String stringRe = msg.getData().getString("value");
            BaseData = DataSupport.findAll(DataModel.class);
            myAdapter.setHostBaseData(BaseData);
            listView.setAdapter(myAdapter);
        }
    };
    final Handler disConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String stringRe = msg.getData().getString("value");
            Toast.makeText(HomeActivity.this,
                    getResources().getText(R.string.disConnect),
                    Toast.LENGTH_SHORT).show();
            tv_connectState
                    .setText(getResources().getText(R.string.disConnect));
            tv_connectState.setTextColor(getResources().getColor(R.color.red));
        }
    };
    final Handler ConnectSucceedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String stringRe = msg.getData().getString("strBuf");
            tv_connectState.setText(stringRe);
            tv_connectState.setTextColor(getResources().getColor(R.color.text_color_yellow));
        }
    };
    final Handler ConnectingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_connectState.setText(getResources().getText(R.string.connecting));
            tv_connectState.setTextColor(getResources().getColor(R.color.text_color_yellow));
        }
    };
    //处理接收的数据
    final Handler ConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String str = msg.getData().getString("strBuf");
            if (!str.isEmpty()){
                isPay=true;
                String a = str.substring(0, 2);
                if (a.equals("13")){
                    Toast.makeText(context, "微信支付", Toast.LENGTH_SHORT).show();
                    SharedPreferencesUtil.saveString(context,"Pay","微信");
                }else if(a.equals("28")){
                    SharedPreferencesUtil.saveString(context,"Pay","支付宝");
                    Toast.makeText(context, "支付宝", Toast.LENGTH_SHORT).show();
                     scanning(str);
                }else {
                    Toast.makeText(context, "请选择微信或支付宝支付", Toast.LENGTH_SHORT).show();
                }
            }


        }
    };
    private void setUpBLECallBack() {
        // 通过BLE API的不同类型的回调方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mGattCallback = new BluetoothGattCallback() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt,
                                                    int status, int newState) {// 当连接状态发生改变
                    // 把连接中标志改为false
                    if (newState == BluetoothProfile.STATE_CONNECTED) {// 当蓝牙设备已经连接
                        MyLog.i("BLE连接", "成功");
                        gatt.discoverServices();
                        gatt.executeReliableWrite();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {// 当设备无法连接
                        Toast.makeText(HomeActivity.this,
                                getResources().getText(R.string.connectFail),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        MyLog.i("BLE连接", "... ...");
                    }
                }

                @Override
                // 发现新服务端
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    } else {
                        String str = "onServicesDiscovered received: " + status;
                        MyLog.i("搜索服务状态", str);
                        ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
                    }
                }

                @Override
                // 读特性
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic, int status) {
                    MyLog.i("发现特征", "");
                    if (status == BluetoothGatt.GATT_SUCCESS) {

                    }
                }

                @Override
                // 写特性
                public void onCharacteristicWrite(BluetoothGatt gatt,
                                                  BluetoothGattCharacteristic characteristic, int status) {
                    share.isBleSendFinish = true;
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        MyLog.i("发送回调", "成功");
                    } else {
                        MyLog.i("发送回调", "失败");
                    }
                }
            };
        }
    }

    private void goBackToDo() {
        isRunning = false;
        share.disConnect();
        finish();
    }

    @Override
    protected void onPause() {
        MyLog.i(TAG, "onPause");
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver1);//通知设备连接成功(自动登录用到，先)
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        MyLog.i(TAG, "onDestroy");
        super.onDestroy();
        //        if (model.isSaved()) {
        //           model.delete();//删除数据库
        //        }
        stopThread=false;//关闭线程
        isRunning = false;
        isThread=false;
        share.stopSearch();
        if (isReciver){
            unregisterReceiver(receiver);//连接设备
            unregisterReceiver(receiverDevice);//监听搜索设备
        }

        share.unregisterReceiver();
        //断开设备
        share.disConnect();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //
            //            Toast.makeText(HomeActivity.this, "设备断开！", Toast.LENGTH_SHORT)
            //                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }


}
