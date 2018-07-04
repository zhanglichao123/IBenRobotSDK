package com.samton.IBenRobotSDK.utils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/07/04 14:23
 *   desc    : io线程到io线程的调度器
 *   version : 1.0
 * </pre>
 */
public abstract class BaseIoSubscriber<T> extends Observable<T> implements Observer<T> {

    /**
     * 构造
     */
    protected BaseIoSubscriber() {
        subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .subscribe(this);
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {

    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    /**
     * 成功回调
     *
     * @param t 数据
     */
    public abstract void onSuccess(T t);

    @Override
    public void onError(Throwable e) {
        onFailed(e);
    }

    /**
     * 失败回调
     *
     * @param e 失败原因
     */
    public abstract void onFailed(Throwable e);

    @Override
    public void onComplete() {

    }
}
