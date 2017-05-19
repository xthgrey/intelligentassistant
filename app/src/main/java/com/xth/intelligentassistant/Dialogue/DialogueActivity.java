package com.xth.intelligentassistant.Dialogue;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.xth.intelligentassistant.MainActivity;
import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private List<Msg> msgList = new ArrayList<>();
    private Msg receivedMsg;
    private Msg sendMsg;
    private SharedPreferences.Editor editor;
    //语音识别对话框
    private Dialog dialog;
    //语音识别器
    private SpeechRecognizer speechRecognizer;
    // 语音合成客户端
    private SpeechSynthesizer mSpeechSynthesizer;

    private String mSampleDirPath;


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
                voiceSelect = Constant.GENERALGIRL_VALUE;
                editor = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE).edit();
                editor.putString(Constant.VOICE_SELECT, voiceSelect);
                editor.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_general_boy:
                voiceSelect = Constant.GENERALBOY_VALUE;
                editor = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE).edit();
                editor.putString(Constant.VOICE_SELECT, voiceSelect);
                editor.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_special_boy:
                voiceSelect = Constant.SPECIALBOY_VALUE;
                editor = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE).edit();
                editor.putString(Constant.VOICE_SELECT, voiceSelect);
                editor.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_emotion_boy:
                voiceSelect = Constant.EMOTIONBOY_VALUE;
                editor = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE).edit();
                editor.putString(Constant.VOICE_SELECT, voiceSelect);
                editor.apply();
                voiceSelectDeal();
                break;
            case R.id.toolbar_emotion_child:
                voiceSelect = Constant.EMOTIONCHILD_VALUE;
                editor = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE).edit();
                editor.putString(Constant.VOICE_SELECT, voiceSelect);
                editor.apply();
                voiceSelectDeal();
                break;
            case android.R.id.home:
                Intent intent = new Intent(DialogueActivity.this, MainActivity.class);
                startActivity(intent);
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

        initialEnv();
        initData();//数据初始化
        initUI();
        startTTS();
        dealData();
    }

    /*用户选择运行时权限调用 onRequestPermissionsResult*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            switch (requestCode) {
                case 1:
                    Toast.makeText(this, Constant.RECORD_AUDIO, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(this, Constant.CALL_PHONE, Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this, Constant.READ_CONTACTS, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        SharedPreferences pref = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE);
        voiceSelect = pref.getString(Constant.VOICE_SELECT, Constant.GENERALGIRL_VALUE);
        text_voice_flag = pref.getBoolean(Constant.TEXT_VOICE_FLAG, false);
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
        View view = LayoutInflater.from(this).inflate(R.layout.dialogue_layout_dialog, null);
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
        //设置 textVoiceButton sendButton 监听
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
                editor = getSharedPreferences(Constant.PREF_FILE, MODE_PRIVATE).edit();
                editor.putBoolean(Constant.TEXT_VOICE_FLAG, text_voice_flag);
                editor.apply();//保存提交
                textVoiceChooseButtonDeal();
                break;
            case R.id.send_button:
                //发送按钮，文字编辑时发送按钮显示
                String content = textEdit.getText().toString();
                if (!"".equals(content)) {
                    sendMsg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(sendMsg);
                    msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
                    recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
                    textEdit.setText("");
                    //返回对话结果
                    receivedMsg = new Msg(content, Msg.TYPE_RECEIVED);
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
                    stop();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (ContextCompat.checkSelfPermission(DialogueActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DialogueActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    }
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
    protected void onDestroy() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 销毁");
        speechRecognizer.destroy();
        this.mSpeechSynthesizer.release();
        super.onDestroy();
    }

    /**
     * 语音识别部分
     */
    private void startASR() {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        Intent intent = new Intent();
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        String args = sp.getString("args", "");
        LogUtil.d(this.getLocalClassName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " 1: " + args);
        if (null != args) {
            intent.putExtra("args", args);
        }
        speechRecognizer.startListening(intent);
    }

    private void stop() {
        LogUtil.d(this.getLocalClassName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + ": ");
        speechRecognizer.stopListening();
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
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                Toast.makeText(this, Constant.ERROR_AUDIO, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Toast.makeText(this, Constant.ERROR_SPEECH_TIMEOUT, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                Toast.makeText(this, Constant.ERROR_CLIENT, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Toast.makeText(this, Constant.ERROR_INSUFFICIENT_PERMISSIONS, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                Toast.makeText(this,Constant.ERROR_NETWORK, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                Toast.makeText(this, Constant.ERROR_NO_MATCH, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                Toast.makeText(this, Constant.ERROR_RECOGNIZER_BUSY, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_SERVER:
                Toast.makeText(this, Constant.ERROR_SERVER, Toast.LENGTH_SHORT).show();
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Toast.makeText(this, Constant.ERROR_NETWORK_TIMEOUT, Toast.LENGTH_SHORT).show();
                break;
        }
        // 出错处理
    }

    @Override
    public void onResults(Bundle results) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 最终结果处理");
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String s = Arrays.toString(nbest.toArray(new String[nbest.size()])).replaceAll("\\[|\\]", "");
        //用对话方式显示语音结果
        sendMsg = new Msg(s, Msg.TYPE_SENT);
        msgList.add(sendMsg);
        msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
        recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
        // 最终结果处理
        //返回语音识别的结果，语音合成说出
        receivedMsg = new Msg(s, Msg.TYPE_RECEIVED);
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
    //初始化配置文件
    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + Constant.SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);

        copyFromAssetsToSdcard(false, Constant.SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + Constant.SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, Constant.SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + Constant.SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, Constant.TEXT_MODEL_NAME, mSampleDirPath + "/" + Constant.TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将资源语音文件复制到手机SD卡中
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
        mSpeechSynthesizer.setApiKey(Constant.API_KEY, Constant.SECRET_KEY);
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId(Constant.APP_ID);
        // 设置语音合成文本模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + Constant.TEXT_MODEL_NAME);
        // 设置语音合成声音模型文件
        if (voiceSelect == Constant.GENERALGIRL_VALUE || voiceSelect == Constant.EMOTIONCHILD_VALUE) {
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                    + Constant.SPEECH_FEMALE_MODEL_NAME);
        } else {
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                    + Constant.SPEECH_MALE_MODEL_NAME);
        }

        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        //第一次使用离在线授权文件下载
        this.mSpeechSynthesizer.auth(TtsMode.MIX);
        // 判断授权信息是否正确，如果正确则初始化语音合成器并开始语音合成，如果失败则做错误处理
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        voiceSelectDeal();
    }

    private void voiceSelectDeal() {
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, voiceSelect);
        switch (voiceSelect) {
            case Constant.GENERALGIRL_VALUE:
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                        + Constant.SPEECH_FEMALE_MODEL_NAME);
                receivedMsg = new Msg(Constant.GENERALGIRL, Msg.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.GENERALGIRL);
                break;
            case Constant.GENERALBOY_VALUE:
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                        + Constant.SPEECH_MALE_MODEL_NAME);
                receivedMsg = new Msg(Constant.GENERALBOY, Msg.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.GENERALBOY);
                break;
            case Constant.SPECIALBOY_VALUE:
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                        + Constant.SPEECH_MALE_MODEL_NAME);
                receivedMsg = new Msg(Constant.SPECIALBOY, Msg.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.SPECIALBOY);
                break;
            case Constant.EMOTIONBOY_VALUE:
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                        + Constant.SPEECH_MALE_MODEL_NAME);
                receivedMsg = new Msg(Constant.EMOTIONBOY, Msg.TYPE_RECEIVED);
                mSpeechSynthesizer.speak(Constant.EMOTIONBOY);
                break;
            case Constant.EMOTIONCHILD_VALUE:
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                        + Constant.SPEECH_FEMALE_MODEL_NAME);
                receivedMsg = new Msg(Constant.EMOTIONCHILD, Msg.TYPE_RECEIVED);
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