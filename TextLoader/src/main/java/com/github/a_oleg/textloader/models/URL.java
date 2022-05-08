package com.github.a_oleg.textloader.models;

import org.springframework.stereotype.Component;

@Component
public class URL {

    public URL(int id, String url, String content) {
        this.id = id;
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    private int id;
    private String url;
    private String content;
}
