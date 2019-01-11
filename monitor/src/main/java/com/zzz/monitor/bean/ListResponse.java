package com.zzz.monitor.bean;

import java.util.List;

public class ListResponse<T> {
    private int total;
    private List<T> objects;

    public int getTotal() {
        return total;
    }

    public List<T> getObjects() {
        return objects;
    }
}
