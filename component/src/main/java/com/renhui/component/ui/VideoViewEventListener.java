package com.renhui.component.ui;

public interface VideoViewEventListener {
    
    void onVideoSizeChanged(int width, int height);

    void onPlayerProgress(long currentPosition, long duration);

    void onPrepared();

    void onPlayStatus(boolean isPlaying);

    void onInfo(int what);

    void onShowLoading();

    void onHideLoading();

    void onPlayComplete();

    void onPlayError(int what, int extra);

}
