package com.renhui.component.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.renhui.component.R;
import com.renhui.component.utils.ScreenUtils;

public class ShowErrorLayout extends LinearLayout {

    public static final int VIDEO_ERROR_NO_NETWORK = 0x0001;
    public static final int VIDEO_ERROR_LOAD_FAILED = 0x0002;

    private Context context;

    private View topSafeSpace;
    private TextView errorDesc;

    public ShowErrorLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ShowErrorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ShowErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.show_error_layout, this);
        topSafeSpace = findViewById(R.id.topSafeSpace);
        errorDesc = findViewById(R.id.errorDesc);
        setVisibility(GONE);
    }

    public void setVideoErrorType(int videoErrorType) {
        if (videoErrorType == VIDEO_ERROR_NO_NETWORK) {
            errorDesc.setText("暂无网络，请检查网络设置");
        } else if (videoErrorType == VIDEO_ERROR_LOAD_FAILED) {
            errorDesc.setText("视频解析失败，请刷新重试");
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams topLayoutParams = new ConstraintLayout.LayoutParams(ScreenUtils.getStatusBarHeight(context) + 10, 18);
        topSafeSpace.setLayoutParams(topLayoutParams);
    }
}
