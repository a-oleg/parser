package com.github.a_oleg.textloader.repository;

import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class HistoryRepository {
    private final DBRepository dbRepository;

    @Autowired
    public HistoryRepository(DBRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    private static final String URL_DB = "jdbc:postgresql://localhost:5432/requestsandurls";
    private static final String URL_POSTGRES = "jdbc:postgresql://localhost:5432/";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection connectionDataBase;

    @PostConstruct
    /**Метод, открывающий БД*/
    public void init() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connectionDataBase = DriverManager.getConnection(URL_DB, USERNAME, PASSWORD);
        } catch (
        PSQLException e) {
            dbRepository.createUrlsDatabase();
        } catch (SQLException e) {
            System.out.println("The database is missing. No requests to upload");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**Метод, формирующий коллекцию из id пяти последних запросов*/
    public ArrayList<Integer> selectIdLastFiveRequest() {
        ArrayList<Integer> lastFiveRequest = new ArrayList<>();
        int idRequest = -1;
        try (Statement stmt = connectionDataBase.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT id_request FROM requests ORDER BY id_request DESC LIMIT 5;");
            while (resultSet.next()) {
                idRequest = resultSet.getInt("id_request");
                lastFiveRequest.add(idRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastFiveRequest;
    }

    /**Метод, возвращающий id и время Request*/
    public HashMap<Integer, LocalDateTime> selectIdAndTimeRequest(int idRequestForSelect) {
        HashMap<Integer, LocalDateTime> idAndTimeRequest = new HashMap<>();
        int idRequest = -1;
        LocalDateTime timeDownload = null;
        try (Statement stmt = connectionDataBase.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM requests " +
                    "WHERE id_request = " + idRequestForSelect + ";");
            while (resultSet.next()) {
                idRequest = resultSet.getInt("id_request");
                timeDownload = resultSet.getObject("time_request", LocalDateTime.class);
                idAndTimeRequest.put(idRequest, timeDownload);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idAndTimeRequest;
    }

    /**Метод, возвращающий id объектов URL, содержащихся в Request*/
    public HashMap<Integer, Integer> selectIdUrlsForRequest (int idRequest) {
        HashMap<Integer, Integer> IdUrlsForRequest = new HashMap<>();
        int idUrl = -1;
        try (Statement stmt = connectionDataBase.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM requestsandurls " +
                    "WHERE id_request = " + idRequest + ";");
            while (resultSet.next()) {
                idUrl = resultSet.getInt("id_url");
                IdUrlsForRequest.put(idRequest, idUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return IdUrlsForRequest;
    }

    /**Метод, возвращающий URL и контент для ID URL*/
    public HashMap<String, String> selectUrlAndContent(int idUrl) {
        String url = null;
        String content = null;
        HashMap<String, String> urlAndContent = new HashMap<>();

        try (Statement stmt = connectionDataBase.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM urls WHERE id_url = " + idUrl + ";");
            while (resultSet.next()) {
                url = resultSet.getString("url");
                content = resultSet.getString("content");
                urlAndContent.put(url, content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urlAndContent;
    }

    @PreDestroy
    public void destroy() {
        try {
            connectionDataBase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
