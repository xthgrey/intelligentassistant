package com.xth.intelligentassistant;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
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
import com.xth.intelligentassistant.dialogue.Msg;
import com.xth.intelligentassistant.dialogue.MsgAdapter;
import com.xth.intelligentassistant.util.AppInfo;
import com.xth.intelligentassistant.util.CallApp;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;
import com.xth.intelligentassistant.util.MySharePreferences;

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

    private Toolbar toolBar;//标题栏
    private Button textVoiceChooseButton;//文字语音选择按钮
    private Button voiceChineseButton;//语音中文按钮
    private Button voiceEnglishButton;//语音英文按钮
    private Button sendButton;//发送按钮
    private EditText textEdit;//文字编辑
    private ImageView imageWave;//对话框中的波形界面
    private RecyclerView recyclerView;

    private MsgAdapter msgAdapter;
    private List<Msg> msgList = new ArrayList<>();
    private MySharePreferences mySharePreferences;
    //语音识别对话框
    private Dialog dialog;
    //语音识别器
    private SpeechRecognizer speechRecognizer;
    // 语音合成客户端
    private SpeechSynthesizer mSpeechSynthesizer;

    private String mSampleDirPath;
    private CallApp callApp;


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
                mySharePreferences.setVoiceSelect(Constant.GENERALGIRL_VALUE);
                voiceSelectDeal(mySharePreferences.getVoiceSelect());
                break;
            case R.id.toolbar_general_boy:
                mySharePreferences.setVoiceSelect(Constant.GENERALBOY_VALUE);
                voiceSelectDeal(mySharePreferences.getVoiceSelect());
                break;
            case R.id.toolbar_special_boy:
                mySharePreferences.setVoiceSelect(Constant.SPECIALBOY_VALUE);
                voiceSelectDeal(mySharePreferences.getVoiceSelect());
                break;
            case R.id.toolbar_emotion_boy:
                mySharePreferences.setVoiceSelect(Constant.EMOTIONBOY_VALUE);
                voiceSelectDeal(mySharePreferences.getVoiceSelect());
                break;
            case R.id.toolbar_emotion_child:
                mySharePreferences.setVoiceSelect(Constant.EMOTIONCHILD_VALUE);
                voiceSelectDeal(mySharePreferences.getVoiceSelect());
                break;
            case android.R.id.home:
                Intent intent = new Intent(DialogueActivity.this, MainActivity.class);
                startActivity(intent);
                //返回MainActivity
                break;
            default:
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
        /**
         * 初始化数据
         */
        mySharePreferences = new MySharePreferences(this);
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
     * 初始化界面
     */
    private void initUI() {

        toolBar = (Toolbar) findViewById(R.id.dialogue_layout_toolbar);
        textVoiceChooseButton = (Button) findViewById(R.id.text_voice_choose_button);
        voiceChineseButton = (Button) findViewById(R.id.voice_chinese_button);
        voiceEnglishButton = (Button) findViewById(R.id.voice_english_button);
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

        //设置 voiceChineseButton voiceEnglishButton 触摸监听
        voiceChineseButton.setOnTouchListener(this);
        voiceEnglishButton.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_voice_choose_button:
                //文字语音选择
                mySharePreferences.setTextVoiceFlag(mySharePreferences.getTextVoiceFlag());
                textVoiceChooseButtonDeal();
                break;
            case R.id.send_button:
                //发送按钮，文字编辑时发送按钮显示
                String content = textEdit.getText().toString();
                if (!"".equals(content)) {
                    recyclerViewPositionToEnd(new Msg(content, Msg.TYPE_SENT));
                    textEdit.setText("");
                    //返回对话结果
                    recyclerViewPositionToEnd(new Msg(content, Msg.TYPE_RECEIVED));
                    mSpeechSynthesizer.speak(content);
                    turnToAntherApp(content);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String s = "";
        switch (v.getId()) {
            case R.id.voice_chinese_button:
            case R.id.voice_english_button:
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    LogUtil.d("voice_button松开");
                    dialog.dismiss();
                    speechRecognizer.stopListening();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (ContextCompat.checkSelfPermission(DialogueActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DialogueActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    }
                    LogUtil.d("voice_button按下");
                    dialog.show();
                    Intent intent = new Intent();
                    if (v.getId() == R.id.voice_chinese_button) {
                        s = Constant.CHINESE_LANGUAGE;
                    } else if (v.getId() == R.id.voice_english_button) {
                        s = Constant.ENGLISH_LANGUAGE;
                    }
                    intent.putExtra(Constant.LANGUAGE, s);
                    speechRecognizer.startListening(intent);
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
        if (mySharePreferences.getTextVoiceFlag()) {//显示文字图片，显示语音按钮，语音按钮显示，编辑框消失，发送按钮消失
            textVoiceChooseButton.setBackgroundResource(R.drawable.dialogue_layout_textbutton);
            voiceChineseButton.setVisibility(View.VISIBLE);
            voiceEnglishButton.setVisibility(View.VISIBLE);
            textEdit.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        } else {//显示语音图片,语音按钮消失，编辑框显示,发送按钮显示
            textVoiceChooseButton.setBackgroundResource(R.drawable.dialogue_layout_voicebutton);
            voiceChineseButton.setVisibility(View.GONE);
            voiceEnglishButton.setVisibility(View.GONE);
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
                Toast.makeText(this, Constant.ERROR_NETWORK, Toast.LENGTH_SHORT).show();
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
        String s = nbest.get(0).replaceAll("\\[|\\]|\\,", "");
//        String s = Arrays.toString(nbest.toArray(new String[nbest.size()])).replaceAll("\\[|\\]", "");
        //用对话方式显示语音结果
        recyclerViewPositionToEnd(new Msg(s, Msg.TYPE_SENT));
        // 最终结果处理
        //返回语音识别的结果，语音合成说出
        recyclerViewPositionToEnd(new Msg(s, Msg.TYPE_RECEIVED));
        //需要合成的文本text的长度不能超过1024个GBK字节。
        mSpeechSynthesizer.speak(s);
        turnToAntherApp(s);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        LogUtil.d(getComponentName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 临时结果处理");
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
        copyFromAssetsToSdcard(false, "english/" + Constant.SPEECH_FEMALE_MODEL_NAME_EN, mSampleDirPath + "/"
                + Constant.SPEECH_FEMALE_MODEL_NAME_EN);
        copyFromAssetsToSdcard(false, "english/" + Constant.SPEECH_MALE_MODEL_NAME_EN, mSampleDirPath + "/"
                + Constant.SPEECH_MALE_MODEL_NAME_EN);
        copyFromAssetsToSdcard(false, "english/" + Constant.TEXT_MODEL_NAME_EN, mSampleDirPath + "/"
                + Constant.TEXT_MODEL_NAME_EN);
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
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/" + Constant.SPEECH_FEMALE_MODEL_NAME);
        // 设置Mix模式的合成策略
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        //第一次使用离在线授权文件下载
        mSpeechSynthesizer.auth(TtsMode.MIX);
        // 初始化语音合成器并开始语音合成
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        voiceSelectDeal(mySharePreferences.getVoiceSelect());
    }

    private void voiceSelectDeal(String voiceSelect) {
        String voiceDat = "", voiceText = "", voiceLanguage = "";
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, voiceSelect);//在线语音声音
        switch (voiceSelect) {
            case Constant.GENERALGIRL_VALUE:
                voiceDat = Constant.SPEECH_FEMALE_MODEL_NAME;
                voiceLanguage = Constant.SPEECH_FEMALE_MODEL_NAME_EN;
                voiceText = Constant.GENERALGIRL;
                break;
            case Constant.GENERALBOY_VALUE:
                voiceDat = Constant.SPEECH_MALE_MODEL_NAME;
                voiceLanguage = Constant.SPEECH_MALE_MODEL_NAME_EN;
                voiceText = Constant.GENERALBOY;
                break;
            case Constant.SPECIALBOY_VALUE:
                voiceDat = Constant.SPEECH_MALE_MODEL_NAME;
                voiceLanguage = Constant.SPEECH_MALE_MODEL_NAME_EN;
                voiceText = Constant.SPECIALBOY;
                break;
            case Constant.EMOTIONBOY_VALUE:
                voiceDat = Constant.SPEECH_MALE_MODEL_NAME;
                voiceLanguage = Constant.SPEECH_MALE_MODEL_NAME_EN;
                voiceText = Constant.EMOTIONBOY;
                break;
            case Constant.EMOTIONCHILD_VALUE:
                voiceDat = Constant.SPEECH_FEMALE_MODEL_NAME;
                voiceLanguage = Constant.SPEECH_FEMALE_MODEL_NAME_EN;
                voiceText = Constant.EMOTIONCHILD;
                break;
            default:
                break;
        }
        mSpeechSynthesizer.loadModel(mSampleDirPath + "/" + voiceDat, mSampleDirPath + "/" + Constant.TEXT_MODEL_NAME);
        mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + Constant.TEXT_MODEL_NAME_EN, mSampleDirPath
                + "/" + voiceLanguage);
        mSpeechSynthesizer.speak(voiceText);
        recyclerViewPositionToEnd(new Msg(voiceText, Msg.TYPE_RECEIVED));
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

    private void recyclerViewPositionToEnd(Msg msg) {
        msgList.add(msg);
        msgAdapter.notifyItemInserted(msgList.size() - 1);//将列表中的最后一项加入适配器
        recyclerView.scrollToPosition(msgList.size() - 1);//定位到最后一行
    }
    private void turnToAntherApp(String content){
        if (callApp == null) {//第一次运行加载数据
            callApp = new CallApp(this);
        }
        AppInfo appInfo = callApp.checkOpenApp(content);
        if (appInfo != null) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(appInfo.getPkgName());
            startActivity(intent);
        }
    }
}