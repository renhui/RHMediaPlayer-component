package com.renhui.component.controller;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.renhui.component.R;
import com.renhui.component.utils.GlideUtil;

public class ShowLoadingLayout extends LinearLayout {

    private Context context;
    
    public ShowLoadingLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ShowLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ShowLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        inflate(context, R.layout.show_loading_layout, this);
        setVisibility(GONE);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        ImageView loadingView = findViewById(R.id.loadingView);
        if (visibility == VISIBLE) {
            GlideUtil.getInstance(context).showImageGif(context, R.drawable.ic_video_buffering_loading_icon, loadingView);
        } else {
            Drawable drawable = loadingView.getDrawable();
            if (drawable instanceof Animatable) {
                Animatable animatable = (Animatable) drawable;
                animatable.stop();
            }
        }
    }
}
