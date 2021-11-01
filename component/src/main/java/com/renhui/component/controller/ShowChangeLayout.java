package com.renhui.component.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.renhui.component.R;

import java.lang.ref.WeakReference;

public class ShowChangeLayout extends LinearLayout {

    public static final int CHANGE_BRIGHTNESS_MODE = 0x0001;
    public static final int CHANGE_VOLUME_MODE = 0x0002;

    private final Context context;

    private int currentShowMode;

    private HideRunnable hideRunnable;
    private ImageView modeImageView;
    private AppCompatSeekBar changeProgress;

    public ShowChangeLayout(Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public ShowChangeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    public ShowChangeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(context).inflate(R.layout.show_change_layout, this);
        modeImageView = findViewById(R.id.ic_change_flag);
        changeProgress = findViewById(R.id.change_seek_bar);
        hideRunnable = new HideRunnable(this);
        setVisibility(GONE);
    }

    public void setChangeShowMode(int changeShowMode) {
        currentShowMode = changeShowMode;

    }

    public void show() {
        this.setVisibility(VISIBLE);
        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, 1000);
    }

    public void setProgress(int progress) {
        if (currentShowMode == CHANGE_BRIGHTNESS_MODE) {
            if (progress >= 50) {
                modeImageView.post(() -> modeImageView.setImageResource(R.mipmap.ic_video_brightness_high));
            } else {
                modeImageView.post(() -> modeImageView.setImageResource(R.mipmap.ic_video_brightness_low));
            }
        } else if (currentShowMode == CHANGE_VOLUME_MODE) {
            if (progress > 0) {
                modeImageView.post(() -> modeImageView.setImageResource(R.mipmap.ic_video_volume_high));
            } else {
                modeImageView.post(() -> modeImageView.setImageResource(R.mipmap.ic_video_volume_close));
            }
        }
        changeProgress.setProgress(progress);
    }

    private static class HideRunnable implements Runnable {

        private final WeakReference<ShowChangeLayout> weakReference;

        public HideRunnable(ShowChangeLayout showChangeLayout) {
            weakReference = new WeakReference<>(showChangeLayout);
        }

        @Override
        public void run() {
            ShowChangeLayout changeLayout = weakReference.get();
            if (changeLayout != null) {
                changeLayout.setVisibility(GONE);
            }
        }
    }
}
