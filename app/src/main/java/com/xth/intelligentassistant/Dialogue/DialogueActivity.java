package com.xth.intelligentassistant.Dialogue;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by XTH on 2017/5/12.
 */

public class DialogueActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, RecognitionListener, SpeechSynthesizerListener {

    private String voiceSelect;//0:普通女生，1：普通男生，2：特别男生，3：情感男生<度逍遥>，4：情感儿童声<度丫丫>
    private Boolean text_voice_flag;//false:Text显示语音图标,true:voice显示文字图标

    private Toolbar toolBar;//标题栏
    private Button textVoiceChooseButton;//文字语音选择按钮
    private Button voiceButton;//语音按钮
    private Button sendButton;//发送按钮
    private EditText textEdit;//文字编辑
    private ImageView imageWave;//对话框中的波形界面
    private RecyclerView recyclerView;
    private MsgAdapter msgAdapter;
    private List<MSG> msgList = new ArrayList<>();
    private MSG receivedMsg;
    private MSG sendMsg;
    //语音识别对话框
    private Dialog dialog;
    //语音识别器
    private SpeechRecognizer speechRecognizer;
    // 语音合成客户端
    private SpeechSynthesizer mSpeechSynthesizer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        getMenuInflater().inflate(R.menu.dialogue_layout_toolbar, menu);//动态创建Toolbar中的菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        switch (item.getItemId()) {
            case R.id.toolbar_general_girl:
                voiceSelect = "0";
                SharedPreferences.Editor editor0 = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor0.putString("voiceSelect", voiceSelect);
                editor0.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_general_boy:
                voiceSelect = "1";
                SharedPreferences.Editor editor1 = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor1.putString("voiceSelect", voiceSelect);
                editor1.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_special_boy:
                voiceSelect = "2";
                SharedPreferences.Editor editor2 = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor2.putString("voiceSelect", voiceSelect);
                editor2.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_emotion_boy:
                voiceSelect = "3";
                SharedPreferences.Editor editor3 = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor3.putString("voiceSelect", voiceSelect);
                editor3.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_emotion_child:
                voiceSelect = "4";
                SharedPreferences.Editor editor4 = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor4.putString("voiceSelect", voiceSelect);
                editor4.apply();
                voiceSelectDeal();
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
        startTTS();
        dealData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        voiceSelect = pref.getString("voiceSelect", "0");
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
        dialog = new Dialog(this, R.style.dialog);//自定义dialog
        // 创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
        // 注册监听器
        speechRecognizer.setRecognitionListener(this);

        //动态加载view到dialog
        View view = LayoutInflater.from(this).inflate(R.layout.voice_image_layout, null);
        dialog.setContentView(view);
        imageWave = (ImageView) view.findViewById(R.id.image_wave);
        // recyclerView 线性布局，将列表加入适配，将适配器载入 recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        msgAdapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(msgAdapter);

        setSupportActionBar(toolBar);//将toolBar作为ActionBar
        //添加导航栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
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
                    sendMsg = new MSG(content, MSG.TYPE_SENT);
                    msgList.add(sendMsg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
                    recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
                    textEdit.setText("");
                    //返回对话结果
                    receivedMsg = new MSG(content, MSG.TYPE_RECEIVED);
                    msgList.add(receivedMsg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
                    recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
                    mSpeechSynthesizer.speak(content);
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
                    cancel();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LogUtil.d("voice_button按下");
                    dialog.show();
                    startASR();
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

    /**
     * Activity生命周期
     */
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
        speechRecognizer.destroy();
        super.onDestroy();
    }

    /**
     * 语音识别部分
     */
    private void startASR() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String args = sp.getString("args", "");
        LogUtil.d(this.getLocalClassName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " 1: " + args);
        if (null != args) {
            intent.putExtra("args", args);
        }
        speechRecognizer.startListening(intent);
    }

