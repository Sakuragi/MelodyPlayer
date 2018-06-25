package com.jim.melodyplayer.net;

/**
 * Created by Jim on 2018/6/25.
 */
public class SongListRequest {
    public String method="baidu.ting.billboard.billList";
    public int type=1;
    public int size=10;
    public int offset=0;

    public SongListRequest(String method, int type, int size, int offset) {
        this.method = method;
        this.type = type;
        this.size = size;
        this.offset = offset;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
