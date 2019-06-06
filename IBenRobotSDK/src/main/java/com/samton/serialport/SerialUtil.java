package com.samton.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/06/28
 *     desc   : 串口操作工具
 *     version: 1.0
 * </pre>
 */
public class SerialUtil {
    /**
     * 输入流(用于数据读取)
     */
    private InputStream mInputStream;
    /**
     * 输出流(用于数据写入)
     */
    private OutputStream mOutputStream;
    /**
     * 缓冲区大小
     */
    private static final int MAX = 512;

    /**
     * 构造函数
     *
     * @param path 串口的物理地址
     */
    public SerialUtil(String path) {
        //串口对象
        SerialPort serialPort = new SerialPort();
        serialPort.open(new File(path), SerialPort.BAUDRATE.B115200, SerialPort.STOPB.B1
                , SerialPort.DATAB.CS8, SerialPort.PARITY.NONE, SerialPort.FLOWCON.NONE);
        mInputStream = serialPort.getInputStream();
        mOutputStream = serialPort.getOutputStream();
    }

    /**
     * 从串口读取数据
     *
     * @return 回显的数组
     * @throws NullPointerException 空指针异常
     */
    public synchronized byte[] getDataByte() {
        if (mInputStream == null) return null;
        byte[] buffer = new byte[MAX];
        try {
            if (mInputStream.available() > 0 && mInputStream.read(buffer) > 0) {
                return buffer;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向串口上写数据
     *
     * @param data 显示的16进制的字符串
     */
    public synchronized void setData(byte[] data) {
        if (mOutputStream == null) return;
        try {
            mOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
