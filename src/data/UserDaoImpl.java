package data;

import biz.DatabaseConnectionFactory;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Optional;

public class UserDaoImpl implements Dao<User> {

    private final ObservableList<User> users;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Constructor for User Dao Impl
     */
    public UserDaoImpl() {
        users = FXCollections.observableArrayList();
    }

    /**
     * Initializes SQL connection instance
     * @return Connection instance
     * @throws SQLException if invalid sql connection
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection conn;
        conn = DatabaseConnectionFactory.getInstance().getConnection();
        return conn;
    }

    /**
     * Gets User Object by User_ID,
     * or an empty Optional if no results
     * @param id User_ID
     * @return Optional<User> object with either the User or Empty.
     */
    @Override
    public Optional<User> get(int id) {
        try {
            String query = "SELECT * FROM users WHERE User_ID =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    return Optional.of(new User(
                            resultSet.getInt("User_ID"),
                            resultSet.getString("User_Name"),
                            resultSet.getString("Password"),
                            resultSet.getDate("Create_Date"),
                            resultSet.getString("Created_By"),
                            resultSet.getTimestamp("Last_Update"),
                            resultSet.getString("Last_Updated_By")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return Optional.empty();
    }

    /**
     * Gets User Object by User_Name,
     * or an empty Optional if no results
     * @param userName string of the username to get
     * @return Optional<User> object with either the User or Empty.
     */
    public Optional<User> getByUsername(String userName) {
        try {
            users.clear();
            String query = "SELECT * FROM users WHERE User_Name =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    return Optional.of(new User(
                            resultSet.getInt("User_ID"),
                            resultSet.getString("User_Name"),
                            resultSet.getString("Password"),
                            resultSet.getDate("Create_Date"),
                            resultSet.getString("Created_By"),
                            resultSet.getTimestamp("Last_Update"),
                            resultSet.getString("Last_Updated_By")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           closeConnections();
        }
        return Optional.empty();
    }

    /**
     * Gets all User Entities from the database
     * and collects them into an ObservableList
     * @return ObservableList of User Entities from database
     */
    @Override
    public ObservableList<User> getAll() {
        try {
            users.clear();
            String query = "SELECT * FROM Users";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                User user = new User(
                        resultSet.getInt("Id"),
                        resultSet.getString("UserName"),
                        resultSet.getString("Password"),
                        resultSet.getDate("CreateDate"),
                        resultSet.getString("CreatedBy"),
                        resultSet.getTimestamp("LastUpdate"),
                        resultSet.getString("LastUpdatedBy")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
          closeConnections();
        }
        return users;
    }

    /**
     * Saves new User entity to database
     * @param user entity to save to database.
     */
    @Override
    public void save(User user) {
        try {
            String query = "INSERT INTO users VALUES (?,?,?,NOW(),?,NOW(),?);";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.setString(5, user.getUserName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Updates existing user entity in database.
     * @param user entity to update
     */
    @Override
    public void update(User user) {
        try {
            String query = "UPDATE users SET " +
                    "User_Name = ?, " +
                    "Password = ?, " +
                    "Last_Updated_By = ?, " +
                    "Last_Update = NOW()" +
                    "WHERE User_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setInt(4, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    @Override
    public void delete(User user) {
        try {
            String query = "DELETE FROM users WHERE User_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * If any connections are open, close them.
     */
    @Override
    public void closeConnections(){
        try {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
