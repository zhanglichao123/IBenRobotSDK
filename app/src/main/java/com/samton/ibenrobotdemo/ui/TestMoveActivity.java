package com.samton.ibenrobotdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samton.ibenrobotdemo.R;

/**
 * Des:
 * <p>
 * Created by onlykk on 2019-08-29
 */
public class TestMoveActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView power;
    private TextView state;
    private Button connect;
    private Button home;
    private Button save;
    private Button load;
    private Button clear;
    private Button add;
    private RelativeLayout mControllerLay;
    private Button forward;
    private Button left;
    private Button stop;
    private Button right;
    private Button backward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_move);
        assignViews();
    }

    private void assignViews() {
        power = (TextView) findViewById(R.id.power);
        state = (TextView) findViewById(R.id.state);
        connect = (Button) findViewById(R.id.connect);
        home = (Button) findViewById(R.id.home);
        save = (Button) findViewById(R.id.save);
        load = (Button) findViewById(R.id.load);
        clear = (Button) findViewById(R.id.clear);
        add = (Button) findViewById(R.id.add);
        mControllerLay = (RelativeLayout) findViewById(R.id.mControllerLay);
        forward = (Button) findViewById(R.id.forward);
        left = (Button) findViewById(R.id.left);
        stop = (Button) findViewById(R.id.stop);
        right = (Button) findViewById(R.id.right);
        backward = (Button) findViewById(R.id.backward);
    }

    @Override
    public void onClick(View v) {

    }
}
