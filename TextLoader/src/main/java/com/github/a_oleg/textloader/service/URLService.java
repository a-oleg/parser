package com.github.a_oleg.textloader.service;

import com.github.a_oleg.textloader.models.URL;
import com.github.a_oleg.textloader.repository.URLRepository;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@Service
public class URLService {
    private final URLRepository URLRepository;

    @Autowired
    public URLService(URLRepository URLRepository) {
        this.URLRepository = URLRepository;
    }

    /**Метод, формирующий объект URL*/
    public ArrayList<URL> createUrl(ArrayList<String> urls) {
        ArrayList<URL> urlModels = new ArrayList<>();
        for(String url : urls) {
            urlModels.add(new URL(url));
        }
        return urlModels;
    }

    /**Метод, парсящий данные сайтов*/
    public ArrayList<URL> parseURL(ArrayList<URL> urls) {
        //HashMap<String, String> urlsAndTexts = new HashMap<>();
        ArrayList<URL> urlsAndTexts = new ArrayList<>();
        Document htmlDocument = null;

        for(URL url : urls) {
            try {
                htmlDocument = Jsoup.connect(url.getUrl()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String bodyTagText = htmlDocument.body().text();
            urlsAndTexts.add(new URL(url.getUrl(), bodyTagText));
        }
        return urlsAndTexts;
    }

    /**Метод, помещающий URL и контент в БД*/
    public void insertIntoDatabase(ArrayList<URL> urls) {
        try {
            URLRepository.insertingDataIntoTables(urls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**Метод, создающий файл для выгрузки пользователю*/
    public File createFileForData(ArrayList<URL> urls) {
        String filename = "ContentFromTextloader.xls";
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Content");

        if(urls == null) {
            HSSFRow nullRow = sheet.createRow((short)0);
            nullRow.createCell(0).setCellValue("You didn't specify any url. Paste the url into the" +
                    "\"Links\" field and click \"Parse url\"");
        } else {
            HSSFRow rowhead = sheet.createRow((short)0);
            rowhead.createCell(0).setCellValue("Url");
            rowhead.createCell(1).setCellValue("Content");

            HSSFRow row;
            int countEntry = 1;
            for (URL url : urls) {
                row = sheet.createRow((short)countEntry);
                row.createCell(0).setCellValue(url.getUrl());
                String content = url.getContent();
                //В ячейке xls-файла существует ограничение на 32767 символов, поэтому "обрезаю" строку
                if(content.length() < 32767) {
                    row.createCell(1).setCellValue(content);
                } else {
                    row.createCell(1).setCellValue(content.substring(0, 32765));
                }
                countEntry++;
            }
        }

        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(filename);
        return file;
    }
}
