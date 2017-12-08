package com.text.updatademo.listenter;

/**
 * Created by sanmu on 2016/10/13 0013.
 */
public interface Callback {
    /**
     * 弹出对话框点击确认和取消按钮
     *
     * @param position 1，确认；2，取消
     */
    void callback(int position);
}
