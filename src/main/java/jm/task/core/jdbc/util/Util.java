package jm.task.core.jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
    private static final Logger LOGGER = Logger.getLogger( Util.class.getName() );
    private final String dbHost = "localhost:3306";
    private final String dbOwner = "root";
    private final String dbPassword = "biblio888tekar";
    private final String dbName = "pre_project_test_schema";
    private final String dbURL = "jdbc:mysql://" + dbHost + "/" + dbName;
    private Connection dbConnect;

    public Util() {
        dbConnect = getDBConnect();
    }

    public Connection getDBConnect() {
        try {
            if (null == dbConnect || dbConnect.isClosed()) {
                dbConnect = DriverManager.getConnection(dbURL, dbOwner, dbPassword);
            }
        } catch (SQLException sqlErr) {
            LOGGER.log(Level.SEVERE, "Database connection error.", sqlErr);
        }
        return dbConnect;
    }
}
