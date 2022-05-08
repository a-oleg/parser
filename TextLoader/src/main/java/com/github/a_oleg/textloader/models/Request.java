package com.github.a_oleg.textloader.models;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class Request {
    public Request(int id, LocalDateTime timeDownload, ArrayList<URL> arrayURL) {
        this.arrayURL = arrayURL;
        this.id = id;
        this.timeDownload = timeDownload;
    }

    public ArrayList<URL> getArrayURL() {
        return arrayURL;
    }

    public LocalDateTime getTimeDownload() {
        return timeDownload;
    }

    private  ArrayList <URL> arrayURL;
    private int id;
    private LocalDateTime timeDownload;


}
