package com.github.a_oleg.textloader.repository;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DBRepository {
    private static final String URL_DB = "jdbc:postgresql://localhost:5432/requestsandurls";
    private static final String URL_POSTGRES = "jdbc:postgresql://localhost:5432/";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static Connection connectionDataBase;

    /**Создание базы данных*/
    public static void createUrlsDatabase() {
        String requestsDB = "CREATE DATABASE requestsAndUrls";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connectionDataBase = DriverManager.getConnection(URL_POSTGRES, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stmt = connectionDataBase.createStatement()) {
            stmt.executeUpdate(requestsDB);
            connectionDataBase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сreating database seccessfully");
        createUrlsDatabaseTables();
    }

    /**Создание таблиц в базе данных*/
    public static void createUrlsDatabaseTables() {
        String requestsTable = "CREATE TABLE REQUESTS" +
                "(id_request SERIAL PRIMARY KEY," +
                "time_request TIMESTAMP NOT NULL);";
        String urlsTable = "CREATE TABLE URLS" +
                "(id_url SERIAL PRIMARY KEY," +
                "url TEXT NOT NULL," +
                "content TEXT NOT NULL);";
        String requestsAndUrlsTable = "CREATE TABLE REQUESTSANDURLS" +
                "(id_request_url SERIAL PRIMARY KEY," +
                "id_request INT NOT NULL," +
                "CONSTRAINT fk_REQUESTS FOREIGN KEY(id_request) REFERENCES REQUESTS(id_request)," +
                "id_url INT NOT NULL," +
                "CONSTRAINT fk_URLS FOREIGN KEY(id_url) REFERENCES URLS(id_url));";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connectionDataBase = DriverManager.getConnection(URL_DB, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement stmt = connectionDataBase.createStatement()) {
            stmt.executeUpdate(urlsTable);
            stmt.executeUpdate(requestsTable);
            stmt.executeUpdate(requestsAndUrlsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connectionDataBase.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сreating tables seccessfully");
    }
}
