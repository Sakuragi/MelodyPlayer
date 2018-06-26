package com.jim.melodyplayer.net;

/**
 * Created by Jim on 2018/6/25.
 */
public class SongListRequest {
    public String method="baidu.ting.billboard.billList";
    public String type="1";
    public String size="10";
    public String offset="0";

    public SongListRequest(){}

    public SongListRequest(String method, String type, String size, String offset) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
}
