package com.github.a_oleg.textloader.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
public class URL {
    private int id;
    private String url;
    private String content;

//    @Autowired
//    public URL(int id, String url, String content) {
//        this.id = id;
//        this.url = url;
//        this.content = content;
//    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
