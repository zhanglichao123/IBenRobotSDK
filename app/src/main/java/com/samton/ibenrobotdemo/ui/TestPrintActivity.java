package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.samton.IBenRobotSDK.core.IBenPrintSDK;
import com.samton.ibenrobotdemo.R;

/**
 * Des:
 * <p>
 * Created by onlykk on 2019-08-29
 */
public class TestPrintActivity extends AppCompatActivity implements View.OnClickListener {
    private Button linkPrint;
    private Button txtPrint;
    private Button imgPrint;
    private Button qrPrint;
    private Button txtImgPrint;
    private Button txtQrPrint;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_print);
        assignViews();
    }

    private void assignViews() {
        linkPrint = findViewById(R.id.link_print);
        txtPrint = findViewById(R.id.txt_print);
        imgPrint = findViewById(R.id.img_print);
        qrPrint = findViewById(R.id.qr_print);
        txtImgPrint = findViewById(R.id.txt_img_print);
        txtQrPrint = findViewById(R.id.txt_qr_print);

        linkPrint.setOnClickListener(this);
        txtPrint.setOnClickListener(this);
        imgPrint.setOnClickListener(this);
        qrPrint.setOnClickListener(this);
        txtImgPrint.setOnClickListener(this);
        txtQrPrint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_print:
                if (IBenPrintSDK.getInstance().isConnect()) {
                    Toast.makeText(this, "已连接，无需重复连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                IBenPrintSDK.getInstance().initPrinter(this);
                break;
            case R.id.txt_print:
                if (!IBenPrintSDK.getInstance().isConnect()) {
                    Toast.makeText(this, "打印机未连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.img_print:
                if (!IBenPrintSDK.getInstance().isConnect()) {
                    Toast.makeText(this, "打印机未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.qr_print:
                if (!IBenPrintSDK.getInstance().isConnect()) {
                    Toast.makeText(this, "打印机未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.txt_img_print:
                if (!IBenPrintSDK.getInstance().isConnect()) {
                    Toast.makeText(this, "打印机未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.txt_qr_print:
                if (!IBenPrintSDK.getInstance().isConnect()) {
                    Toast.makeText(this, "打印机未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }
}
