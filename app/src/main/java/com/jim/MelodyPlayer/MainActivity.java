package com.jim.MelodyPlayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jim.MelodyPlayer.base.BaseViewHolder;
import com.jim.MelodyPlayer.base.CommonAdapter;
import com.jim.MelodyPlayer.base.FM;
import com.jim.MelodyPlayer.player.AudioPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private RecyclerView mRecyclerView;
    private CommonAdapter<FM> mAdapter;
    private ArrayList<FM> datas;
    private String testUrl="https://video.ydlcdn.com/2018/01/25/786b3e1640569ac1379505fdb0f8d1a8.mp3";
    public static final int INIT_SEEK_BAR=1;
    public static final int UPDATE_SEEK_BAR=2;

    private TextView mTvCurrentTime;
    private SeekBar mSeekBar;
    private TextView mTotalTime;
    private ImageView ivPre;
    private ImageView ivNext;
    private ImageView ivPlay;
    private AudioPlayer mAudioPlayer=new AudioPlayer();

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case INIT_SEEK_BAR:
                    mSeekBar.setMax(msg.arg1);
//                    mTotalTime
                    break;
            }
        }
    };

    private void initViews(){
        mTvCurrentTime=findViewById(R.id.tv_current_time);
        mSeekBar=findViewById(R.id.sb_progress);
        mTotalTime=findViewById(R.id.tv_total_time);
        ivPre=findViewById(R.id.iv_prev);
        ivPlay=findViewById(R.id.iv_play);
        ivNext=findViewById(R.id.iv_next);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
