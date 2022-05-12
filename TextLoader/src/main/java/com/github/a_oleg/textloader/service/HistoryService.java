package com.github.a_oleg.textloader.service;

import com.github.a_oleg.textloader.models.Request;
import com.github.a_oleg.textloader.models.URL;
import com.github.a_oleg.textloader.repository.HistoryRepository;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class HistoryService {
    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**Метод, формирующий коллекцию объектов Request и соответствующих им URL*/
    public ArrayList<Request> createRequestsCollection() {
        ArrayList<Request> lastFiveRequest = new ArrayList<>();
        ArrayList<Integer> idRequests = historyRepository.selectIdLastFiveRequest();
        for(int idRequest : idRequests) {
            Request request = createRequest(idRequest);
            lastFiveRequest.add(request);
        }
        return lastFiveRequest;
    }

    /**Метод, формирующий объект Request*/
    public Request createRequest(int numberRequest) {
        int id = -1;
        LocalDateTime timeDownload = null;
        ArrayList <URL> arrayURL = new ArrayList<>();

        HashMap<Integer, LocalDateTime> idAndTimeRequest = historyRepository.selectIdAndTimeRequest(numberRequest);

        for (Map.Entry<Integer, LocalDateTime> entryRequest: idAndTimeRequest.entrySet()) {
            id = entryRequest.getKey();
            timeDownload = entryRequest.getValue();

            HashMap<Integer, Integer> idUrlForRequest = historyRepository.selectIdUrlsForRequest(entryRequest.getKey());
            for (Map.Entry<Integer, Integer> entryUrl: idUrlForRequest.entrySet()) {
                URL url = createURL(entryUrl.getValue());
                arrayURL.add(url);
            }
        }
        Request requestModel = new Request(id, timeDownload, arrayURL);
        return requestModel;
    }

    /**Метод, формирующий объект URl*/
    public URL createURL(int idUrl) {
        String url = null;
        String content = null;
        HashMap<String, String> urlAndContent = historyRepository.selectUrlAndContent(idUrl);
        for (Map.Entry<String, String> entry: urlAndContent.entrySet()) {
            url = entry.getKey();
            content = entry.getValue();
        }
        URL urlModel = new URL(idUrl, url, content);
        return urlModel;
    }

    /**Метод, создающий файл для выгрузки пользователю*/
    public File createFileForData(ArrayList<Request> requests) {
        String filename = "HistoryContentFromTextloader.xls";
        HSSFWorkbook workbook = new HSSFWorkbook();

        if (requests.size() == 0) {
            HSSFSheet sheet = workbook.createSheet("Request");
            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell(0).setCellValue("There is no information on requests in the database");
        } else {
            int countRequests = 0;
            for (int i = 0; i < requests.size(); i++) {
                Request rqst = requests.get(i);
                ArrayList<URL> urls = rqst.getArrayURL();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd-yyyy HH.mm");
                String dateAndTime = dtf.format(rqst.getTimeDownload());

                HSSFSheet sheet = workbook.createSheet(++countRequests + " request " + " " + dateAndTime);

                HSSFRow rowhead = sheet.createRow((short)0);
                rowhead.createCell(0).setCellValue("Url");
                rowhead.createCell(1).setCellValue("Content");

                HSSFRow row;
                int countRow = 1;
                for (URL url : urls) {
                    row = sheet.createRow((short) countRow);
                    row.createCell(0).setCellValue(url.getUrl());
                    String content = url.getContent();
                    //В ячейке xls-файла существует ограничение на 32767 символов, поэтому "обрезаю" строку
                    if (content.length() < 32767) {
                        row.createCell(1).setCellValue(content);
                    } else {
                        row.createCell(1).setCellValue(content.substring(0, 32765));
                    }
                    countRow++;
                }
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

//    /**Метод, создающий файл для выгрузки пользователю*/
//    public File createFileForData(ArrayList<Request> requests) {
//        String filename = "HistoryContentFromTextloader.xls";
//        HSSFWorkbook workbook = new HSSFWorkbook();
//
//        if (requests.size() == 0) {
//            HSSFSheet sheet = workbook.createSheet("Request");
//            HSSFRow rowhead = sheet.createRow((short) 0);
//            rowhead.createCell(0).setCellValue("There is no information on requests in the database");
//        } else {
//            int countRequests = 0;
//            for (int i = 0; i < requests.size(); i++) {
//                Request rqst = requests.get(i);
//                ArrayList<URL> urls = rqst.getArrayURL();
//                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd-yyyy HH.mm");
//                String dateAndTime = dtf.format(rqst.getTimeDownload());
//
//                HSSFSheet sheet = workbook.createSheet(++countRequests + " request " + " " + dateAndTime);
//
//                HSSFRow rowhead = sheet.createRow((short)0);
//                rowhead.createCell(0).setCellValue("Url");
//                rowhead.createCell(1).setCellValue("Content");
//
//                HSSFRow row;
//                int countRow = 1;
//                for (URL url : urls) {
//                    row = sheet.createRow((short) countRow);
//                    row.createCell(0).setCellValue(url.getUrl());
//                    String content = url.getContent();
//                    //В ячейке xls-файла существует ограничение на 32767 символов, поэтому "обрезаю" строку
//                    if (content.length() < 32767) {
//                        row.createCell(1).setCellValue(content);
//                    } else {
//                        row.createCell(1).setCellValue(content.substring(0, 32765));
//                    }
//                    countRow++;
//                }
//            }
//        }
//        FileOutputStream fileOut = null;
//        try {
//            fileOut = new FileOutputStream(filename);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            workbook.write(fileOut);
//            fileOut.close();
//            workbook.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        File file = new File(filename);
//        return file;
//    }