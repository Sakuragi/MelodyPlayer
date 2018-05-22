package com.jim.MelodyPlayer.player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim on 2018/3/22 0022.
 */

public class AudioBean implements Parcelable {


    protected AudioBean(Parcel in) {

    }

    public static final Creator<AudioBean> CREATOR = new Creator<AudioBean>() {
        @Override
        public AudioBean createFromParcel(Parcel in) {
            return new AudioBean(in);
        }

        @Override
        public AudioBean[] newArray(int size) {
            return new AudioBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
