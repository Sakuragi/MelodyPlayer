package com.jim.melodyplayer.player;

import android.util.Base64;


import com.jim.melodyplayer.utils.LogUtil;
import com.jim.melodyplayer.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Jim on 2018/5/15 0015.
 */

public class FileCache implements Cache {

    public static String TEMP=".temp";

    public File file;
    private RandomAccessFile dataFile;

    private FileCache(String url){
        LogUtil.d("cache path: "+cachePath);
        File file=new File(cachePath+Base64.encodeToString(url.getBytes(),Base64.NO_WRAP));
        File tempFile=new File(cachePath+Base64.encodeToString(url.getBytes(),Base64.NO_WRAP)+TEMP);
        LogUtil.d("temp file name: "+tempFile.getName());
        if (!file.exists()&&tempFile.exists()){
           this.file=tempFile;
            LogUtil.d("temp file is exist");
        }else if (!file.exists()){
            try {
                tempFile.getParentFile().mkdirs();
                boolean isCreated=tempFile.createNewFile();
                if (isCreated){
                    LogUtil.d("create tempfile");
                    this.file=tempFile;
                }
            } catch (IOException e) {
                LogUtil.e(e.toString());
            }
        }else {
            this.file=file;
            LogUtil.d("completed file is exist");
        }
        createDataFile();
    }

    private void createDataFile(){
        LogUtil.e("createDataFile");
        try {
            dataFile=new RandomAccessFile(file,"rw");
        } catch (FileNotFoundException e) {
            LogUtil.e("File not found");
        }
    }

    public static FileCache open(String url){
        return new FileCache(url);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return dataFile.read(b);
    }

    @Override
    public int read(byte[] b, long off, int len) throws IOException {
        dataFile.seek(off);
        return dataFile.read(b,0,len);
    }

    @Override
    public long available() throws IOException {
        return dataFile.length();
    }

    @Override
    public void close() {
        Util.closeCloseableQuietly(dataFile);
    }

    @Override
    public void append(byte[] data, int length) throws IOException {
        dataFile.seek(available());
        dataFile.write(data,0,length);
    }

    @Override
    public boolean isCompleted() {
        return !isTempFile(file);
    }

    @Override
    public void finishedCache() {
        if (isCompleted()){
            LogUtil.d("file was completed");
            return;
        }
        close();
        String fileName = file.getName().substring(0, file.getName().length() - TEMP.length());
        File completedFile = new File(file.getParentFile(), fileName);
        boolean renamed = file.renameTo(completedFile);
        if (!renamed){
            LogUtil.d("rename temp file failed");
        }
        file=completedFile;
        try {
            dataFile=new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            LogUtil.e(e.toString());
        }
    }

    private boolean isTempFile(File file) {
        return file.getName().endsWith(TEMP);
    }
}
