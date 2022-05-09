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
            ResultSet resultSet = stmt.executeQuery("SELECT MAX(id_request) FROM requests;");
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

//    //Нужно переписать класс. Т.к. сейчас он сам смотрит последни 5 запросов и выбирает нужный реквест. Функционал 5 запросов вынесен в отдельный метод
//    /**Метод, присваивающий значения переменным объекта Request*/
//    public Request initializeRequest (int numberRequest) {
//        int idRequest = -1;
//        LocalDateTime timeDownload = null;
//        try (Statement stmt = connectionDataBase.createStatement()) {
//            ResultSet resultSet = stmt.executeQuery("SELECT * FROM requests " +
//                        "WHERE id_request = " + numberRequest + ";");
//                while (resultSet.next()) {
//                    idRequest = resultSet.getInt("id_request");
//                    timeDownload = resultSet.getObject("time_request", LocalDateTime.class);
//                }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    public ArrayList<Request> initializeRequest(ArrayList<Integer> numbersRequests) {
//        ArrayList<Request> requests = new ArrayList<>();
//
//        Request requestModel = null;
//        int idRequest = -1;
//        LocalDateTime timeDownload = null;
//        ArrayList<URL> urls = new ArrayList<>();
//
//        for (int numberRequest : numbersRequests) {
//            try (Statement stmt = connectionDataBase.createStatement()) {
//                ResultSet resultSet = stmt.executeQuery("SELECT * FROM requests " +
//                        "WHERE id_request = (SELECT MAX(id_request) FROM requests) - " + numberRequest + ";");
//                while (resultSet.next()) {
//                    idRequest = resultSet.getInt("id_request");
//                    timeDownload = resultSet.getObject("time_request", LocalDateTime.class);
//                }
//                resultSet = stmt.executeQuery("SELECT id_url FROM requestsandurls WHERE id_request = ''"
//                        + idRequest + "';");
//                while (resultSet.next()) {
//                    int idURL = resultSet.getInt("id_url");
//                    urls.add(initializeURL(idURL));
//                }
//            } catch(SQLException e){
//                e.printStackTrace();
//            }
//        }
//        return requests(idRequest, timeDownload, );
//    }


//            ArrayList <URL> requestUrls = new ArrayList<>();
//            try (Statement stmt = connectionDataBase.createStatement()) {
//                ResultSet resultSet = stmt.executeQuery("SELECT * FROM requests " +
//                        "WHERE id_request = (SELECT MAX(id_request) FROM requests) - " + numbersRequests + ";");
//                while (resultSet.next()) {
//                    idRequest = resultSet.getInt("id_request");
//                    timeDownload = resultSet.getObject("time_request", LocalDateTime.class);
//                }
//
//                resultSet = stmt.executeQuery("SELECT id_url FROM requestsandurls WHERE id_request = '"
//                        + idRequest + "';");
//                while (resultSet.next()) {
//                    int idURL = resultSet.getInt("id_url");
//                    requestUrls.add(initializeURL(idURL));
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            requestModel = new Request(idRequest, timeDownload, requestUrls);

//    /**Метод, формирующий массив из id и времени последних пяти запросов*/
//    public HashMap<Integer, LocalDateTime> selectIdLastFiveRequests() {
//        HashMap<Integer, LocalDateTime> lastFiveRequests = new HashMap<>();
//        try (Statement stmt = connectionDataBase.createStatement()) {
//            ResultSet resultSet = stmt.executeQuery("SELECT * FROM requests ORDER BY time_request DESC LIMIT 5;");
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id_request");
//                LocalDateTime timeDownload = resultSet.getObject("time_request", LocalDateTime.class);
//                lastFiveRequests.put(id, timeDownload);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return lastFiveRequests;
//    }

//    /**Метод, формирующий коллекцию запросов и url*/
//    public HashMap selectUrlsForTheLastFiveRequests(ArrayList<Integer> requests) {
//        HashMap<Integer, Integer> requestsAndUrls = new HashMap<>();
//
//        try (Statement stmt = connectionDataBase.createStatement()) {
//            for (int id_request : requests) {
//                ResultSet resultSet = stmt.executeQuery("SELECT * FROM requestsandurls WHERE id_request = "
//                        + id_request + ";");
//                while (resultSet.next()) {
//                    int id_url = resultSet.getInt("id_url");
//                    requestsAndUrls.put(id_request, id_url);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return requestsAndUrls;
//    }
//
//    public HashMap selectUrlsAndContext(HashMap requestsAndUrls) {
//        HashMap <Integer, HashMap<String, String>> UrlsAndContext = new HashMap<>();
//        Map<Integer, Integer> requestsAndUrlsMap = requestsAndUrls;
//
//        try (Statement stmt = connectionDataBase.createStatement()) {
//            for (Map.Entry<Integer, Integer> entry: requestsAndUrlsMap.entrySet()) {
//                ResultSet resultSet = stmt.executeQuery("SELECT * FROM urls WHERE id_url = "
//                        + entry.getValue() + ";");
//                while (resultSet.next()) {
//                    String url = resultSet.getString("url");
//                    String content = resultSet.getString("content");
//                    System.out.println("URL: " + url + ", content: " + content);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return UrlsAndContext;

    @PreDestroy
    public void destroy() {
        try {
            connectionDataBase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
