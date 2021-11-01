package com.renhui.component.core;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.lifecycle.LifecycleObserver;

import com.renhui.component.ui.VideoViewEventListener;
import com.renhui.mediaplayer.base.AbstractVideoPlayer;
import com.renhui.mediaplayer.base.VideoPlayerEventListener;
import com.renhui.mediaplayer.utils.GlobalConfig;

import java.util.Timer;
import java.util.TimerTask;

public class VideoMediaPlayer implements VideoPlayerEventListener, LifecycleObserver {

    private Context context;
    private Surface surface;
    private AudioManager audioManager;
    private AbstractVideoPlayer mediaPlayer;

    private int maxVolume = 0;
    private int oldVolume = 0;

    private boolean needSeek = false;
    private long needSeekPosition = 0L;
    private boolean isPrepared = false;
    private boolean isPauseByPerson = false;

    private Timer updateProgressTimer;
    private TimerTask updateProgressTimerTask;

    private VideoViewEventListener videoViewEventListener;

    public VideoMediaPlayer(Context context) {
        this.context = context;
        initAudioManager();
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(surface);
        }
    }

    public void setVideoViewEventListener(VideoViewEventListener videoViewEventListener) {
        this.videoViewEventListener = videoViewEventListener;
    }

    private void initAudioManager() {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public void startPlayVideo(String path, int playerType, int playMode) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlayable()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = PlayerFactoryUtils.getVideoPlayer(context, playerType);
        mediaPlayer.initPlayer();
        mediaPlayer.setPlayMode(playMode);
        mediaPlayer.videoPlayerEventListener = this;
        isPrepared = false;
        setDataSource(path);
        if (surface != null) {
            mediaPlayer.setSurface(surface);
        }
        mediaPlayer.prepareAsync();
    }

    private void setDataSource(String playUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.setDataSource(playUrl);
        }
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        isPauseByPerson = false;
    }

    public float getSpeed() {
        if (mediaPlayer == null) {
            return 1f;
        } else {
            return mediaPlayer.getSpeed();
        }
    }

    public void setSpeed(float speed) {
        if(mediaPlayer != null) {
            mediaPlayer.setSpeed(speed);
        }
    }

    public void needSeekToPosition(long position) {
        needSeek = true;
        needSeekPosition = position;
    }

    public void seekTo(long position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public void pause() {
        isPauseByPerson = true;
        if (mediaPlayer != null) {
            Log.e(GlobalConfig.LOG_TAG,"暂停播放");
            mediaPlayer.pause();
        }
    }

    public boolean isPlayable() {
        if (mediaPlayer == null) {
            return false;
        } else {
            return mediaPlayer.isPlayable();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) {
            return false;
        } else {
            return mediaPlayer.isPlaying();
        }
    }

    public long getDuration() {
        if (mediaPlayer == null) {
            return 0;
        } else {
            return mediaPlayer.getDuration();
        }
    }

    public void ON_RESUME() {
        if (surface != null) {
            if (mediaPlayer != null) {
                mediaPlayer.setSurface(surface);
            }
        }
        if (isPauseByPerson) {
            return;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlayable() && isPrepared) {
                mediaPlayer.start();
            }
        }
    }

    public boolean isNeedUpdateSurface = false;

    public void ON_PAUSE() {
        if (isPauseByPerson) {
            isNeedUpdateSurface = true;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
            return;
        }
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    public void ON_DESTROY() {
        release();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /************************* 音量控制模块 ***************************/

    public void getCurrentVolume() {
        oldVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // 根据手势设置音量
    public int setVolumeBaseOnGesture(int layoutHeight, MotionEvent e1, MotionEvent e2) {
        int value = layoutHeight / maxVolume;
        int newVolume = (int) ((e1.getY() - e2.getY()) / value + oldVolume);
        if (newVolume < 0) newVolume = 0;
        if (newVolume > maxVolume) newVolume = maxVolume;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);
        //要强行转Float类型才能算出小数点，不然结果一直为0
        return (int) (newVolume / (float) maxVolume * 100);
    }


    @Override
    public void onError(int type, int extra) {
        if (videoViewEventListener != null) {
            videoViewEventListener.onPlayError(type, extra);
        }
    }

    @Override
    public void onCompletion() {
        if (videoViewEventListener != null) {
            videoViewEventListener.onPlayComplete();
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        if (videoViewEventListener != null) {
            videoViewEventListener.onInfo(what);
        }
    }

    @Override
    public void onBufferStart() {
        if (videoViewEventListener != null) {
            videoViewEventListener.onShowLoading();
        }
    }

    @Override
    public void onBufferEnd() {
        if (videoViewEventListener != null) {
            videoViewEventListener.onHideLoading();
        }
    }

    @Override
    public void onPrepared() {
        isPrepared = true;
        if(mediaPlayer != null) {
            mediaPlayer.start();
            if (needSeek) {
                mediaPlayer.seekTo(needSeekPosition);
            }
        }
        startUpdateProgressTimer();
        isPauseByPerson = false;
        needSeek = false;
        if (videoViewEventListener != null) {
            videoViewEventListener.onPrepared();
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        if (videoViewEventListener != null) {
            videoViewEventListener.onVideoSizeChanged(width, height);
        }
    }


    /************************* 播放进度控制模块 *********************************/

    void updateProgress() {
        long currentPosition = mediaPlayer == null ? 0 : mediaPlayer.getCurrentPosition();
        long duration = mediaPlayer == null ? 0 :mediaPlayer.getDuration();
        if (videoViewEventListener != null) {
            videoViewEventListener.onPlayerProgress(currentPosition, duration);
        }
    }

    void updateVideoPlayStatus() {
        if (videoViewEventListener != null) {
            videoViewEventListener.onPlayStatus(mediaPlayer != null && mediaPlayer.isPlaying());
        }
    }

    private void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (updateProgressTimer == null) {
            updateProgressTimer = new Timer();
        }
        if (updateProgressTimerTask == null) {
            updateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    updateProgress();
                    updateVideoPlayStatus();
                }
            };
        }
        updateProgressTimer.schedule(updateProgressTimerTask, 0, 1000);
    }

    private void cancelUpdateProgressTimer() {
        if (updateProgressTimerTask != null){
            updateProgressTimerTask.cancel();
            updateProgressTimerTask = null;
        }
        if (updateProgressTimer != null) {
            updateProgressTimer.cancel();
            updateProgressTimer = null;
        }
    }

    public void requestVideoImageCapture() {
        if (mediaPlayer != null) {
            mediaPlayer.requestVideoImageCapture();
        }
    }
}
