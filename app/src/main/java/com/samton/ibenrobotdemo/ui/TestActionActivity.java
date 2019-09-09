package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.samton.IBenRobotSDK.core.IBenActionUtil;
import com.samton.ibenrobotdemo.R;

/**
 * Des:动作测试
 * <p>
 * Created by onlykk on 2019-08-29
 */
public class TestActionActivity extends AppCompatActivity implements View.OnClickListener {
    private Button handLeftUp;
    private Button handLeftDown;
    private Button handRightUp;
    private Button handRightDown;
    private Button headLeft;
    private Button headMiddle;
    private Button headRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_action);
        assignViews();
    }

    private void assignViews() {
        handLeftUp = findViewById(R.id.hand_left_up);
        handLeftDown = findViewById(R.id.hand_left_down);
        handRightUp = findViewById(R.id.hand_right_up);
        handRightDown = findViewById(R.id.hand_right_down);
        headLeft = findViewById(R.id.head_left);
        headMiddle = findViewById(R.id.head_middle);
        headRight = findViewById(R.id.head_right);

        handLeftUp.setOnClickListener(this);
        handLeftDown.setOnClickListener(this);
        handRightUp.setOnClickListener(this);
        handRightDown.setOnClickListener(this);
        headLeft.setOnClickListener(this);
        headMiddle.setOnClickListener(this);
        headRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hand_left_up:
                IBenActionUtil.getInstance().leftArm(90);
                break;
            case R.id.hand_left_down:
                IBenActionUtil.getInstance().leftArm(0);
                break;
            case R.id.hand_right_up:
                IBenActionUtil.getInstance().rightArm(90);
                break;
            case R.id.hand_right_down:
                IBenActionUtil.getInstance().rightArm(0);
                break;
            case R.id.head_left:
                IBenActionUtil.getInstance().headAction(30);
                break;
            case R.id.head_right:
                IBenActionUtil.getInstance().headAction(-30);
                break;
            case R.id.head_middle:
                IBenActionUtil.getInstance().headAction(0);
                break;
        }
    }
}
