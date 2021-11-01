package com.renhui.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.renhui.component.controller.PlayerControlView;
import com.renhui.component.controller.ShowChangeLayout;
import com.renhui.component.controller.ShowCompleteLayout;
import com.renhui.component.controller.ShowErrorLayout;
import com.renhui.component.controller.ShowLoadingLayout;
import com.renhui.component.controller.VideoCaptureListener;
import com.renhui.component.controller.VideoGestureListener;
import com.renhui.component.controller.VideoGestureRelativeLayout;
import com.renhui.component.core.PlayerType;
import com.renhui.component.core.VideoMediaPlayer;
import com.renhui.component.render.TextureRenderView;
import com.renhui.component.ui.VideoViewEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VodVideoPlayView extends LinearLayout implements VideoViewEventListener
        , TextureView.SurfaceTextureListener, VideoGestureListener, LifecycleObserver
        , VideoCaptureListener {

    private static final String Log_TAG = VodVideoPlayView.class.getName();

    private final Context mContext;

    private SurfaceTexture mSurfaceTexture;
    private WindowManager windowManager;

    private VideoMediaPlayer videoMediaPlayer;
    private TextureRenderView renderView;
    private PlayerControlView playerControlView;
    private VideoGestureRelativeLayout videoGestureLayer;

    private ShowLoadingLayout showLoadingLayout;
    private ShowChangeLayout showChangeLayout;
    private ShowCompleteLayout showCompleteLayout;
    private ShowErrorLayout showErrorLayout;

    private int videoWidth = 0;
    private int videoHeight = 0;

    private int playMode;
    private String resourcePath;

    private long currentPosition = 0;
    private float currentPlaySpeed = 1f;

    private boolean isPlayFinished = false;

    public VodVideoPlayView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public VodVideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public VodVideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(mContext).inflate(R.layout.view_vod_video_play, this);
        renderView = findViewById(R.id.renderView);
        playerControlView = findViewById(R.id.playControlView);
        videoGestureLayer = findViewById(R.id.videoGestureLayer);
        showLoadingLayout = findViewById(R.id.showLoadingLayout);
        showChangeLayout = findViewById(R.id.showChangeLayout);
        showCompleteLayout = findViewById(R.id.showCompleteLayout);
        showErrorLayout = findViewById(R.id.showErrorLayout);
        videoGestureLayer.setVideoGestureListener(this);
        playerControlView.setVideoCaptureListener(this);
        initPlayer();
        setBackPageListener();
        setVideoFunctionListener();
    }

    private void setBackPageListener() {
        playerControlView.findViewById(R.id.videoBack).setOnClickListener(v -> {
            Activity activity = (Activity) mContext;
            activity.finish();
        });
        showCompleteLayout.findViewById(R.id.completeBack).setOnClickListener(v -> {
            Activity activity = (Activity) mContext;
            activity.finish();
        });
        showErrorLayout.findViewById(R.id.errorBack).setOnClickListener(v -> {
            Activity activity = (Activity) mContext;
            activity.finish();
        });
    }

    private void setVideoFunctionListener() {
        // 播放出错 刷新重试
        showErrorLayout.findViewById(R.id.retryPlay).setOnClickListener(v -> {
            showErrorLayout.setVisibility(GONE);
            onShowLoading();  // 展示加载loading
            videoMediaPlayer.startPlayVideo(resourcePath, PlayerType.RH, playMode);
            videoMediaPlayer.setSpeed(currentPlaySpeed);
            if (currentPosition > 0) {
                videoMediaPlayer.needSeekToPosition(currentPosition);
            }
        });
        // 播放完成 - 重新播放
        showCompleteLayout.findViewById(R.id.vod_repeat_layer).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCompleteLayout.setVisibility(GONE);
                onShowLoading();  // 展示加载loading
                videoMediaPlayer.startPlayVideo(resourcePath, PlayerType.RH, playMode);
                videoMediaPlayer.setSpeed(currentPlaySpeed);
            }
        });
    }

    private void initPlayer() {
        videoMediaPlayer = new VideoMediaPlayer(mContext);
        videoMediaPlayer.setVideoViewEventListener(this);
        renderView.setSurfaceTextureListener(this);
        playerControlView.setVideoMediaPlayer(videoMediaPlayer);
    }

    @Override
    public void onVideoCapture() {
        new Thread(() -> {
            Bitmap videoBitmap = renderView.getBitmap();
            FileOutputStream outputStream = null;
            try {
                File saveFile = new File(mContext.getExternalCacheDir(), "222.png");
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }
                outputStream = new FileOutputStream(saveFile);
                if (!videoBitmap.isRecycled()) {
                    videoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != outputStream) {
                    try {
                        outputStream.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        Toast.makeText(mContext, "截屏成功，请在文件系统查看", Toast.LENGTH_LONG).show();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void ON_RESUME() {
        videoMediaPlayer.ON_RESUME();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void ON_PAUSE() {
        videoMediaPlayer.ON_PAUSE();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void ON_DESTROY() {
        videoMediaPlayer.ON_DESTROY();
    }

    // 视频等比缩放
    private RelativeLayout.LayoutParams getVideoSizeParams() {
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        int vWidth = videoWidth;
        int vHeight = videoHeight;
        if (vWidth == 0) {
            vWidth = 600;
        }
        if (vHeight == 0) {
            vHeight = 400;
        }
        if (vWidth > width || vHeight > height) {
            float wRatio = (float) vWidth / (float) width;
            float hRatio = (float) vHeight / (float) height;
            float ratio = Math.max(wRatio, hRatio);
            width = (int) Math.ceil((float) vWidth / ratio);
            height = (int) Math.ceil((float) vHeight / ratio);
        } else {
            float wRatio = (float) width / (float) vWidth;
            float hRatio = (float) height / (float) vHeight;
            float ratio = Math.min(wRatio, hRatio);
            width = (int) Math.ceil((float) vWidth * ratio);
            height = (int) Math.ceil((float) vHeight * ratio);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture != null) {
            if (videoMediaPlayer.isNeedUpdateSurface) {
                if (mSurfaceTexture.hashCode() == surfaceTexture.hashCode()) {
                    renderView.setSurfaceTexture(mSurfaceTexture);
                    videoMediaPlayer.setSurface(new Surface(mSurfaceTexture));
                } else {
                    mSurfaceTexture = surfaceTexture;
                    videoMediaPlayer.setSurface(new Surface(mSurfaceTexture));
                }
            } else {
                renderView.setSurfaceTexture(mSurfaceTexture);
                videoMediaPlayer.setSurface(new Surface(mSurfaceTexture));
            }
            videoMediaPlayer.isNeedUpdateSurface = false;
        } else {
            mSurfaceTexture = surfaceTexture;
            videoMediaPlayer.setSurface(new Surface(mSurfaceTexture));
        }
    }

    public void setVideoResourceAndPlay(String resourcePath) {
        this.currentPosition = 0;
        this.resourcePath = resourcePath;
        this.playMode = playMode;
        if (showCompleteLayout.getVisibility() == VISIBLE) {
            showCompleteLayout.setVisibility(GONE);
        }
        if (showErrorLayout.getVisibility() == VISIBLE) {
            showErrorLayout.setVisibility(GONE);
        }
        new Thread(() -> {
            onShowLoading();
            videoMediaPlayer.startPlayVideo(resourcePath, PlayerType.RH, playMode);
            playerControlView.setPlayMode(playMode);
        }).start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onBrightnessGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!canRespondGesture()) return;
        Window window = ((Activity) mContext).getWindow();
        float screenBrightness = window.getAttributes().screenBrightness;
        float value = (e1.getY() - e2.getY()) / videoGestureLayer.getHeight() / 2 + screenBrightness;
        if (value < 0) value = 0.01F;
        if (value > 1) value = 1F;
        WindowManager.LayoutParams params = window.getAttributes();
        params.screenBrightness = value;
        window.setAttributes(params);
        showChangeLayout.setChangeShowMode(ShowChangeLayout.CHANGE_BRIGHTNESS_MODE);
        showChangeLayout.setProgress((int) (value * 100));
        showChangeLayout.show();
    }

    @Override
    public void onVolumeGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!canRespondGesture()) return;
        int volumeProgress = videoMediaPlayer.setVolumeBaseOnGesture(videoGestureLayer.getHeight(), e1, e2);
        showChangeLayout.setChangeShowMode(ShowChangeLayout.CHANGE_VOLUME_MODE);
        showChangeLayout.setProgress(volumeProgress);
        showChangeLayout.show();
    }

    @Override
    public void onSingleTapGesture(MotionEvent e) {
        if (!canRespondGesture()) return;
        if (!playerControlView.showing) {
            if (playerControlView.functionLocking) {
                playerControlView.showLocked();
            } else {
                playerControlView.show();
            }
        } else {
            if (playerControlView.functionLocking) {
                playerControlView.hideLocked();
            } else {
                playerControlView.hide();
            }
        }
    }

    @Override
    public void onDoubleTapGesture(MotionEvent e) {
        // 暂时不做，双击 “暂停/播放” 逻辑
    }

    @Override
    public void onDown(MotionEvent e) {
        videoMediaPlayer.getCurrentVolume();
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        videoWidth = width;
        videoHeight = height;
        renderView.post(() -> renderView.setLayoutParams(getVideoSizeParams()));
    }

    @Override
    public void onPrepared() {

    }

    public float getSpeed() {
        return this.currentPlaySpeed;
    }

    public void setSpeed(float speed) {
        this.currentPlaySpeed = speed;
        if (videoMediaPlayer != null) {
            videoMediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public void onPlayerProgress(long currentPosition, long duration) {
        this.currentPosition = currentPosition;
        playerControlView.onPlayerProgress(currentPosition, duration);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        renderView.setLayoutParams(getVideoSizeParams());
    }

    @Override
    public void onPlayStatus(boolean isPlaying) {
        playerControlView.onPlayStatus(isPlaying);
    }

    @Override
    public void onInfo(int what) {
        // int MEDIA_INFO_VIDEO_RENDERING_START = 3;
        // int MEDIA_INFO_AUDIO_RENDERING_START  = 10002;
        Log.e(Log_TAG, "Player OnInfo =" + what);
        if (what == 3 || what == 10002) {
            isPlayFinished = false;
            onHideLoading();
        }
    }

    @Override
    public void onShowLoading() {
        showLoadingLayout.post(() -> showLoadingLayout.setVisibility(View.VISIBLE));
    }

    @Override
    public void onHideLoading() {
        showLoadingLayout.post(() -> showLoadingLayout.setVisibility(View.GONE));
    }

    @Override
    public void onPlayComplete() {
        new Thread(() -> {
            // 如果当前播放结束回调并非正常时间端
            if (currentPosition + 5 * 1000 < videoMediaPlayer.getDuration()) {
                post(() -> {
                    Log.e(Log_TAG, "无网络，回调的播放完成！");
                    playerControlView.hide();
                    showChangeLayout.setVisibility(GONE);
                    showCompleteLayout.setVisibility(GONE);
                    showErrorLayout.setVideoErrorType(ShowErrorLayout.VIDEO_ERROR_LOAD_FAILED);
                    showErrorLayout.setVisibility(VISIBLE);
                });
                return;
            }
            Log.e(Log_TAG, "播放完成！");
            post(() -> {
                playerControlView.hide();
                showChangeLayout.setVisibility(GONE);
                showErrorLayout.setVisibility(GONE);
                showCompleteLayout.setVisibility(VISIBLE);
            });
        }).start();
        isPlayFinished = true;
    }

    @Override
    public void onPlayError(int what, int extra) {
        onHideLoading();
        if (what == -10000) {
            showErrorLayout.post(() -> {
                playerControlView.hide();
                showErrorLayout.setVideoErrorType(ShowErrorLayout.VIDEO_ERROR_LOAD_FAILED);
                showErrorLayout.setVisibility(VISIBLE);
            });
        }
    }


    public void release() {
        if (videoMediaPlayer != null) {
            videoMediaPlayer.release();
        }
    }

    // 是否可以响应手势
    private boolean canRespondGesture() {
        return showCompleteLayout.getVisibility() == View.GONE && showErrorLayout.getVisibility() == View.GONE && !playerControlView.functionLocking;
    }

}
