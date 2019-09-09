package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.samton.IBenRobotSDK.core.IBenWakeUpUtil;
import com.samton.ibenrobotdemo.R;

/**
 * Des:
 * <p>
 * Created by onlykk on 2019-08-29
 */
public class TestWakeUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button startWake;
    private Button stopWake;
    private Button setBeam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wakeup);
        assignViews();
    }

    private void assignViews() {
        startWake = findViewById(R.id.start_wake);
        stopWake = findViewById(R.id.stop_wake);
        setBeam = findViewById(R.id.set_beam);
        startWake.setOnClickListener(this);
        stopWake.setOnClickListener(this);
        setBeam.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_wake:
                IBenWakeUpUtil.getInstance().setCallBack(angle -> Toast.makeText(TestWakeUpActivity.this, "被语音唤醒，声源角度：" + angle, Toast.LENGTH_SHORT).show());
                break;
            case R.id.stop_wake:
                IBenWakeUpUtil.getInstance().stopWakeUp();
                Toast.makeText(this, "已停止被唤醒", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set_beam:
                IBenWakeUpUtil.getInstance().setBeam();
                Toast.makeText(this, "已加强正前方 1⃣ 麦", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
