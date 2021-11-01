package com.renhui.component.controller;

import android.view.MotionEvent;

public interface VideoGestureListener {

    //亮度手势，手指在Layout左半部上下滑动时候调用
    void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    //音量手势，手指在Layout右半部上下滑动时候调用
    void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    //单击手势，确认是单击的时候调用
    void onSingleTapGesture(MotionEvent e);

    //双击手势，确认是双击的时候调用
    void onDoubleTapGesture(MotionEvent e);

    //按下手势，第一根手指按下时候调用
    void onDown(MotionEvent e);

}
