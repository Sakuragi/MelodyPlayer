package com.jim.melodyplayer.base;

public class FM {
    public int id;
    public String name;
    public String author;
    public String image_url;
    public String hits;

    @Override
    public String toString() {
        return "FM{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", image_url='" + image_url + '\'' +
                ", hits='" + hits + '\'' +
                '}';
    }
}