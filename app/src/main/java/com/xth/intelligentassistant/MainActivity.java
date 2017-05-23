package com.xth.intelligentassistant;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.widget.Toast;

import com.xth.intelligentassistant.util.CallApp;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.internetapi.GaodeLocation;
import com.xth.intelligentassistant.util.LogUtil;
import com.xth.intelligentassistant.util.MySharePreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolBar;//标题栏
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton voiceAssistant;

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

        setSupportActionBar(toolBar);//将toolBar作为ActionBar
        //添加toolbar导航栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.main_navigate);
        }
        //设置 navigationView Item 监听
        navigationView.setNavigationItemSelectedListener(this);
        //设置悬浮按钮监听
        voiceAssistant.setOnClickListener(this);
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
                new GaodeLocation(this);
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
}
