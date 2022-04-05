package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDaoJDBCImpl implements UserDao {
    private static final Logger LOGGER = Logger.getLogger( UserDaoJDBCImpl.class.getName() );
    private final String dbTableName = "user";
    private final Util util = new Util();

    public UserDaoJDBCImpl() {
    }

    private void sqlUpdate(String request) {
        try ( Connection connection = util.getDBConnect() ) {
            try ( Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE) ) {
                statement.executeUpdate(request);
            }
        } catch (SQLException sqlErr) {
            LOGGER.severe("Error in UPDATE of the table.");
        }
    }

    public void createUsersTable() {
        sqlUpdate( String.format("CREATE TABLE IF NOT EXISTS %s (id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                                 " Name VARCHAR(20), lastName VARCHAR(20), age TINYINT);", dbTableName) );
    }

    public void dropUsersTable() {
        sqlUpdate( String.format("DROP TABLE IF EXISTS %s;", dbTableName) );
    }

    public void saveUser(String name, String lastName, byte age) {
        String request = String.format("INSERT %s (name, lastName, age) VALUES(?, ?, ?)", dbTableName);

        try ( Connection connection = util.getDBConnect() ) {
            try ( PreparedStatement statement = connection.prepareStatement(request) ) {
                statement.setNString(1, name);
                statement.setNString(2, lastName);
                statement.setByte(3, age);
                statement.executeUpdate();
            }
        } catch (SQLException sqlErr) {
            LOGGER.severe("Error in UPDATE of the table.");
        }
    }

    public void removeUserById(long id) {
        sqlUpdate( String.format("DELETE FROM %s WHERE id=%d;", dbTableName, id) );
    }

    public List<User> getAllUsers() {
        final List<User> allUsers = new ArrayList<>();

        try ( Connection connection = util.getDBConnect() ) {
            try ( Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE) ) {
                ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s;", dbTableName));

                if ( resultSet.first() ) {
                    do {
                        User user = new User( resultSet.getString("name"), resultSet.getString("lastName"), resultSet.getByte("age") );

                        user.setId(resultSet.getLong("id"));
                        allUsers.add(user);
                    } while (resultSet.next());
                }
            }
        } catch (SQLException sqlErr) {
            LOGGER.severe("Error in users QUERY.");
        }

        return allUsers;
    }

    public void cleanUsersTable() {
        sqlUpdate( String.format("DELETE FROM %s;", dbTableName) );
    }
}
