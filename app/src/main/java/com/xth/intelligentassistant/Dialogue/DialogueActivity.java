package com.xth.intelligentassistant.Dialogue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XTH on 2017/5/12.
 */

public class DialogueActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private Boolean voice_sex_flag;//false:女的，true：男的
    private Boolean text_voice_flag;//false:Text显示语音图标,true:voice显示文字图标

    private Toolbar toolBar;//标题栏
    private Button textVoiceChooseButton;//文字语音选择按钮
    private Button voiceButton;//语音按钮
    private Button sendButton;//发送按钮
    private EditText textEdit;//文字编辑
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private List<MSG> msgList = new ArrayList<>();

    private Dialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        getMenuInflater().inflate(R.menu.dialogue_layout_toolbar, menu);//动态创建Toolbar中的菜单

        //判断ToolBar中的“声音选择”项，判断男女
        MenuItem menuItem = menu.findItem(R.id.voice_sex);
        if (voice_sex_flag) {
            menuItem.setIcon(R.drawable.dialogue_layout_boy);
        } else {
            menuItem.setIcon(R.drawable.dialogue_layout_girl);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        switch (item.getItemId()) {
            case R.id.voice_sex:
                /**
                 * 声音选择逻辑，后期可供用户选择不同的声音
                 */
                voice_sex_flag = !voice_sex_flag;
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("voice_sex_flag", voice_sex_flag);
                editor.apply();
                if (voice_sex_flag) {
                    item.setIcon(R.drawable.dialogue_layout_boy);
                } else {
                    item.setIcon(R.drawable.dialogue_layout_girl);
                }
                break;
            case android.R.id.home:
                //返回MainActivity
                break;
        }
        return true;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 创建");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogue_layout);

        initData();//数据初始化
        initUI();
        dealData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        voice_sex_flag = pref.getBoolean("voice_sex_flag", false);
        text_voice_flag = pref.getBoolean("text_voice_flag", false);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        toolBar = (Toolbar) findViewById(R.id.dialogue_layout_toolbar);
        textVoiceChooseButton = (Button) findViewById(R.id.text_voice_choose_button);
        voiceButton = (Button) findViewById(R.id.voice_button);
        sendButton = (Button) findViewById(R.id.send_button);
        textEdit = (EditText) findViewById(R.id.text_edit);
        recyclerView = (RecyclerView) findViewById(R.id.dialogue_layout_recyclerview);
        dialog = new Dialog(this, R.style.dialog);

        View view = LayoutInflater.from(this).inflate(R.layout.voice_image_layout, null);
        dialog.setContentView(view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        msgAdapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(msgAdapter);

        setSupportActionBar(toolBar);//将toolBar作为ActionBar
        //添加导航栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.dialogue_layout_back);
        }
        //设置 textVoiceButton 监听
        textVoiceChooseButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        //设置 voiceButton 触摸监听
        voiceButton.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_voice_choose_button:
                //文字语音选择
                text_voice_flag = !text_voice_flag;
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("text_voice_flag", text_voice_flag);
                editor.apply();//保存提交
                textVoiceChooseButtonDeal();
                break;
            case R.id.send_button:
                //发送按钮，文字编辑时发送按钮显示
                String content = textEdit.getText().toString();
                if (!"".equals(content)) {
                    MSG msg = new MSG(content, MSG.TYPE_SENT);
                    msgList.add(msg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
                    recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
                    textEdit.setText("");
                }
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.voice_button:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    LogUtil.d("voice_button松开");
                    dialog.dismiss();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LogUtil.d("voice_button按下");
                    dialog.show();
                }
                break;
        }
        return true;
    }

    /**
     * 数据处理
     */
    private void dealData() {
        textVoiceChooseButtonDeal();
    }

    /**
     * 文字语音选择按钮点击处理
     */
    private void textVoiceChooseButtonDeal() {
        if (text_voice_flag) {//显示文字图片，显示语音按钮，语音按钮显示，编辑框消失，发送按钮消失
            textVoiceChooseButton.setBackgroundResource(R.drawable.dialogue_layout_textbutton);
            voiceButton.setVisibility(View.VISIBLE);
            textEdit.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        } else {//显示语音图片,语音按钮消失，编辑框显示,发送按钮显示
            textVoiceChooseButton.setBackgroundResource(R.drawable.dialogue_layout_voicebutton);
            voiceButton.setVisibility(View.GONE);
            textEdit.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 开始");
        super.onStart();
    }

    @Override
    protected void onResume() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 恢复");
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 暂停");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 停止");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 重新开始");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 销毁");
        super.onDestroy();
    }
}
