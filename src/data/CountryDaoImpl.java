package data;

import biz.DatabaseConnectionFactory;
import models.Country;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CountryDaoImpl implements Dao<Country> {

    private final ObservableList<Country> countries;
    private final User user;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Constructor for Country Dao Impl
     * @param user current application user
     */
    public CountryDaoImpl(User user) {
        this.user = user;
        countries = FXCollections.observableArrayList();
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
     * Gets Country Object by Country_ID,
     * or an empty Optional if no results
     * @param id Country_ID
     * @return Optional object with either the Country or Empty.
     */
    @Override
    public Optional<Country> get(int id) {
        try {
            String query = "SELECT * FROM countries WHERE Country_ID =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    return Optional.of(new Country(
                            resultSet.getInt("Country_ID"),
                            resultSet.getString("Country")
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
     * Gets all Country Entities from the database
     * and collects them into an ObservableList
     * @return ObservableList of Country Entities from database
     */
    @Override
    public ObservableList<Country> getAll() {
        try {
            countries.clear();
            String query = "SELECT * FROM countries";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                {
                    Country country = new Country(
                            resultSet.getInt("Country_ID"),
                            resultSet.getString("Country")
                    );

                    countries.add(country);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return countries;
    }

    /**
     * Saves new Country entity to database
     * @param country entity to save to database.
     */
    @Override
    public void save(Country country) {
        try {
            String query = "INSERT INTO countries VALUES (?,?,NOW(),?,NOW(),?)";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, country.getId());
            preparedStatement.setString(2, country.getName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Updates existing country entity in database.
     * @param country entity to update
     */
    @Override
    public void update(Country country) {
        try {
            String query = "UPDATE countries SET " +
                    "Country = ?, " +
                    "Last_Updated_By = ?, " +
                    "Last_Update = NOW()" +
                    "WHERE Country_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country.getName());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setInt(3, country.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    @Override
    public void delete(Country country){
        try {
            String query = "DELETE FROM countries WHERE Country_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, country.getId());
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
