package com.renhui.component.core.rhplayer;

import android.content.Context;

import com.renhui.component.core.factory.PlayerFactory;

public class RHPlayerFactory extends PlayerFactory<RHVideoPlayer> {

    public static RHPlayerFactory create() {
        return new RHPlayerFactory();
    }

    @Override
    public RHVideoPlayer createPlayer(Context context) {
        return new RHVideoPlayer(context);
    }
}
