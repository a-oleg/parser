package com.github.a_oleg.textloader.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class URL {
    private int id;
    private String url;
    private String content;

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public URL(String url) {
        this.url = url;
    }

    public URL(String url, String content) {
        this.url = url;
        this.content = content;
    }
}
