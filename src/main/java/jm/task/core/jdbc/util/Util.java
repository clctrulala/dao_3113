package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {
    private final String dbHost = "localhost:3306";
    private final String dbOwner = "root";
    private final String dbPassword = "biblio888tekar";
    private final String dbName = "pre_project_test_schema";
    private Connection dbConnect;

    public Util() {
        dbConnect = connect();
    }

    private Connection connect() {
        final String dbURL = "jdbc:mysql://" + dbHost + "/" + dbName;

        try {
            dbConnect = DriverManager.getConnection(dbURL, dbOwner, dbPassword);
        } catch (SQLException sqlErr) {
        }
        return dbConnect;
    }

    public Connection getDBConnect() throws SQLException {
        if (null == dbConnect || dbConnect.isClosed()) { dbConnect = connect(); }
        return dbConnect;
    }

}
