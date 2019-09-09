package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iflytek.cloud.SpeechError;
import com.samton.IBenRobotSDK.core.IBenTTSUtil;
import com.samton.IBenRobotSDK.interfaces.IBenTTSCallBack;
import com.samton.ibenrobotdemo.R;

/**
 * Des:
 * <p>
 * Created by onlykk on 2019-08-29
 */
public class TestTTSActivity extends AppCompatActivity implements View.OnClickListener {
    private Button initTts;
    private EditText color;
    private EditText speed;
    private EditText pitch;
    private EditText volume;
    private Button setTts;
    private EditText txt;
    private Button startTts;
    private Button pauseTts;
    private Button resumeTts;
    private Button stopTts;
    private Button isSpeakTts;
    private ProgressBar progress_bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tts);
        assignViews();
    }

    private void assignViews() {
        initTts = (Button) findViewById(R.id.init_tts);
        color = (EditText) findViewById(R.id.color);
        speed = (EditText) findViewById(R.id.speed);
        pitch = (EditText) findViewById(R.id.pitch);
        volume = (EditText) findViewById(R.id.volume);
        setTts = (Button) findViewById(R.id.set_tts);
        txt = (EditText) findViewById(R.id.txt);
        startTts = (Button) findViewById(R.id.start_tts);
        pauseTts = (Button) findViewById(R.id.pause_tts);
        resumeTts = (Button) findViewById(R.id.resume_tts);
        stopTts = (Button) findViewById(R.id.stop_tts);
        isSpeakTts = (Button) findViewById(R.id.is_speak_tts);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        initTts.setOnClickListener(this);
        setTts.setOnClickListener(this);
        startTts.setOnClickListener(this);
        pauseTts.setOnClickListener(this);
        resumeTts.setOnClickListener(this);
        stopTts.setOnClickListener(this);
        isSpeakTts.setOnClickListener(this);

        progress_bar.setMax(100);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.init_tts:
                IBenTTSUtil.getInstance().init(this);
                break;
            case R.id.set_tts:
                IBenTTSUtil.getInstance().setTTSParam(color.getText().toString().trim(),
                        speed.getText().toString().trim(),
                        pitch.getText().toString().trim(),
                        volume.getText().toString().trim());
                break;
            case R.id.start_tts:
                String ttsTxt = txt.getText().toString().trim();
                if (TextUtils.isEmpty(ttsTxt)) {
                    Toast.makeText(this, "请输入需要播报的文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                IBenTTSUtil.getInstance().startSpeaking(ttsTxt, new IBenTTSCallBack() {
                    @Override
                    public void onProgress(int percent, int beginPos, int endPos) {
                        progress_bar.setProgress(percent);
                    }

                    @Override
                    public void onPause() {
                        Toast.makeText(TestTTSActivity.this, "暂停播报", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResume() {
                        Toast.makeText(TestTTSActivity.this, "继续播报", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSpeakBegin() {
                        Toast.makeText(TestTTSActivity.this, "开始播报", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompleted(SpeechError error) {
                        Toast.makeText(TestTTSActivity.this, "播报结束" + error, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.pause_tts:
                IBenTTSUtil.getInstance().pauseSpeaking();
                break;
            case R.id.resume_tts:
                IBenTTSUtil.getInstance().resumeSpeaking();
                break;
            case R.id.stop_tts:
                IBenTTSUtil.getInstance().stopSpeaking();
                IBenTTSUtil.getInstance().recycle();
                break;
            case R.id.is_speak_tts:
                Toast.makeText(this, IBenTTSUtil.getInstance().isSpeaking() ? "正在播报" : "未在播报", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
