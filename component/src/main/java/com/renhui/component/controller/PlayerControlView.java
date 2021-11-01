package com.renhui.component.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.renhui.component.R;
import com.renhui.component.core.VideoMediaPlayer;
import com.renhui.component.utils.ScreenUtils;
import com.renhui.component.utils.TimeUtils;
import com.renhui.mediaplayer.base.AbstractVideoPlayer;
import com.renhui.mediaplayer.utils.GlobalConfig;

public class PlayerControlView extends FrameLayout {

    public final static long DEFAULT_SHOW_TIMEOUT_MS = 5000L;

    private Context context;

    public boolean functionLocking = false;
    public int playMode = AbstractVideoPlayer.PLAY_VIDEO_MODE;
    public int currentShowMode;

    private View topSafeSpace;
    private View topRightSafeSpace;
    private View bottomSafeSpace;
    private View bottomRightSafeSpace;
    private View bottomBSafeSpace;
    private View bottomBRightSafeSpace;

    private ConstraintLayout videoTopLayer;
    private LinearLayout videoBottomLayer;

    private AppCompatSeekBar playerSeekBar;

    private TextView currentTime;
    private TextView durationTime;

    private ImageView ivPlayBtn;

    private View leftCenterView;
    private ImageView videoFunctionLock;
    private TextView tvSpeedChange;

    private ImageView videoImageCapture;
    private VideoMediaPlayer videoMediaPlayer;

    private VideoCaptureListener videoCaptureListener;

    public PlayerControlView(@NonNull Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    public PlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews();
    }

    public void setVideoMediaPlayer(VideoMediaPlayer videoMediaPlayer) {
        Log.e(GlobalConfig.LOG_TAG, "设置播放器");
        this.videoMediaPlayer = videoMediaPlayer;
    }

    public void setVideoCaptureListener(VideoCaptureListener videoCaptureListener) {
        this.videoCaptureListener = videoCaptureListener;
    }

