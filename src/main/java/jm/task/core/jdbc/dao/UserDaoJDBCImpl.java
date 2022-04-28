package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDaoJDBCImpl implements UserDao {
    private static final Logger LOGGER = Logger.getLogger(UserDaoJDBCImpl.class.getName());
    private final Connection connection = new Util().getDBConnect();

    public UserDaoJDBCImpl() {
    }

    private boolean sqlUpdate(String request) {
        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            connection.setAutoCommit(false);
            statement.executeUpdate(request);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlErr) {
            try {
                connection.rollback();
            } catch (SQLException rollbackErr) {
                LOGGER.log(Level.SEVERE, "ROLLBACK error.", rollbackErr);
            }
            LOGGER.severe("Error in COMMIT operation of the table.");
            return false;
        }
        return true;
    }

    public void createUsersTable() {
        if (!sqlUpdate( "CREATE TABLE IF NOT EXISTS User (id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    " Name VARCHAR(20), lastName VARCHAR(20), age TINYINT);")) {
            LOGGER.severe("Table not created.");
        }
    }

    public void dropUsersTable() {
        if(!sqlUpdate("DROP TABLE IF EXISTS User;")) {
            LOGGER.severe("Table not dropped.");
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        String request = "INSERT User (name, lastName, age) VALUES(?, ?, ?)";

        try ( PreparedStatement statement = connection.prepareStatement(request) ) {
            connection.setAutoCommit(false);
            statement.setNString(1, name);
            statement.setNString(2, lastName);
            statement.setByte(3, age);
            statement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlErr) {
            try {
                connection.rollback();
            } catch (SQLException rollbackErr) {
                LOGGER.log(Level.SEVERE, "ROLLBACK error.", rollbackErr);
            }
            LOGGER.severe("User not saved.");
        }
    }

    public void removeUserById(long id) {
        if (!sqlUpdate(String.format("DELETE FROM User WHERE id=%d;", id))) {
            LOGGER.severe("User not removed.");
        }
    }

    public List<User> getAllUsers() {
        final List<User> allUsers = new ArrayList<>();

        try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM User;");

            while (resultSet.next()) {
                User user = new User(resultSet.getString("name"), resultSet.getString("lastName"), resultSet.getByte("age"));

                user.setId(resultSet.getLong("id"));
                allUsers.add(user);
            }
        } catch (SQLException sqlErr) {
            LOGGER.severe("Error in QUERY.");
        }
        return allUsers;
    }

    public void cleanUsersTable() {
        if (!sqlUpdate("DELETE FROM User;")) {
            LOGGER.severe("Table not cleaned.");
        }
    }

}
