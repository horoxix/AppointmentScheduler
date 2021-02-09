package data;

import biz.DatabaseConnectionFactory;
import models.Country;
import models.Division;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DivisionDaoImpl implements Dao<Division> {

    private final ObservableList<Division> divisions;
    private final User user;
    private final Dao<Country> countryDao;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Constructor for Division Dao Impl
     * @param user current app user
     */
    public DivisionDaoImpl(User user) {
        this.user = user;
        divisions = FXCollections.observableArrayList();
        countryDao = new CountryDaoImpl(user);
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
     * Gets Division Object by Division_ID,
     * or an empty Optional if no results
     * @param id Division_ID
     * @return Optional<Division> object with either the Division or Empty.
     */
    @Override
    public Optional<Division> get(int id) {
        try {
            String query = "SELECT * FROM first_level_divisions WHERE Division_ID =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    Division division = new Division(
                            resultSet.getInt("Division_ID"),
                            resultSet.getString("Division"),
                            resultSet.getDate("Create_Date"),
                            resultSet.getString("Created_By"),
                            resultSet.getTimestamp("Last_Update"),
                            resultSet.getString("Last_Updated_By"),
                            resultSet.getInt("Country_ID")
                    );

                    Optional<Country> country = countryDao.get(division.getCountryId());

                    if(country.isPresent()){
                        division.setCountry(country.get());
                        division.setCountryName(country.get().getName());
                    }
                    return Optional.of(division);
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
     * Gets all Appointment Entities from the database
     * and collects them into an ObservableList
     * @return ObservableList of Appointment Entities from database
     */
    @Override
    public ObservableList<Division> getAll() {
        try {
            divisions.clear();
            String query = "SELECT * FROM first_level_divisions";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Division division = new Division(
                        resultSet.getInt("Division_ID"),
                        resultSet.getString("Division"),
                        resultSet.getDate("Create_Date"),
                        resultSet.getString("Created_By"),
                        resultSet.getTimestamp("Last_Update"),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getInt("Country_ID")
                );

                divisions.add(division);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return divisions;
    }

    /**
     * Saves new division entity to database
     * @param division entity to save to database.
     */
    @Override
    public void save(Division division) {
        try {
            String query = "INSERT INTO first_level_divisions VALUES (?,?,NOW(),?,NOW(),?,?,?,?);";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, division.getId());
            preparedStatement.setString(2, division.getName());
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
     * Updates existing division entity in database.
     * @param division entity to update
     */
    @Override
    public void update(Division division) {
        try {
            String query = "UPDATE first_level_divisions SET " +
                    "Division = ?, " +
                    "Last_Updated_By = ?, " +
                    "Last_Update = NOW()" +
                    "WHERE Division_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, division.getName());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setInt(3, division.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Deletes division entity from database
     * @param division entity to delete
     */
    @Override
    public void delete(Division division) {
        try {
            String query = "DELETE FROM first_level_divisions WHERE Division_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, division.getId());
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
