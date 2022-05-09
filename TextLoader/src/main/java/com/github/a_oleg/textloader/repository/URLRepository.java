package com.github.a_oleg.textloader.repository;

import com.github.a_oleg.textloader.models.URL;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class URLRepository {
    private final DBRepository dbRepository;

    @Autowired
    public URLRepository(DBRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    //Потом убрать поля в отдельный файл
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
        } catch (PSQLException e) {
            dbRepository.createUrlsDatabase();
        } catch (SQLException e) {
            System.out.println("The database is missing. No requests to upload");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**Метод, осуществляющий вставку данных в таблицы БД*/
    public static boolean insertingDataIntoTables(ArrayList<URL> urls) throws SQLException {
        connectionDataBase = DriverManager.getConnection(URL_DB, USERNAME, PASSWORD);

        LocalDateTime localDate = LocalDateTime.now();

        PreparedStatement preparedStatement = connectionDataBase.prepareStatement("INSERT INTO " +
                "requests(time_request) VALUES(?);");
        preparedStatement.setTimestamp(1, Timestamp.valueOf(localDate));
        preparedStatement.executeUpdate();

        for(URL urlModel: urls) {
            String url = urlModel.getUrl();
            String content = urlModel.getContent();

            preparedStatement = connectionDataBase.prepareStatement("INSERT INTO " +
                    "urls(url, content) VALUES(?, ?);");
            preparedStatement.setString(1, url);
            preparedStatement.setString(2, content);
            preparedStatement.executeUpdate();

            int requestId = -1;
            preparedStatement = connectionDataBase.prepareStatement("SELECT id_request FROM " +
                    "requests WHERE time_request = ?");
            preparedStatement.setTimestamp(1, Timestamp.valueOf(localDate));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                requestId = resultSet.getInt("id_request");
            }

            int urlId = -1;
            preparedStatement = connectionDataBase.prepareStatement("SELECT id_url FROM urls " +
                    "WHERE url = ?");
            preparedStatement.setString(1, url);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                urlId = resultSet.getInt("id_url");
            }
            resultSet.close();

            preparedStatement = connectionDataBase.prepareStatement("INSERT INTO " +
                    "requestsandurls(id_request, id_url) VALUES(?, ?);");
            preparedStatement.setInt(1, requestId);
            preparedStatement.setInt(2, urlId);
            preparedStatement.executeUpdate();
        }
        System.out.println("The data has been successfully added to the tables");
        connectionDataBase.close();
        return true;
    }
}
