package com.xth.intelligentassistant;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.xth.intelligentassistant.db.Device;
import com.xth.intelligentassistant.db.OperateDB;
import com.xth.intelligentassistant.db.Sence;
import com.xth.intelligentassistant.internetapi.GaodeLocation;
import com.xth.intelligentassistant.main.ExpandableListFragment;
import com.xth.intelligentassistant.main.SwipeMenuListFragment;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, BottomNavigationBar.OnTabSelectedListener {

    private int bottomNavigationPosition;

    private ImageView bingPic;
    private Toolbar toolBar;//标题栏
    private DrawerLayout drawerLayout;//抽屉布局
    private NavigationView navigationView;//抽屉中的导航布局
    private FloatingActionButton voiceAssistant;//悬浮球
    private BottomNavigationBar bottomNavigationBar;//底部导航
    private TextView weatherCity;
    private ImageView weatherCondCode;
    private TextView weatherTmp;
    private TextView weatherCounty;
    private TextView weatherCondTxt;
    private TextView weatherCondHum;

    private SwipeMenuListFragment swipeMenuListFragment;
    private ExpandableListFragment expandableListFragment;

    private GaodeLocation gaodeLocation;//高德地图定位

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.WEATHER_CALL_BACK:
                    weatherCity.setText(gaodeLocation.getHttpUtil().getWeather().getCity());
                    try {
                        InputStream in = getResources().getAssets().open(Constant.WEATHER + "/" + Constant.WEATHER + gaodeLocation.getHttpUtil().getWeather().getCondcode() + Constant.PNG);
                        Bitmap bmp = BitmapFactory.decodeStream(in);
                        weatherCondCode.setImageBitmap(bmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    weatherTmp.setText(gaodeLocation.getHttpUtil().getWeather().getTmp());
                    weatherCounty.setText(gaodeLocation.getHttpUtil().getWeather().getCounty());
                    weatherCondTxt.setText(gaodeLocation.getHttpUtil().getWeather().getCondText());
                    weatherCondHum.setText(gaodeLocation.getHttpUtil().getWeather().getHum());
                    break;
                case Constant.WEATHER_EMPTY:
                case Constant.WEATHER_REQUEST_ERROR:
                    Toast.makeText(getApplicationContext(), Constant.WEATHER_REQUEST_ERROR_TXT, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tool_bar_menu, menu);//动态创建Toolbar中的菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                switch (bottomNavigationPosition) {
                    case 0:
                        editName(Constant.SENCE_NAME);
                        break;
                    case 1:
                        editName(Constant.DEVICE_NAME);
                        break;
                }

                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editName(final String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.alert_dialog_layout, null);
        final EditText alertDialogEdit = (EditText) view.findViewById(R.id.alert_dialog_edit);
        builder.setTitle(title);
        builder.setView(view);
        builder.setPositiveButton(Constant.CONFIRM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = alertDialogEdit.getText().toString();
                s = s.replaceAll("\\s", "");
                if (!"".equals(s)) {
                    switch (title) {
                        case Constant.SENCE_NAME:
                            if (OperateDB.isHaveInDB(s)) {
                                Toast.makeText(MainActivity.this, Constant.ERROR_SENCE_NAME, Toast.LENGTH_SHORT).show();
                            } else {
                                swipeMenuListFragment.swipeViewAddItem(Constant.SWIPE_SENCE_KEY, alertDialogEdit.getText().toString());
                            }
                            break;
                        case Constant.DEVICE_NAME:
                            if (OperateDB.isHaveInDB((String) expandableListFragment.getGroupList().get(expandableListFragment.getGroupPosition()).get(Constant.SWIPE_SENCE_KEY), s)) {
                                Toast.makeText(MainActivity.this, Constant.ERROR_DEVICE_NAME, Toast.LENGTH_SHORT).show();
                            } else {
                                expandableListFragment.expandListViewAddItem(Constant.SWIPE_DIVICE_KEY, alertDialogEdit.getText().toString());
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(MainActivity.this, Constant.ERROR_EMPTY_NAME, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(Constant.CANCEL, null);

        AlertDialog tempDialog = builder.create();
        tempDialog.setView(view, 0, 0, 0, 0);
        /** 3.自动弹出软键盘 **/
        tempDialog.setOnShowListener(new AlertDialog.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(alertDialogEdit, InputMethodManager.SHOW_IMPLICIT);

            }
        });
        tempDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyForPermission();
        initUI();
        initData();
    }

    private void initData() {
        List<Sence> senceList = DataSupport.findAll(Sence.class);
        List<Device> deviceList = DataSupport.findAll(Device.class);
        for (Sence sence : senceList) {
            LogUtil.d("initData Sence: " + sence.getSenceName());
        }
        for (Device device : deviceList) {
            LogUtil.d("initData DeviceSence：" + device.getSenceName() + "::" + device.getDeviceName());
        }
        ZXingLibrary.initDisplayOpinion(this);
        bottomNavigationPosition = 0;
        locationAndWeatherUpdate();
    }

    //运行时权限申请授权处理
    private void applyForPermission() {
        //GPS权限不理
        //5
    }

    /*用户选择运行时权限调用 onRequestPermissionsResult*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            switch (requestCode) {
                case 2:
                    Toast.makeText(this, Constant.CALL_PHONE, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this, Constant.READ_CONTACTS, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(this, Constant.ACCESS_COARSE_LOCATION, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private void initUI() {
        bingPic = (ImageView)findViewById(R.id.main_bing_pic);
        toolBar = (Toolbar) findViewById(R.id.main_layout_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.main_navigate_view);
        voiceAssistant = (FloatingActionButton) findViewById(R.id.main_voice_assistant);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.main_bottom_navigation_bar);

        weatherCity = (TextView) findViewById(R.id.weather_city);
        weatherCondCode = (ImageView) findViewById(R.id.weather_cond_code);
        weatherTmp = (TextView) findViewById(R.id.weather_tmp);
        weatherCounty = (TextView) findViewById(R.id.weather_county);
        weatherCondTxt = (TextView) findViewById(R.id.weather_cond_txt);
        weatherCondHum = (TextView) findViewById(R.id.weather_cond_hum);

        swipeMenuListFragment = new SwipeMenuListFragment();
        expandableListFragment = new ExpandableListFragment();

        Glide.with(this).load(Constant.BING_PIC).into(bingPic);//加载必应图片

        setSupportActionBar(toolBar);//将toolBar作为ActionBar
        //添加toolbar导航栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.main_navigate);
        }
        //设置底部导航子项
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.main_home_location, Constant.SENCE_NAME))
                .addItem(new BottomNavigationItem(R.drawable.main_home_device, Constant.DEVICE_NAME))
                .initialise();
        //主界面list view显示布局
        replaceFragment(swipeMenuListFragment);

        //设置 navigationView Item 监听
        navigationView.setNavigationItemSelectedListener(this);
        //设置悬浮按钮监听
        voiceAssistant.setOnClickListener(this);
        //底部导航设置监听
        bottomNavigationBar.setTabSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.main_menu_qrcode:
                Intent scanIntent = new Intent(this, QrcodeActivity.class);
                startActivityForResult(scanIntent, Constant.REQUEST_CODE);
                break;
            case R.id.main_menu_picture:
                Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pictureIntent.setType("image/*");
                startActivityForResult(pictureIntent, Constant.REQUEST_IMAGE);
                break;
            case R.id.main_menu_friends:
                break;
            case R.id.main_menu_location:
                locationAndWeatherUpdate();
                break;
            default:
                break;
        }
        return true;
    }
    private void locationAndWeatherUpdate(){
        gaodeLocation = new GaodeLocation(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                while (true) {
                    int callBackFlag = gaodeLocation.getHttpUtil().getWeatherCallBackFlag();
                    switch (callBackFlag) {
                        case Constant.WEATHER_CALL_BACK:
                            message.what = Constant.WEATHER_CALL_BACK;
                            handler.sendMessage(message);
                            break;
                        case Constant.WEATHER_EMPTY:
                            message.what = Constant.WEATHER_EMPTY;
                            handler.sendMessage(message);
                            break;
                        case Constant.WEATHER_REQUEST_ERROR:
                            message.what = Constant.WEATHER_EMPTY;
                            handler.sendMessage(message);
                            break;
                        default:
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    if (callBackFlag != 0) {
                        break;
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //处理扫描结果（在界面上显示）
            case Constant.REQUEST_CODE:
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        Toast.makeText(this, Constant.ANALYSIS + result, Toast.LENGTH_LONG).show();
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(MainActivity.this, Constant.ERROR_ANALYZE, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Constant.REQUEST_IMAGE:
                if (data != null) {
                    Uri uri = data.getData();
                    ContentResolver cr = getContentResolver();
                    try {
                        Bitmap mBitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片

                        // 好像是android多媒体数据库的封装接口，具体的看Android文档
                        CursorLoader cursorLoader = new CursorLoader(this, uri, null, null, null, null);
                        Cursor cursor = cursorLoader.loadInBackground();
                        // 按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        // 最后根据索引值获取图片路径
                        String path = cursor.getString(column_index);
                        LogUtil.d(path);
                        CodeUtils.analyzeBitmap(path, new CodeUtils.AnalyzeCallback() {
                            @Override
                            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                                Toast.makeText(MainActivity.this, Constant.ANALYSIS + result, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onAnalyzeFailed() {
                                Toast.makeText(MainActivity.this, Constant.ERROR_ANALYZE, Toast.LENGTH_LONG).show();
                            }
                        });

                        if (mBitmap != null) {
                            mBitmap.recycle();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_voice_assistant:
                Intent intent = new Intent(MainActivity.this, DialogueActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onTabSelected(int position) {
        bottomNavigationPosition = position;

        switch (position) {
            case 0:
                replaceFragment(swipeMenuListFragment);
                break;
            case 1:
                expandableListFragment.setGroupList(swipeMenuListFragment.getSwipeMenuItemList());//将场景列表中的内容传到设备列表里
                replaceFragment(expandableListFragment);
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_list_view_layout, fragment);
        transaction.commit();
    }


}
