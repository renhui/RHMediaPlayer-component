package com.renhui.component.controller;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.renhui.component.utils.ScreenUtils;

public class VideoPlayerOnGestureListener extends GestureDetector.SimpleOnGestureListener {

    @ScrollMode
    private int scrollMode = ScrollMode.NONE;

    private VideoGestureListener videoGestureListener;

    private Context context;

    public VideoPlayerOnGestureListener(Context context, VideoGestureListener videoGestureListener) {
        super();
        this.context = context;
        this.videoGestureListener = videoGestureListener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //每次按下都重置为NONE
        scrollMode = ScrollMode.NONE;
        if (videoGestureListener != null) {
            videoGestureListener.onDown(e);
        }
        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) {
        super.onShowPress(e);
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (videoGestureListener != null) {
            videoGestureListener.onSingleTapGesture(e);
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (videoGestureListener != null) {
            videoGestureListener.onDoubleTapGesture(e);
        }
        return super.onDoubleTap(e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return super.onDoubleTapEvent(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        switch (scrollMode) {
            case ScrollMode.NONE:
                if (Math.abs(distanceX) - Math.abs(distanceY) > distanceX) {
                    // Do Nothing - 原用于快进快退
                } else {
                    if (e1.getX() < (float) (ScreenUtils.getScreenWidth(context) / 2)) {
                        scrollMode = ScrollMode.BRIGHTNESS;
                    } else {
                        scrollMode = ScrollMode.VOLUME;
                    }
                }
                break;
            case ScrollMode.VOLUME:
                if (videoGestureListener != null) {
                    videoGestureListener.onVolumeGesture(e1, e2, distanceX, distanceY);
                }
                break;
            case ScrollMode.BRIGHTNESS:
                if (videoGestureListener != null) {
                    videoGestureListener.onBrightnessGesture(e1, e2, distanceX, distanceY);
                }
                break;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
