package com.zzz.monitor.base;


public interface BaseView<T> {
    void setPresenter(T presenter);

    void toast(String msg);
}
