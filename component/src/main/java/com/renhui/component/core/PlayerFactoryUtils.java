package com.renhui.component.core;

import android.content.Context;

import com.renhui.component.core.ijkplayer.IjkPlayerFactory;
import com.renhui.component.core.rhplayer.RHPlayerFactory;
import com.renhui.mediaplayer.base.AbstractVideoPlayer;


public class PlayerFactoryUtils {

    public static AbstractVideoPlayer getVideoPlayer(Context context, int playerType) {
        if (playerType == PlayerType.IJK) {
            return IjkPlayerFactory.create().createPlayer(context);
        }  else if (playerType == PlayerType.RH){
            return RHPlayerFactory.create().createPlayer(context);
        } else {
            return IjkPlayerFactory.create().createPlayer(context);
        }
    }
}
