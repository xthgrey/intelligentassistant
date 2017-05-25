package com.xth.intelligentassistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.xth.intelligentassistant.internetapi.GaodeLocation;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, BottomNavigationBar.OnTabSelectedListener {

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

    private GaodeLocation gaodeLocation;//高德地图定位

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constant.WEATHER_CALL_BACK:
                    weatherCity.setText(gaodeLocation.getHttpUtiil().getWeather().getCity());
                    try {
                        InputStream in = getResources().getAssets().open(Constant.WEATHER + "/" + Constant.WEATHER + gaodeLocation.getHttpUtiil().getWeather().getCondcode()+ Constant.PNG);
                        Bitmap bmp= BitmapFactory.decodeStream(in);
                        weatherCondCode.setImageBitmap(bmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    weatherTmp.setText(gaodeLocation.getHttpUtiil().getWeather().getTmp());
                    weatherCounty.setText(gaodeLocation.getHttpUtiil().getWeather().getCounty());
                    weatherCondTxt.setText(gaodeLocation.getHttpUtiil().getWeather().getCondText());
                    weatherCondHum.setText(gaodeLocation.getHttpUtiil().getWeather().getHum());
                    break;
                case Constant.WEATHER_EMPTY:
                case Constant.WEATHER_REQUEST_ERROR:
                    Toast.makeText(getApplicationContext(),Constant.WEATHER_REQUEST_ERROR_TXT,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyForPermission();
        initUI();
    }

    //运行时权限申请授权处理
    private void applyForPermission() {
        //GPS权限不理
        //4
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
        weatherCondHum = (TextView)findViewById(R.id.weather_cond_hum);

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
                .addItem(new BottomNavigationItem(R.drawable.main_home_location, "房间定位"))
                .addItem(new BottomNavigationItem(R.drawable.main_home_device, "设备"))
                .initialise();

        //设置 navigationView Item 监听
        navigationView.setNavigationItemSelectedListener(this);
        //设置悬浮按钮监听
        voiceAssistant.setOnClickListener(this);
        //底部导航设置监听
        bottomNavigationBar.setTabSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_browser:

                break;
            case R.id.main_menu_call:

                break;
            case R.id.main_menu_friends:

                break;
            case R.id.main_menu_location:
                gaodeLocation = new GaodeLocation(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        while(true){
                            int callBackFlag = gaodeLocation.getHttpUtiil().getWeatherCallBackFlag();
                            switch (callBackFlag){
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
                            if (callBackFlag != 0){
                                break;
                            }
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
        return true;
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
        LogUtil.d("onTabSelected" + position);
    }

    @Override
    public void onTabUnselected(int position) {
        LogUtil.d("onTabUnselected" + position);
    }

    @Override
    public void onTabReselected(int position) {
        LogUtil.d("onTabReselected" + position);
    }
}
