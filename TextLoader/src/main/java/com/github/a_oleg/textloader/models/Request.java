package com.github.a_oleg.textloader.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

//    public Request(int id, LocalDateTime timeDownload, ArrayList<URL> arrayURL) {
//        this.id = id;
//        this.timeDownload = timeDownload;
//        this.arrayURL = arrayURL;
//    }

//    public LocalDateTime getTimeDownload() {
//        return timeDownload;
//    }
//
//    public ArrayList<URL> getArrayURL() {
//        return arrayURL;
//    }


}
