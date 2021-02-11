package data;

import biz.DatabaseConnectionFactory;
import biz.TimeService;
import models.Appointment;
import models.Contact;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.ZoneId;
import java.util.Optional;

public class AppointmentDaoImpl implements Dao<Appointment> {

    private final ObservableList<Appointment> appointments;
    private final ContactDaoImpl contactDao;
    private final User user;
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * Constructor for Appointment Dao Impl
     * @param user current app user
     */
    public AppointmentDaoImpl(User user) {
        this.user = user;
        appointments = FXCollections.observableArrayList();
        contactDao = new ContactDaoImpl(user);
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
     * Gets Appointment Object by Appointment_ID,
     * or an empty Optional if no results
     * @param id Appointment_ID
     * @return Optional object with either the Appointment or Empty.
     */
    @Override
    public Optional<Appointment> get(int id){
        try {
            String query = "SELECT * FROM appointments WHERE Appointment_ID =?";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.isBeforeFirst()) {
                return Optional.empty();
            }
            else {
                if(resultSet.next()){
                    return Optional.of(new Appointment(
                            resultSet.getInt("Appointment_ID"),
                            resultSet.getString("Title"),
                            resultSet.getString("Description"),
                            resultSet.getString("Location"),
                            resultSet.getString("Type"),
                            TimeService.convertToLocalDateTimeString(resultSet.getTimestamp("Start").toString(), ZoneId.systemDefault()),
                            TimeService.convertToLocalDateTimeString(resultSet.getTimestamp("End").toString(), ZoneId.systemDefault()),
                            resultSet.getInt("Customer_ID"),
                            resultSet.getInt("Contact_ID")
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
     * Gets all Appointment Entities from the database
     * and collects them into an ObservableList
     * @return ObservableList of Appointment Entities from database
     */
    @Override
    public ObservableList<Appointment> getAll() {
        try {
            appointments.clear();
            String query = "SELECT * FROM appointments";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Appointment appointment = new Appointment(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        TimeService.convertToLocalDateTimeString(resultSet.getTimestamp("Start").toString(), ZoneId.systemDefault()),
                        TimeService.convertToLocalDateTimeString(resultSet.getTimestamp("End").toString(), ZoneId.systemDefault()),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("Contact_ID")
                );
                Optional<Contact> contact = contactDao.get(appointment.getContactId());

                contact.ifPresent(appointment::setContact);

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
        return appointments;
    }

    /**
     * Saves new Appointment entity to database
     * @param appointment entity to save to database.
     */
    @Override
    public void save(Appointment appointment) {
        try {
            String query = "INSERT INTO appointments VALUES (?,?,?,?,?,?,?,NOW(),?,NOW(),?,?,?,?);";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointment.getId());
            preparedStatement.setString(2, appointment.getTitle());
            preparedStatement.setString(3, appointment.getDescription());
            preparedStatement.setString(4, appointment.getLocation());
            preparedStatement.setString(5, appointment.getType());
            preparedStatement.setTimestamp(6, TimeService.convertToUtcTimestamp(appointment.getStartTime(), ZoneId.systemDefault()));
            preparedStatement.setTimestamp(7, TimeService.convertToUtcTimestamp(appointment.getEndTime(), ZoneId.systemDefault()));
            preparedStatement.setString(8, user.getUserName());
            preparedStatement.setString(9, user.getUserName());
            preparedStatement.setInt(10, appointment.getCustomerId());
            preparedStatement.setInt(11, user.getId());
            preparedStatement.setInt(12, appointment.getContactId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Updates existing appointment entity in database.
     * @param appointment entity to update
     */
    @Override
    public void update(Appointment appointment) {
        try {
            String query = "UPDATE appointments SET " +
                    "Title = ?, " +
                    "Description = ?, " +
                    "Location = ?, " +
                    "Type = ?, " +
                    "Start = ?, " +
                    "End = ?, " +
                    "Last_Updated_By = ?, " +
                    "Customer_ID = ?, " +
                    "User_ID = ?, " +
                    "Contact_ID = ?, " +
                    "Last_Update = NOW()" +
                    "WHERE Appointment_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, appointment.getTitle());
            preparedStatement.setString(2, appointment.getDescription());
            preparedStatement.setString(3, appointment.getLocation());
            preparedStatement.setString(4, appointment.getType());
            preparedStatement.setTimestamp(5, TimeService.convertToUtcTimestamp(appointment.getStartTime(), ZoneId.systemDefault()));
            preparedStatement.setTimestamp(6, TimeService.convertToUtcTimestamp(appointment.getEndTime(), ZoneId.systemDefault()));
            preparedStatement.setString(7, user.getUserName());
            preparedStatement.setInt(8, appointment.getCustomerId());
            preparedStatement.setInt(9, user.getId());
            preparedStatement.setInt(10, appointment.getContactId());
            preparedStatement.setInt(11, appointment.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /**
     * Deletes appointment entity from database
     * @param appointment entity to delete
     */
    @Override
    public void delete(Appointment appointment) {
        try {
            String query = "DELETE FROM appointments WHERE Appointment_ID = ?;";
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointment.getId());
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
