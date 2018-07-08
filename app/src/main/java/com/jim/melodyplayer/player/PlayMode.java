package com.jim.melodyplayer.player;

import com.jim.melodyplayer.utils.SharedPreferencesEditor;

/**
 * Created by Jim on 2018/7/7.
 */
public enum PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    private static final String PLAY_MODE_KEY = "play_mode";

    public static PlayMode getCurrentMode() {
        String currentMode = SharedPreferencesEditor.getString(PLAY_MODE_KEY);
        if (currentMode.equalsIgnoreCase(LOOP.toString())) {
            return LOOP;
        } else if (currentMode.equalsIgnoreCase(SINGLE.toString())) {
            return SINGLE;
        } else if (currentMode.equalsIgnoreCase(LIST.toString())) {
            return LIST;
        } else if (currentMode.equalsIgnoreCase(SHUFFLE.toString())) {
            return SHUFFLE;
        } else {
            return defaultMode();
        }
    }

    public static void setPlayMode(PlayMode mode) {
        SharedPreferencesEditor.putString(PLAY_MODE_KEY, mode.name());
    }

    public static PlayMode defaultMode() {
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
