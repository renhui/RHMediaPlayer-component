package com.renhui.component.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.renhui.component.R;
import com.renhui.component.utils.ScreenUtils;

public class ShowCompleteLayout extends LinearLayout {

    private Context context;

    private View topSafeSpace;

    public ShowCompleteLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ShowCompleteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ShowCompleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.show_complete_layout, this);
        topSafeSpace = findViewById(R.id.topSafeSpace);
        topSafeSpace.setLayoutParams(new ConstraintLayout.LayoutParams(ScreenUtils.getStatusBarHeight(context) + 10, 18));
        setVisibility(GONE);
        // 重新播放
        findViewById(R.id.vod_repeat_layer).setOnClickListener(v -> {
            // TODO 重新播放
        });
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams topLayoutParams = new ConstraintLayout.LayoutParams(ScreenUtils.getStatusBarHeight(context) + 10, 18);
        topSafeSpace.setLayoutParams(topLayoutParams);
    }
}