    private void initViews() {
        inflate(context, R.layout.live_player_control_view, this);
        leftCenterView = findViewById(R.id.leftCenter);
        videoFunctionLock = findViewById(R.id.videoFunctionLock);
        playerSeekBar = findViewById(R.id.playerSeekBar);
        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);
        topSafeSpace = findViewById(R.id.topSafeSpace);
        topRightSafeSpace = findViewById(R.id.topRightSafeSpace);
        bottomSafeSpace = findViewById(R.id.bottomSafeSpace);
        bottomRightSafeSpace = findViewById(R.id.bottomRightSafeSpace);
        videoTopLayer = findViewById(R.id.videoTopLayer);
        videoBottomLayer = findViewById(R.id.videoBottomLayer);
        bottomBSafeSpace = findViewById(R.id.bottomBSafeSpace);
        bottomBRightSafeSpace = findViewById(R.id.bottomBRightSafeSpace);
        videoImageCapture = findViewById(R.id.videoImageCapture);
        tvSpeedChange = findViewById(R.id.speedChange);
        ivPlayBtn = findViewById(R.id.playBtn);
        // 进度条
        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoMediaPlayer.seekTo((videoMediaPlayer.getDuration() * seekBar.getProgress() / 100));
                show();
            }
        });
        // 播放按钮
        ivPlayBtn.setOnClickListener(v -> {
            Log.e(GlobalConfig.LOG_TAG, "播放按钮点击");
            if (!videoMediaPlayer.isPlayable()) return;
            if (videoMediaPlayer.isPlaying()) {
                videoMediaPlayer.pause();
            } else {
                videoMediaPlayer.start();
            }
        });
        // 倍速
        tvSpeedChange.setOnClickListener(v -> {
            if (videoMediaPlayer != null) {
                String speedText = tvSpeedChange.getText().toString();
                switch (speedText) {
                    case "1x":
                        videoMediaPlayer.setSpeed(1.25f);
                        tvSpeedChange.setText("1.25x");
                        break;
                    case "1.25x":
                        videoMediaPlayer.setSpeed(1.5f);
                        tvSpeedChange.setText("1.5x");
                        break;
                    case "1.5x":
                        videoMediaPlayer.setSpeed(2f);
                        tvSpeedChange.setText("2x");
                        break;
                    case "2x":
                        videoMediaPlayer.setSpeed(0.5f);
                        tvSpeedChange.setText("0.5x");
                        break;
                    default:
                        videoMediaPlayer.setSpeed(1f);
                        tvSpeedChange.setText("1x");
                        break;
                }
            }
        });
        // 截屏
        videoImageCapture.setOnClickListener(v -> {
            if (videoCaptureListener != null) {
                videoCaptureListener.onVideoCapture();
            }
        });
        setCenterSpaceView();
        // 按照横屏的布局方式进行布局的展示
        showLandScapeLayout();
    }

    // 设置播放模式 - 音频 & 视频
    public void setPlayMode(int playMode) {
        this.playMode = playMode;
        this.currentShowMode = playMode;
    }

    private void setCenterSpaceView() {
        ViewGroup.LayoutParams centerLayoutParams = new LinearLayout.LayoutParams(ScreenUtils.getStatusBarHeight(context) + 10, 18);
        leftCenterView.setLayoutParams(centerLayoutParams);

        videoFunctionLock.setOnClickListener(v -> {
            functionLocking = !functionLocking;
            if (functionLocking) {
                videoFunctionLock.setImageResource(R.mipmap.ic_video_function_lock);
                showLocked();
            } else {
                videoFunctionLock.setImageResource(R.mipmap.ic_video_function_unlock);
            }
        });
    }

    /**
     * 根据回调播放器状态，设置当前的播放按钮图标
     */
    public void onPlayStatus(boolean isPlaying) {
        ivPlayBtn.post(() -> {
            if (isPlaying) {
                ivPlayBtn.setImageResource(R.mipmap.ic_video_pause_icon);
            } else {
                ivPlayBtn.setImageResource(R.mipmap.ic_video_play_icon);
            }
        });
    }

    private final Runnable hideRunnable = this::hide;

    public boolean showing = false;

    public void show() {
        showing = true;
        setVisibility(VISIBLE);
        if (videoTopLayer.getVisibility() == GONE) {
            videoTopLayer.setVisibility(VISIBLE);
            Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_enter_from_top);
            videoTopLayer.startAnimation(loadAnimation);
        }
        if (videoBottomLayer.getVisibility() == GONE) {
            videoBottomLayer.clearAnimation();
            videoBottomLayer.setVisibility(VISIBLE);
            Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_enter_from_bottom);
            videoBottomLayer.startAnimation(loadAnimation);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        requestFocus();
        removeCallbacks(hideRunnable);
        postDelayed(hideRunnable, DEFAULT_SHOW_TIMEOUT_MS);
    }

    public void showLocked() {
        showing = true;
        videoTopLayer.setVisibility(GONE);
        videoBottomLayer.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    public void hide() {
        showing = false;
        removeCallbacks(hideRunnable);
        if (videoTopLayer.getVisibility() == VISIBLE) {
            videoTopLayer.clearAnimation();
            videoTopLayer.setVisibility(GONE);
            Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_exit_from_top);
            videoTopLayer.startAnimation(loadAnimation);
        }
        if (videoBottomLayer.getVisibility() == VISIBLE) {
            videoBottomLayer.clearAnimation();
            videoBottomLayer.setVisibility(GONE);
            Animation loadAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_exit_from_bottom);
            videoBottomLayer.startAnimation(loadAnimation);
            loadAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void hideLocked() {
        showing = false;
        removeCallbacks(hideRunnable);
        setVisibility(GONE);
    }

    // 更新播放进度
    public void onPlayerProgress(long currentPosition, long duration) {
        playerSeekBar.post(() -> {
            if (duration > 0) {
                playerSeekBar.setProgress((int) (100 * currentPosition / duration));
            } else {
                playerSeekBar.setProgress(0);
            }
        });
        currentTime.post(() -> currentTime.setText(TimeUtils.getFormatTime(currentPosition)));
        durationTime.post(() -> durationTime.setText(TimeUtils.getFormatTime(duration)));
    }

    private void showLandScapeLayout() {
        ConstraintLayout.LayoutParams topLayoutParams = new ConstraintLayout.LayoutParams(ScreenUtils.getStatusBarHeight(context) + 10, 18);
        topSafeSpace.setLayoutParams(topLayoutParams);
        LinearLayout.LayoutParams bottomLayoutParams = new LinearLayout.LayoutParams(ScreenUtils.getStatusBarHeight(context) + 12, 18);
        topRightSafeSpace.setLayoutParams(bottomLayoutParams);
        bottomSafeSpace.setLayoutParams(bottomLayoutParams);
        bottomRightSafeSpace.setLayoutParams(bottomLayoutParams);
        bottomBSafeSpace.setLayoutParams(bottomLayoutParams);
        bottomBRightSafeSpace.setLayoutParams(bottomLayoutParams);
    }

}
