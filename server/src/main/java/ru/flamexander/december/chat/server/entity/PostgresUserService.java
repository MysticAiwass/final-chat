package ru.flamexander.december.chat.server.entity;

import ru.flamexander.december.chat.server.UserService;

import java.sql.*;

public class PostgresUserService implements UserService {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/lesson_26";
    private static final String USER = "postgres";
    private static final String PASSWORD  = "1";

    private static final String SELECT_ROLES_FOR_USER = "select r.name_role from roles r " +
            "join user_role ur on r.role_id = ur.role_id " +
            "join users u on ur.user_id = u.user_id " +
            "where u.username = ?;";

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT username FROM users WHERE login=? AND password=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createNewUser(String login, String password, String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            connection.setAutoCommit(false);

            String insertUserQuery = "INSERT INTO users (login, password, username) VALUES (?, ?, ?)";
            try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertUserStatement.setString(1, login);
                insertUserStatement.setString(2, password);
                insertUserStatement.setString(3, username);
                insertUserStatement.executeUpdate();

                try (ResultSet generatedKeys = insertUserStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        String insertUserRoleQuery = "INSERT INTO user_role (user_id, role_id) VALUES (?, " +
                                "(SELECT role_id FROM roles WHERE name_role = 'USER'))";
                        try (PreparedStatement insertUserRoleStatement = connection.prepareStatement(insertUserRoleQuery)) {
                            insertUserRoleStatement.setInt(1, userId);
                            insertUserRoleStatement.executeUpdate();
                        }

                        connection.commit();
                    } else {
                        connection.rollback();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT COUNT(*) AS count FROM users WHERE login=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, login);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isUsernameAlreadyExist(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String query = "SELECT COUNT(*) AS count FROM users WHERE username=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getUserRole(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLES_FOR_USER)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("name_role");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
