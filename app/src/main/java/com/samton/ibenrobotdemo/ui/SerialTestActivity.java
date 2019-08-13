package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;
import android.widget.TextView;

import com.samton.AppConfig;
import com.samton.IBenRobotSDK.core.IBenSerialUtil;
import com.samton.IBenRobotSDK.core.MainSDK;
import com.samton.IBenRobotSDK.interfaces.ISerialCallBack;
import com.samton.IBenRobotSDK.utils.FileIOUtils;
import com.samton.IBenRobotSDK.utils.FileUtils;
import com.samton.IBenRobotSDK.utils.TimeUtils;
import com.samton.ibenrobotdemo.R;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/10/26
 *     desc   : 舵机连接测试
 *     version: 1.0
 * </pre>
 */

public class SerialTestActivity extends AppCompatActivity implements ISerialCallBack {

    private TextView mTextView;
    private ScrollView mContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_test);

        mTextView = findViewById(R.id.mTextView);
        mContent = findViewById(R.id.mContent);

        MainSDK.getInstance().init(getApplication(), "", "", true);
        IBenSerialUtil.getInstance().setCallBack(this);

        FileUtils.createOrExistsFile(AppConfig.MAP_PATH + "/log.txt");

        findViewById(R.id.mClearBtn).setOnClickListener(v -> mTextView.setText(""));
    }

    @Override
    public void onReadData(String result) {
        final String msg = TimeUtils.getNowString()
                + "---" + result.substring(0, 5) + "\n";
        FileIOUtils.writeFileFromString(
                AppConfig.MAP_PATH + "/log.txt", msg, true);
        runOnUiThread(() -> {
            mTextView.append(msg);
            mContent.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }
}
