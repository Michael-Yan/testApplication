package com.zzz.monitor.bean;

import java.util.List;


public class VodConfig {
    private int start;
    private int end;
    private List<Snippet> objects;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<Snippet> getObjects() {
        return objects;
    }

    public void setObjects(List<Snippet> objects) {
        this.objects = objects;
    }
}
