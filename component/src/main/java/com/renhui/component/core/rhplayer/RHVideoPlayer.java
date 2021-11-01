package com.renhui.component.core.rhplayer;

import android.content.Context;
import android.view.Surface;

import com.renhui.mediaplayer.FFmpegPlayer;
import com.renhui.mediaplayer.base.AbstractVideoPlayer;
import com.renhui.mediaplayer.listener.OnCompleteListener;
import com.renhui.mediaplayer.listener.OnPreparedListener;
import com.renhui.mediaplayer.listener.OnVideoPlayInfoListener;
import com.renhui.mediaplayer.listener.OnVideoSizeParamListener;

public class RHVideoPlayer extends AbstractVideoPlayer implements OnVideoPlayInfoListener, OnPreparedListener, OnCompleteListener, OnVideoSizeParamListener {

    private Context context;

    private FFmpegPlayer player;

    public RHVideoPlayer(Context context) {
        this.context = context;
    }

    public void initPlayer() {
        player = new FFmpegPlayer();
        player.setPreparedListener(this);
        player.setVideoSizeParamListener(this);
        player.setOnCompleteListener(this);
        player.setVideoPlayInfoListener(this);
    }

    public void setDataSource(String path) {
        if (player != null) {
            player.setDataSource(path);
        }
    }

    @Override
    public void setPlayMode(int playMode) {

    }

    public void setSurface(Surface surface) {
        if (player != null) {
            player.setSurface(surface);
        }
    }

    @Override
    public void setSurfaceSize(int width, int height) {

    }

    public void prepareAsync() {
        if (player != null) {
            player.prepareAsync();
        }
    }

    public void start() {
        if (player != null) {
            player.start();
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isPlaying() {
        if (player == null) {
            return false;
        }
        return player.isPlaying();
    }

    @Override
    public boolean isPlayable() {
        if (player == null) {
            return false;
        }
        return player.isPlayable();
    }

    @Override
    public void seekTo(long time) {
        if (player != null) {
            player.seek((int) time);
        }
    }

    public void release() {
        if (player != null) {
            player.release();
        }
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return player.getDuration();
    }

    @Override
    public void setSpeed(float speed) {
        if (player != null) {
            player.setSpeed(speed);
        }
    }

    @Override
    public float getSpeed() {
        if (player == null) {
            return 1;
        }
        return player.getSpeed();
    }

    @Override
    public void onPrepared() {
        if (videoPlayerEventListener != null) {
            videoPlayerEventListener.onPrepared();
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        if (videoPlayerEventListener != null) {
            videoPlayerEventListener.onInfo(what, extra);
        }
    }

    @Override
    public void onVideoSizeChange(int width, int height) {
        if (videoPlayerEventListener != null) {
            videoPlayerEventListener.onVideoSizeChanged(width, height);
        }
    }

    @Override
    public void onComplete() {
        if (videoPlayerEventListener != null) {
            videoPlayerEventListener.onCompletion();
        }
    }

    @Override
    public void requestVideoImageCapture() {
        if (player != null) {
            player.requestVideoImageCapture();
        }
    }
}