    private void bindParams(Intent intent) {
        // 设置识别参数
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 设置识别参数");
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        // 提示音
        if (pref.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        // 音频源
        if (pref.contains(Constant.EXTRA_INFILE)) {
            String tmp = pref.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        // 保存过程中产生的音频文件
        if (pref.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        // 离线语音识别路径
        if (pref.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        // 采样率
        if (pref.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = pref.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        // 语种
        if (pref.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = pref.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        // 语义解析设置
        if (pref.contains(Constant.EXTRA_NLU)) {
            String tmp = pref.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }
        // 语义活动检测
        // search 搜索（短）
        // input 输入（长）
        if (pref.contains(Constant.EXTRA_VAD)) {
            String tmp = pref.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        // 垂直领域
        String prop = null;
        if (pref.contains(Constant.EXTRA_PROP)) {
            String tmp = pref.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }
        intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
        if (null != prop) {
            int propInt = Integer.parseInt(prop);
            if (propInt == 10060) {
                intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
            } else if (propInt == 20000) {
                intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
            }
        }
        intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 设置识别参数结束");
    }

    private String buildTestSlotData() {
        LogUtil.d(this.getLocalClassName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + ": ");
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    private void cancel() {
        LogUtil.d(this.getLocalClassName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + ": ");
        speechRecognizer.cancel();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 准备就绪");
        // 准备就绪
    }

    @Override
    public void onBeginningOfSpeech() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 开始说话处理");
        // 开始说话处理
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 音量变化处理 " + rmsdB);
        if (rmsdB <= 1000) {
            imageWave.setImageResource(R.drawable.voice_image_wave1);
        } else if (rmsdB > 1000 && rmsdB <= 2000) {
            imageWave.setImageResource(R.drawable.voice_image_wave2);
        } else if (rmsdB > 2000 && rmsdB <= 3000) {
            imageWave.setImageResource(R.drawable.voice_image_wave3);
        } else if (rmsdB > 3000 && rmsdB <= 4000) {
            imageWave.setImageResource(R.drawable.voice_image_wave4);
        } else if (rmsdB > 4000 && rmsdB <= 5000) {
            imageWave.setImageResource(R.drawable.voice_image_wave5);
        } else if (rmsdB > 5000 && rmsdB <= 6000) {
            imageWave.setImageResource(R.drawable.voice_image_wave6);
        } else if (rmsdB > 6000) {
            imageWave.setImageResource(R.drawable.voice_image_wave7);
        }
        // 音量变化处理
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 录音数据传出处理");
        // 录音数据传出处理
    }

    @Override
    public void onEndOfSpeech() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 说话结束处理");
        // 说话结束处理
    }

    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                Toast.makeText(this, "音频问题", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                Toast.makeText(this, "没有语音输入", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                Toast.makeText(this, "其它客户端错误", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                Toast.makeText(this, "网络问题", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                Toast.makeText(this, "没有匹配的识别结果", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                Toast.makeText(this, "引擎忙", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                Toast.makeText(this, "服务端错误", Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                Toast.makeText(this, "连接超时", Toast.LENGTH_SHORT).show();
                break;
        }
        sb.append(":" + error);
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 出错处理 " + sb.toString());
        // 出错处理
    }

    @Override
    public void onResults(Bundle results) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 最终结果处理");
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String s = Arrays.toString(nbest.toArray(new String[nbest.size()])).replaceAll("\\[|\\]", "");
        //用对话方式显示语音结果
        sendMsg = new MSG(s, MSG.TYPE_SENT);
        msgList.add(sendMsg);
        msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
        recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
        // 最终结果处理
        //返回语音识别的结果，语音合成说出
        receivedMsg = new MSG(s, MSG.TYPE_RECEIVED);
        msgList.add(receivedMsg);
        msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
        recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
        //需要合成的文本text的长度不能超过1024个GBK字节。
        this.mSpeechSynthesizer.speak(s);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 临时结果处理");
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : " + "~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
        }
        // 临时结果处理
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 处理事件回调");
        // 处理事件回调
    }

    /**
     * 语音合成部分
     */

    // 初始化语音合成客户端并启动
    private void startTTS() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        // 获取语音合成对象实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        // 设置context
        mSpeechSynthesizer.setContext(this);
        // 设置语音合成状态监听器
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 设置在线语音合成授权，需要填入从百度语音官网申请的api_key和secret_key
        mSpeechSynthesizer.setApiKey("VsCpOUtv6vquaw5ZMKhLLZAs", "15004d92543351f67bf6873f9ec907ea");
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId("9628655");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);

        voiceSelectDeal();
    }
    private void voiceSelectDeal(){
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, voiceSelect);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        switch (voiceSelect){
            case "0":
                receivedMsg = new MSG(Constant.GENERALGIRL, MSG.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.GENERALGIRL);
                break;
            case "1":
                receivedMsg = new MSG(Constant.GENERALBOY, MSG.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.GENERALBOY);
                break;
            case "2":
                receivedMsg = new MSG(Constant.SPECIALBOY, MSG.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.SPECIALBOY);
                break;
            case "3":
                receivedMsg = new MSG(Constant.EMOTIONBOY, MSG.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.EMOTIONBOY);
                break;
            case "4":
                receivedMsg = new MSG(Constant.EMOTIONCHILD, MSG.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.EMOTIONCHILD);
                break;
            default:
                break;
        }
        msgList.add(receivedMsg);
        msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
        recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行

    }

    @Override
    public void onSynthesizeStart(String s) {
        // 监听到合成开始，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到合成开始" + s);
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {
        // 监听到有合成数据到达，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到有合成数据到达" + s + "---" + i);
    }

    @Override
    public void onSynthesizeFinish(String s) {
        // 监听到合成结束，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到合成结束" + s);
    }

    @Override
    public void onSpeechStart(String s) {
        // 监听到合成并播放开始，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到合成并播放开始" + s);
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        // 监听到播放进度有变化，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到播放进度有变化" + s + "---" + i);
    }

    @Override
    public void onSpeechFinish(String s) {
        // 监听到播放结束，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到播放结束" + s);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        // 监听到出错，在此添加相关操作
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 监听到出错" + s + "---" + speechError);
    }
}
