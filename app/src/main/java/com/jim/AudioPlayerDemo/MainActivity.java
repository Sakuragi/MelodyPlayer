package com.jim.AudioPlayerDemo;

import android.content.res.AssetManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jim.AudioPlayerDemo.base.BaseViewHolder;
import com.jim.AudioPlayerDemo.base.CommonAdapter;
import com.jim.AudioPlayerDemo.base.FM;
import com.jim.AudioPlayerDemo.player.AudioPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CommonAdapter<FM> mAdapter;
    private ArrayList<FM> datas;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton=findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AudioPlayer player=new AudioPlayer();
                player.setDataSource("http://mp3-cdn.luoo.net/low/luoo/radio889/01.mp3");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        player.play();
                    }
                },5000);
            }
        });
//        initView();
    }

    private void initView() {
        datas=new ArrayList<>();
        InputStream inputStream=null;
        try {
            inputStream=getAssets().open("data.json");
            byte[] buffer=new byte[2048];
            StringBuilder builder=new StringBuilder();
            while (inputStream.read(buffer)>0){
                builder.append(new String(buffer));
            }
            Type listType = new TypeToken<List<FM>>(){}.getType();
            JSONObject jsonObject=new JSONObject(builder.toString());
            JSONArray array=jsonObject.getJSONArray("data");
            Gson gson=new Gson();
            datas=gson.fromJson(new StringReader(array.toString()),listType);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("TAG",datas.toString());
        mAdapter=new CommonAdapter<FM>(this,R.layout.item_fm_list,datas) {
            @Override
            public void convert(BaseViewHolder holder, int position, FM data, int dataState) {
                holder.setImageWithUrl(R.id.sdv_head,data.image_url);
                holder.setText(R.id.tv_title,data.name);
                holder.setText(R.id.tv_anchor,data.author);
                holder.setText(R.id.tv_listen_num,data.hits);
            }
        };
//        mRecyclerView=findViewById(R.id.rcv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }
}
