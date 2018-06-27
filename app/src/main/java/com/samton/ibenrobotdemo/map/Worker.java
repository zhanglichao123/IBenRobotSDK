package com.samton.ibenrobotdemo.map;

import java.util.ArrayList;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/14 11:18
 *   desc    : 工作线程类
 *   version : 1.0
 * </pre>
 */
public final class Worker {
    /**
     * 工作状态 - 空闲
     */
    private final static int IDLE = 0x0;
    /**
     * 工作状态 - 工作中
     */
    private final static int WORKING = 0x1;
    /**
     * 线程队列
     */
    private final ArrayList<Runnable> jobQueue = new ArrayList<>();
    /**
     * 工作线程锁
     */
    private final Object WorkerLock = new Object();
    /**
     * 工作状态锁
     */
    private final Object StateLock = new Object();
    /**
     * 工作状态
     */
    private int state;
    /**
     * 工作线程
     */
    private Thread workerThread;
    /**
     * 工作线程工作类
     */
    private Runnable workerRunnable = new Runnable() {
        @Override
        public void run() {
            // 声明局部线程集合变量
            ArrayList<Runnable> jobs = new ArrayList<>();
            // 死循环
            while (true) {
                // 同步锁,保证添加的线程唯一
                synchronized (jobQueue) {
                    // 把现有的线程集合加入到局部变量中,并清空类的线程集合
                    jobs.addAll(jobQueue);
                    jobQueue.clear();
                }
                // 如果局部线程集合变量内没有要执行的线程,跳出死循环
                if (jobs.isEmpty()) {
                    break;
                }
                // 遍历局部线程集合变量,执行其中的所有线程
                for (Runnable job : jobs) {
                    job.run();
                }
                // 清空局部变量中的线程
                jobs.clear();
            }
            // 工作状态锁,保证工作状态的改变唯一性
            synchronized (StateLock) {
                state = IDLE;
            }
        }
    };

    /**
     * 构造函数
     */
    public Worker() {
        // 初始化状态为空闲
        state = IDLE;
        // 初始化工作线程
        workerThread = new Thread(workerRunnable);
    }

    /**
     * 将要执行的工作放到队列中
     *
     * @param job 要执行的工作
     */
    public void push(Runnable job) {
        // 同步锁,保证添加的线程唯一
        synchronized (jobQueue) {
            jobQueue.add(job);
        }
        // 执行线程
        execute(job);
    }

    /**
     * 执行方法
     */
    private void execute() {
        // 状态锁,保证状态值改变唯一
        synchronized (StateLock) {
            // 如果线程当前处于工作状态,退出
            if (state == WORKING) {
                return;
            }
            // 将状态改为工作中
            state = WORKING;
        }
        // 如果工作线程被终结掉了,创建新的工作线程
        if (workerThread.getState() == Thread.State.TERMINATED) {
            workerThread = new Thread(workerRunnable);
        }
        // 如果工作线程处于线程等待状态,唤醒线程
        if (workerThread.getState() == Thread.State.WAITING) {
            // 工作锁,保证线程执行唯一性
            synchronized (WorkerLock) {
                workerThread.notify();
            }
        }
        // 如果工作线程处于新创建状态,开启线程
        if (workerThread.getState() == Thread.State.NEW) {
            workerThread.start();
        }
    }

    /**
     * 执行方法
     *
     * @param runnable 线程
     */
    private void execute(Runnable runnable) {
        ExecutorUtil.getInstance().execute(runnable);
    }

    /**
     * 将要执行的工作放到队列的最前面
     *
     * @param job 要执行的工作
     */
    public void pushHead(Runnable job) {
        // 同步锁,保证添加的线程唯一
        synchronized (jobQueue) {
            jobQueue.add(0, job);
        }
        // 执行线程
        execute(job);
    }

    /**
     * 清空线程队列
     */
    public void clear() {
        // 同步锁,保证添加的线程唯一
        synchronized (jobQueue) {
            jobQueue.clear();
        }
    }
}
