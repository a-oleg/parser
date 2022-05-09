package com.github.a_oleg.textloader.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class Request {
    private int id;
    private LocalDateTime timeDownload;
    private  ArrayList <URL> arrayURL;

    public Request() {
    }
}
