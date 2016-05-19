package com.sen.haoliyou.mode;

/**
 * Created by Administrator on 2016/5/19.
 */
public class EventAssessSubmit {
    private boolean isReflesh;

    public EventAssessSubmit(boolean isReflesh){
        this.isReflesh = isReflesh;
    }
    public boolean isReflesh() {
        return isReflesh;
    }

    public void setReflesh(boolean reflesh) {
        isReflesh = reflesh;
    }
}
