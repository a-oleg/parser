package com.github.a_oleg.textloader.controller;

import com.github.a_oleg.textloader.models.URL;
import com.github.a_oleg.textloader.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;

@Controller
public class URLController {
    private final URLService urlService;

    @Autowired
    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @RequestMapping(value = "/downloadResult", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource parseURL(@RequestParam(name = "URL", required = true) String url, Model model) {
        File file;
        if(url == "") {
            file = urlService.createFileForData(null);
        } else {
            String [] arrayURL = url.split("\r\n");
            ArrayList<String> linesForParse = new ArrayList<>();
            for (String arrayElement : arrayURL) {
                linesForParse.add(arrayElement);
            }
            ArrayList<URL> urls = urlService.createUrl(linesForParse);
            ArrayList<URL> urlsForParse = urlService.parseURL(urls);
            urlService.insertIntoDatabase(urlsForParse);
            file = urlService.createFileForData(urlsForParse);
        }
        return new FileSystemResource(file);

        /*
        //Код, возвращающий содержимое файлв на front-end. Необходим для тестирования приложения
        //The code that returns the contents of the file to the front-end. Required for testing the application
        int countUrls = 0;
        for(String urlAdress : urlsForParse) {
            model.addAttribute("URL" + countUrls, urlAdress);
            model.addAttribute("textPage" + countUrls, urlAndText.get(urlAdress));
            countUrls++;
        }
        return "downloadResult";
         */
    }
}
