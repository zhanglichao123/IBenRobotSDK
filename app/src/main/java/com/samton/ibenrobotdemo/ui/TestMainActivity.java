package com.samton.ibenrobotdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.samton.ibenrobotdemo.R;

/**
 * Des:
 * <p>
 * Created by onlykk on 2019-08-29
 */
public class TestMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button testMoveBtn;
    private Button testActionBtn;
    private Button testPrintBtn;
    private Button testWakeupBtn;
    private Button testChatBtn;
    private Button testTtsBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);
        assignViews();
    }

    private void assignViews() {
        testMoveBtn = findViewById(R.id.test_move_btn);
        testActionBtn = findViewById(R.id.test_action_btn);
        testPrintBtn = findViewById(R.id.test_print_btn);
        testWakeupBtn = findViewById(R.id.test_wakeup_btn);
        testChatBtn = findViewById(R.id.test_chat_btn);
        testTtsBtn = findViewById(R.id.test_tts_btn);

        testMoveBtn.setOnClickListener(this);
        testActionBtn.setOnClickListener(this);
        testPrintBtn.setOnClickListener(this);
        testWakeupBtn.setOnClickListener(this);
        testChatBtn.setOnClickListener(this);
        testTtsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_move_btn:
                startActivity(new Intent(this, TestMoveActivity.class));
                break;
            case R.id.test_action_btn:
                startActivity(new Intent(this, TestActionActivity.class));
                break;
            case R.id.test_print_btn:
                startActivity(new Intent(this, TestPrintActivity.class));
                break;
            case R.id.test_wakeup_btn:
                startActivity(new Intent(this, TestWakeUpActivity.class));
                break;
            case R.id.test_chat_btn:
                startActivity(new Intent(this, TestChatActivity.class));
                break;
            case R.id.test_tts_btn:
                startActivity(new Intent(this, TestTTSActivity.class));
                break;
        }
    }
}
