package com.github.a_oleg.textloader.service;

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
import java.util.HashMap;
import java.util.Map;

@Service
public class URLService {
    private final URLRepository URLRepository;

    @Autowired
    public URLService(URLRepository URLRepository) {
        this.URLRepository = URLRepository;
    }

    /**Метод, парсящий данные сайтов*/
    public HashMap<String, String> parseURL(ArrayList<String> urls) {
        HashMap<String, String> urlsAndTexts = new HashMap<>();
        Document htmlDocument = null;

        for(String urlForParsing : urls) {
            try {
                htmlDocument = Jsoup.connect(urlForParsing).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String bodyTagText = htmlDocument.body().text();
            urlsAndTexts.put(urlForParsing, bodyTagText);
        }
        return urlsAndTexts;
    }

    /**Метод, помещающий URL и контент в БД*/
    public void insertIntoDatabase(HashMap<String, String> urlAndText) {
        try {
            URLRepository.insertingDataIntoTables(urlAndText);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**Метод, создающий файл для выгрузки пользователю*/
    public File createFileForData(HashMap<String, String> urlAndText) {
        String filename = "ContentFromTextloader.xls";

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Content");

        HSSFRow rowhead = sheet.createRow((short)0);
        rowhead.createCell(0).setCellValue("Url");
        rowhead.createCell(1).setCellValue("Content");

        HSSFRow row;
        int countEntry = 1;
        for (Map.Entry entry: urlAndText.entrySet()) {
            row = sheet.createRow((short)countEntry);
            row.createCell(0).setCellValue((String)entry.getKey());
            //Сделать проверку, что срока не более 32767 и отбросить более, чем 32 тыс
            //row.createCell(1).setCellValue((String)entry.getValue());
            String content = (String)entry.getValue();
            //В ячейке xls-файла существует ограничение на 32767 символов, поэтому "обрезаю" строку
            if(content.length() < 32767) {
                row.createCell(1).setCellValue(content);
            } else {
                row.createCell(1).setCellValue(content.substring(0, 32765));
            }
            countEntry++;
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
