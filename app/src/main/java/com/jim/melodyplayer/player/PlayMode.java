package com.jim.melodyplayer.player;

/**
 * Created by Jim on 2018/7/7.
 */
public enum PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    public static PlayMode defaultMode(){
        return LOOP;
    }

    public static PlayMode switchNextMode(PlayMode current) {
        if (current == null) return defaultMode();

        switch (current) {
            case LOOP:
                return LIST;
            case LIST:
                return SHUFFLE;
            case SHUFFLE:
                return SINGLE;
            case SINGLE:
                return LOOP;
        }
        return defaultMode();
    }
}
