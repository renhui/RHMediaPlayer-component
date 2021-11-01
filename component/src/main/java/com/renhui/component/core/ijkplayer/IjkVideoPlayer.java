package com.renhui.component.core.ijkplayer;

import android.content.Context;
import android.util.Log;
import android.view.Surface;

import com.renhui.component.core.PlayerErrorType;
import com.renhui.mediaplayer.base.AbstractVideoPlayer;
import com.renhui.mediaplayer.utils.GlobalConfig;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkVideoPlayer extends AbstractVideoPlayer {

    public IjkMediaPlayer ijkMediaPlayer;

    public Context context;

    public IjkVideoPlayer(Context context) {
        this.context = context;
    }

    @Override
    public void initPlayer() {
        ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1L);
        // 试验参数 设置
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        initListener();
    }

    private void initListener() {
        // 设置监听，可以查看ijk中的IMediaPlayer源码监听事件
        // 设置视频错误监听器
        ijkMediaPlayer.setOnErrorListener(onErrorListener);
        // 设置视频播放完成监听事件
        ijkMediaPlayer.setOnCompletionListener(onCompletionListener);
        // 设置视频信息监听器
        ijkMediaPlayer.setOnInfoListener(onInfoListener);
        // 设置视频缓冲更新监听事件
        ijkMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        // 设置准备视频播放监听事件
        ijkMediaPlayer.setOnPreparedListener(onPreparedListener);
        // 设置视频大小更改监听器
        ijkMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        // 设置视频seek完成监听事件
        ijkMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        // 设置时间文本监听器
        ijkMediaPlayer.setOnTimedTextListener(onTimedTextListener);
    }

    @Override
    public void setDataSource(String path) {
        if (ijkMediaPlayer != null) {
            try {
                ijkMediaPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setPlayMode(int playMode) {

    }

    @Override
    public void setSurface(Surface surface) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setSurface(surface);
        }
    }

    @Override
    public void setSurfaceSize(int width, int height) {

    }

    @Override
    public void prepareAsync() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.prepareAsync();
        }
    }

    @Override
    public void start() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.stop();
        }
    }

    @Override
    public void reset() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.reset();
        }
    }

    @Override
    public boolean isPlaying() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isPlayable() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.isPlayable();
        }
        return false;
    }

    @Override
    public void seekTo(long time) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.seekTo(time);
        }
    }

    @Override
    public void release() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setOnErrorListener(null);
            ijkMediaPlayer.setOnCompletionListener(null);
            ijkMediaPlayer.setOnInfoListener(null);
            ijkMediaPlayer.setOnBufferingUpdateListener(null);
            ijkMediaPlayer.setOnPreparedListener(null);
            ijkMediaPlayer.setOnVideoSizeChangedListener(null);
            new Thread(() -> ijkMediaPlayer.release());
        }
    }

    @Override
    public long getCurrentPosition() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public void setSpeed(float speed) {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.setSpeed(speed);
        }
    }

    @Override
    public float getSpeed() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.getSpeed(1f);
        }
        return 1f;
    }

    /**
     * 设置视频播放错误监听事件
     */
    private final IMediaPlayer.OnErrorListener onErrorListener = (iMediaPlayer, what, extra) -> {
        if (what == -10000) {
            if (videoPlayerEventListener != null) {
                videoPlayerEventListener.onError(PlayerErrorType.MEDIA_ERROR_IJK_PLAYER, extra);
            }
        }
        return false;
    };

    /**
     * 设置视频缓冲更新监听事件
     */
    private final IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = (iMediaPlayer, i) -> {

    };

    /**
     * 设置准备视频播放监听事件
     */
    private final IMediaPlayer.OnPreparedListener onPreparedListener = iMediaPlayer -> {
        if (videoPlayerEventListener != null) {
            videoPlayerEventListener.onPrepared();
        }
    };

    /**
     * 设置视频大小更改监听器
     */
    private final IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = (iMediaPlayer, width, height, sar_num, sar_den) -> {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            if (videoPlayerEventListener != null) {
                videoPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
            }
        }
    };

    /**
     * 设置视频播放完成监听事件
     */
    private final IMediaPlayer.OnCompletionListener onCompletionListener = iMediaPlayer -> {
        if (videoPlayerEventListener != null) {
            videoPlayerEventListener.onCompletion();
        }
    };

    /**
     * 设置视频信息监听器
     */
    private final IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (videoPlayerEventListener != null) {
                    videoPlayerEventListener.onBufferStart();
                }
            }
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                if (videoPlayerEventListener != null) {
                    videoPlayerEventListener.onBufferEnd();
                }
            }
            if (videoPlayerEventListener != null) {
                videoPlayerEventListener.onInfo(what, extra);
            }
            return true;
        }
    };

    /**
     * 设置时间文本监听器
     */
    private final IMediaPlayer.OnTimedTextListener onTimedTextListener = (iMediaPlayer, ijkTimedText) -> {

    };

    /**
     * 设置视频seek完成监听事件
     */
    private final IMediaPlayer.OnSeekCompleteListener onSeekCompleteListener = iMediaPlayer -> {

    };

    @Override
    public void requestVideoImageCapture() {
        Log.e(GlobalConfig.LOG_TAG, "IjkPlayer Not Support!");
    }
}
