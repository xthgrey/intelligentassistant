package com.xth.intelligentassistant;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xth.intelligentassistant.Dialogue.DialogueActivity;
import com.xth.intelligentassistant.util.LogUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolBar;//标题栏
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton voiceAssistant;

    private Uri uri;
    private Intent intent;

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


    }

    /*用户选择运行时权限调用 onRequestPermissionsResult*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            switch (requestCode) {
                case 2:
                    Toast.makeText(this, "无法通话", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this, "无法读取联系人", Toast.LENGTH_SHORT).show();
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
                intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, "");//后续可以通过对话搜索相应的内容
                startActivity(intent);
                break;
            case R.id.main_menu_call:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 2);
                }
                uri = Uri.parse("tel:");
                intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;
            case R.id.main_menu_friends:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 3);
                }
                readContacts();
                break;
            case R.id.main_menu_location:
                drawerLayout.closeDrawers();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 获取联系人信息
     */
    private void readContacts() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    LogUtil.d(displayName + number);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
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
}
