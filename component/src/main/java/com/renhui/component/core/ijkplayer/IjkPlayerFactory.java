package com.renhui.component.core.ijkplayer;

import android.content.Context;

import com.renhui.component.core.factory.PlayerFactory;

public class IjkPlayerFactory extends PlayerFactory<IjkVideoPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkVideoPlayer createPlayer(Context context) {
        return new IjkVideoPlayer(context);
    }
}
