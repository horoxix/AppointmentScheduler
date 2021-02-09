package data;

import biz.DatabaseConnectionFactory;
import models.Contact;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ContactDaoImpl implements Dao<Contact> {

    private final ObservableList<Contact> contacts;
    private final User user;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Constructor for Contact Dao Impl
     */
    public ContactDaoImpl(User user) {
        this.user = user;
        contacts = FXCollections.observableArrayList();
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
     * Gets Contact Object by Contact_ID,
     * or an empty Optional if no results
     * @param id Contact_ID
     * @return Optional<Contact> object with either the Contact or Empty.
     */
    @Override
    public Optional<Contact> get(int id) {
        try {
            String query = "SELECT * FROM contacts WHERE Contact_ID =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    return Optional.of(new Contact(
                            resultSet.getInt("Contact_ID"),
                            resultSet.getString("Contact_Name"),
                            resultSet.getString("Email")
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
     * Gets all Contact Entities from the database
     * and collects them into an ObservableList
     * @return ObservableList of Contact Entities from database
     */
    @Override
    public ObservableList<Contact> getAll() {
        try {
            contacts.clear();
            String query = "SELECT * FROM contacts";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Contact contact = new Contact(
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name"),
                        resultSet.getString("Email")
                );
                contacts.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return contacts;
    }

    /**
     * Saves new Contact entity to database
     * @param contact entity to save to database.
     */
    @Override
    public void save(Contact contact) {
        try {
            String query = "INSERT INTO contacts VALUES (?,?,?);";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, contact.getId());
            preparedStatement.setString(2, contact.getName());
            preparedStatement.setString(3, contact.getEmail());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Updates existing contact entity in database.
     * @param contact entity to update
     */
    @Override
    public void update(Contact contact) {
        try {
            String query = "UPDATE contacts SET " +
                    "Contact_Name = ?, " +
                    "Email = ?, " +
                    "Last_Updated_By = ?, " +
                    "Last_Update = NOW()" +
                    "WHERE Contact_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, contact.getName());
            preparedStatement.setString(2, contact.getEmail());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setInt(4, contact.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Deletes contact entity from database
     * @param contact entity to delete
     */
    @Override
    public void delete(Contact contact) {
        try {
            String query = "DELETE FROM contacts WHERE Contact_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, contact.getId());
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
