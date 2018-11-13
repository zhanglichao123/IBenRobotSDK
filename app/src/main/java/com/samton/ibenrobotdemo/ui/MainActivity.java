package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.samton.IBenRobotSDK.core.IBenRecordUtil;
import com.samton.IBenRobotSDK.core.IBenSerialUtil;
import com.samton.IBenRobotSDK.core.IBenSensorUtil;
import com.samton.IBenRobotSDK.core.IBenTTSUtil;
import com.samton.IBenRobotSDK.data.SensorData;
import com.samton.IBenRobotSDK.interfaces.ISensorCallBack;
import com.samton.IBenRobotSDK.interfaces.ISerialCallBack;
import com.samton.IBenRobotSDK.utils.ToastUtils;
import com.samton.ibenrobotdemo.R;
import com.samton.ibenrobotdemo.data.SerialMsgHelper;
import com.samton.ibenrobotdemo.utils.SerialPortFinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mSendTestEdit = null;
    private TextView mResultText = null;
    private TextView mSensorText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mResultText = (TextView) findViewById(R.id.mResultText);
        mSensorText = (TextView) findViewById(R.id.mSersor);
        mSendTestEdit = (EditText) findViewById(R.id.mSendTestEdit);

        findViewById(R.id.mLeftUp).setOnClickListener(this);
        findViewById(R.id.mLeftDown).setOnClickListener(this);
        findViewById(R.id.mRightUp).setOnClickListener(this);
        findViewById(R.id.mRightDown).setOnClickListener(this);
        findViewById(R.id.mHeadLeft).setOnClickListener(this);
        findViewById(R.id.mHeadMiddle).setOnClickListener(this);
        findViewById(R.id.mHeadRight).setOnClickListener(this);
        findViewById(R.id.mSerialPortBtn).setOnClickListener(this);
    }

    private void initData() {
        IBenTTSUtil.getInstance().init(this);
        IBenRecordUtil.getInstance().init(this);
        IBenSensorUtil.getInstance().setCallBack(new ISensorCallBack() {
            @Override
            public void onReadData(SensorData sensor) {
                if (sensor != null && sensor.isLegal()) {
                    final StringBuffer buffer = new StringBuffer();
                    buffer.append("传感器参数 \n");
                    buffer.append("二氧化碳：" + sensor.getCarbonDioxide() + "ppm\n");
                    buffer.append("甲醛    ：" + sensor.getFormaldehyde() + "ug/m3 \n");
                    buffer.append("TVOC    : " + sensor.getTVOC() + "ug/m3 \n");
                    buffer.append("PM2.5   ：" + sensor.getPM2_5() + "ug/m3 \n");
                    buffer.append("PM10    ：" + sensor.getPM10() + "ug/m3 \n");
                    buffer.append("温度    ：" + sensor.getTemperature() + "摄氏度\n");
                    buffer.append("湿度    ：" + sensor.getHumidity() + "RH \n");
                    buffer.append("传感器参数 \n");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSensorText.setText(buffer.toString());
                        }
                    });
                }
            }
        });
//        IBenSerialUtil.getInstance().setCallBack(new ISerialCallBack() {
//            @Override
//            public void onReadData(final String result) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mResultText.setText(result);
//                    }
//                });
//            }
//        });
//        IBenPrintSDK.getInstance().initPrinter(this);
    }

    @Override
    public void onClick(View v) {
        String msg = mSendTestEdit.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) {
            ToastUtils.showShort("请输入要发送/说的话");
            return;
        }
        switch (v.getId()) {
            case R.id.mLeftUp:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getArmMsg(SerialMsgHelper.Action.LEFT_ARM_UP));
                break;
            case R.id.mLeftDown:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getArmMsg(SerialMsgHelper.Action.LEFT_ARM_DOWN));
                break;
            case R.id.mRightUp:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getArmMsg(SerialMsgHelper.Action.RIGHT_ARM_UP));
                break;
            case R.id.mRightDown:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getArmMsg(SerialMsgHelper.Action.RIGHT_ARM_DOWN));
                break;
            case R.id.mHeadLeft:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getHeadMsg(SerialMsgHelper.Action.HEAD_LEFT));
                break;
            case R.id.mHeadMiddle:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getHeadMsg(SerialMsgHelper.Action.HEAD_MIDDLE));
                break;
            case R.id.mHeadRight:
                IBenSerialUtil.getInstance().sendData(
                        SerialMsgHelper.getHeadMsg(SerialMsgHelper.Action.HEAD_RIGHT));
                break;
            case R.id.mSerialPortBtn:
                IBenSerialUtil.getInstance().sendData(msg);
                break;
        }
    }

}
