package com.github.a_oleg.textloader.repository;

import org.apache.commons.math3.util.Pair;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${URL}")
    String URL;
    @Value("${USERNAMEDB}")
    String USERNAME;
    @Value("${PASSWORD}")
    String PASSWORD;

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
            connectionDataBase = DriverManager.getConnection(URL, USERNAME, PASSWORD);
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
    public Pair selectIdAndTimeRequest(int idRequestForSelect) {
        Pair <Integer, LocalDateTime> idAndTimeRequest = null;
        int idRequest = -1;
        LocalDateTime timeDownload = null;

        try (Statement stmt = connectionDataBase.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM requests " +
                    "WHERE id_request = " + idRequestForSelect + ";");
            while (resultSet.next()) {
                idRequest = resultSet.getInt("id_request");
                timeDownload = resultSet.getObject("time_request", LocalDateTime.class);
                idAndTimeRequest = new Pair<Integer, LocalDateTime>(idRequest, timeDownload);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idAndTimeRequest;
    }

    /**Метод, возвращающий id объектов URL, содержащихся в Request*/
    public ArrayList<Integer> selectIdUrlsForRequest (int idRequest) {
        ArrayList<Integer> urls = new ArrayList<>();
        int idUrl = -1;
        try (Statement stmt = connectionDataBase.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM requestsandurls " +
                    "WHERE id_request = " + idRequest + ";");
            while (resultSet.next()) {
                idUrl = resultSet.getInt("id_url");
                urls.add(idUrl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urls;
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
