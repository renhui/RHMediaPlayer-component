package com.renhui.component.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.RelativeLayout;

public class VideoGestureRelativeLayout extends RelativeLayout {

    private VideoPlayerOnGestureListener videoPlayerOnGestureListener;

    private GestureDetector gestureDetector;

    private VideoGestureListener videoGestureListener;

    private Context context;

    public VideoGestureRelativeLayout(Context context) {
        super(context);
        this.context = context;
    }

    public VideoGestureRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public VideoGestureRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setVideoGestureListener(VideoGestureListener videoGestureListener) {
        this.videoGestureListener = videoGestureListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        videoPlayerOnGestureListener = new VideoPlayerOnGestureListener(context, videoGestureListener);
        gestureDetector = new GestureDetector(context, videoPlayerOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
