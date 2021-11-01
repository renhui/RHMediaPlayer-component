package com.renhui.component.core.factory;

import android.content.Context;

import com.renhui.mediaplayer.base.AbstractVideoPlayer;

public abstract class PlayerFactory<T extends AbstractVideoPlayer> {

    /**
     * 创建具体内核的播放器
     */
    public abstract T createPlayer(Context context);

}
