package data;

import biz.DatabaseConnectionFactory;
import models.Customer;
import models.Division;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerDaoImpl implements Dao<Customer> {
    private final User user;
    private final ObservableList<Customer> customers;
    private final Dao<Division> divisionDao;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Constructor for Customer Dao Impl
     * @param user current app user
     */
    public CustomerDaoImpl(User user) {
        this.user = user;
        customers = FXCollections.observableArrayList();
        divisionDao = new DivisionDaoImpl(user);
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
     * Gets Customer Object by Customer_ID,
     * or an empty Optional if no results
     * @param id Customer_ID
     * @return Optional<Customer> object with either the Customer or Empty.
     */
    @Override
    public Optional<Customer> get(int id) {
        try {
            String query = "SELECT * FROM customers WHERE Customer_ID =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    return Optional.of(new Customer(
                            resultSet.getInt("Customer_ID"),
                            resultSet.getString("Customer_Name"),
                            resultSet.getString("Address"),
                            resultSet.getString("Postal_Code"),
                            resultSet.getString("Phone"),
                            resultSet.getString("Created_By"),
                            resultSet.getString("Last_Updated_By"),
                            resultSet.getInt("Division_ID")
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
     * Gets all Customer Entities from the database
     * and collects them into an ObservableList
     * @return ObservableList of Customer Entities from database
     */
    @Override
    public ObservableList<Customer> getAll() {
        try {
            customers.clear();
            String query = "SELECT * FROM customers";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Customer customer = new Customer(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Created_By"),
                        resultSet.getString("Last_Updated_By"),
                        resultSet.getInt("Division_ID")
                );
                Optional<Division> division = divisionDao.get(customer.getDivisionId());

                if(division.isPresent()){
                    customer.setDivision(division.get());
                    customer.setDivisionName(division.get().getName());
                    customer.setCountryName(division.get().getCountryName());
                }

                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return customers;
    }

    /**
     * Saves new Customer entity to database
     * @param customer entity to save to database.
     */
    @Override
    public void save(Customer customer) {
        try {
            String query = "INSERT INTO customers VALUES (?,?,?,?,?,NOW(),?,NOW(),?,;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customer.getId());
            preparedStatement.setString(2, customer.getName());
            preparedStatement.setString(3, customer.getAddress());
            preparedStatement.setString(4, customer.getPostalCode());
            preparedStatement.setString(5, customer.getPhone());
            preparedStatement.setString(6, user.getUserName());
            preparedStatement.setInt(7, customer.getDivisionId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Updates existing customer entity in database.
     * @param customer entity to update
     */
    @Override
    public void update(Customer customer) {
        try {
            String query = "UPDATE customers SET " +
                    "Customer_Name = ?, " +
                    "Address = ?, " +
                    "Postal_Code = ?, " +
                    "Phone = ?, " +
                    "Division_ID = ?, " +
                    "Last_Update_Date = NOW(), " +
                    "Last_Updated_by = ? " +
                    "WHERE Customer_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getAddress());
            preparedStatement.setString(3, customer.getPostalCode());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setInt(5, customer.getDivisionId());
            preparedStatement.setString(6, user.getUserName());
            preparedStatement.setInt(7, customer.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           closeConnections();
        }
    }

    /**
     * Deletes customer entity from database
     * @param customer entity to delete
     */
    @Override
    public void delete(Customer customer) {
        try {
            String query = "DELETE FROM customers WHERE Customer_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customer.getId());
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
