package com.samton.IBenRobotSDK.core;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.usb.USBPort;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 打印机sdk
 */
public class IBenPrintSDK {
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
    private static IBenPrintSDK mInstance = null;
    private boolean isConnect = false;
    private PrinterInstance mPrinterInstance;

    private IBenPrintSDK() {
    }

    //获取打印SDK单例
    public static IBenPrintSDK getInstance() {
        if (mInstance == null) {
            synchronized (IBenPrintSDK.class) {
                if (mInstance == null) {
                    mInstance = new IBenPrintSDK();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化打印机，开启连接
     */
    public void initPrinter(Context context) {
        isConnect = false;
        //检测设备列表
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (manager == null) return;
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        if (devices == null || devices.size() <= 0) return;
        ArrayList<UsbDevice> deviceList = new ArrayList<>();
        for (UsbDevice device : devices.values()) {
            if (USBPort.isUsbPrinter(device)) deviceList.add(device);
        }
        if (deviceList.isEmpty()) return;
        //广播
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        manager.requestPermission(deviceList.get(0), intent);
        //打印机核心类
        mPrinterInstance = PrinterInstance.getPrinterInstance(context, deviceList.get(0), new Handler(msg -> {
            isConnect = msg.what == PrinterConstants.Connect.SUCCESS;
            return false;
        }));
        //开启连接
        mPrinterInstance.openConnection();
        //初始化打印机
        mPrinterInstance.initPrinter();
    }

    /**
     * 关闭打印机连接
     */
    public void closePrint() {
        if (mPrinterInstance != null) {
            mPrinterInstance.closeConnection();
            isConnect = false;
        }
    }

    /**
     * 判断打印机是否已连接
     */
    public boolean isConnect() {
        if (mPrinterInstance == null) return false;
        return isConnect;
    }

    /**
     * 获取打印机当前状态
     */
    public int getCurrentStatus() {
        if (mPrinterInstance == null) return -1;
        return mPrinterInstance.getCurrentStatus();
    }

    /**
     * 设置字体大小
     */
    public IBenPrintSDK setFont(int size) {
        if (mPrinterInstance != null) mPrinterInstance.setFont(0, size, size, 0, 0);
        return getInstance();
    }

    /**
     * 设置打印位置
     */
    public IBenPrintSDK setPrinter(int value) {
        if (mPrinterInstance != null)
            mPrinterInstance.setPrinter(PrinterConstants.Command.ALIGN, value);
        return getInstance();
    }

    /**
     * 设置打印的文字内容
     */
    public IBenPrintSDK printText(String content) {
        if (mPrinterInstance != null) mPrinterInstance.printText(content);
        return getInstance();
    }

    /**
     * 这是打印的灰度图片内容
     */
    public IBenPrintSDK printColorImg2Gray(Bitmap bitmap, PrinterConstants.PAlign type) {
        if (mPrinterInstance != null) mPrinterInstance.printColorImg2Gray(bitmap, type, 0, false);
        return getInstance();
    }

    /**
     * 切纸
     */
    public void cutPaper() {
        if (mPrinterInstance != null) mPrinterInstance.cutPaper(65, 1);
    }
}
