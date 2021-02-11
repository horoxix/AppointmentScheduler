package controllers;

import biz.TimeService;
import models.*;
import data.AppointmentDaoImpl;
import data.ContactDaoImpl;
import data.CustomerDaoImpl;
import data.Dao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.Helper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static biz.TimeService.timeFormatter;

public class ModifyAppointmentController {
    private final Appointment appointment;
    private final ObservableList<Appointment> appointments;
    private ResourceBundle bundle;
    private Scene scene;
    private final Dao<Appointment> appointmentDao;
    private final Dao<Customer> customerDao;
    private final Dao<Contact> contactDao;
    private boolean invalidSaveState;

    @FXML
    private TextField tfAppointmentId;

    @FXML
    private TextField tfTitle;

    @FXML
    private DatePicker dpStart;

    @FXML
    private DatePicker dpEnd;

    @FXML
    private ComboBox<Customer> cbCustomer;

    @FXML
    private ComboBox<Contact> cbContact;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private ComboBox<String> cbStartTime;

    @FXML
    private ComboBox<String> cbEndTime;

    @FXML
    private TextField tfLocation;

    /**
     * Constructor, creates the Modify Appointment Controller
     * @param appointment the current appointment to modify
     * @param user the current App User
     * @param appointments the current List of Appointments
     */
    public ModifyAppointmentController(Appointment appointment, User user, ObservableList<Appointment> appointments){
        // Initialize appointments
        this.appointment = appointment;
        this.appointments = appointments;

        // Initialize Data Access Objects
        appointmentDao = new AppointmentDaoImpl(user);
        customerDao = new CustomerDaoImpl(user);
        contactDao = new ContactDaoImpl(user);
    }

    /**
     * Initialize Method, called after constructor
     */
    public void initialize(){
        bundle = ResourceBundle.getBundle("resources.UIResources");

        // Initialize combo box lists
        cbStartTime.setItems(Helper.getTimes());
        cbEndTime.setItems(Helper.getTimes());
        cbCustomer.setItems(customerDao.getAll());
        cbContact.setItems(contactDao.getAll());
        cbType.setItems(Helper.getAppointmentTypes());

        // Populate text fields
        tfAppointmentId.setText(String.valueOf(appointment.getId()));
        tfTitle.setText(appointment.getTitle());
        taDescription.setText(appointment.getDescription());
        tfLocation.setText(appointment.getLocation());

        // Populate start and end date/time
        dpStart.setValue(TimeService.convertToLocalDate(appointment.getStartTime()));
        dpEnd.setValue(TimeService.convertToLocalDate(appointment.getEndTime()));
        cbType.getSelectionModel().select(appointment.getType());
        cbStartTime.getSelectionModel().select(TimeService.convertToLocalTime(appointment.getStartTime()).format(timeFormatter));
        cbEndTime.getSelectionModel().select(TimeService.convertToLocalTime(appointment.getEndTime()).format(timeFormatter));

        Optional<Contact> contact = cbContact
                .getItems()
                .stream()
                .filter(x -> (x.getId() == appointment.getContactId()))
                .findFirst();

        contact.ifPresent(value -> cbContact.getSelectionModel().select(value));

        //cbContact.getSelectionModel().select(appointment.getContact());

        Optional<Customer> customer = cbCustomer
                .getItems()
                .stream()
                .filter(x -> (x.getId() == appointment.getCustomerId()))
                .findFirst();

        customer.ifPresent(value -> cbCustomer.getSelectionModel().select(value));

        initializeBindings();
    }

    /**
     * Initializes button/ui bindings.
     */
    private void initializeBindings(){
        // Initialize Bindings and Actions
        btnSave.setOnAction((event -> {
            try {
                handleSaveButton(event);
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        btnCancel.setOnAction((event -> {
            try {
                handleCancelButtonAction(event);
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        dpStart.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        dpEnd.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
    }

    /**
     * Handles the Save Button click, Checks for invalid state
     * and then, if valid, saves to the database.
     * @param event Button Click Event
     * @throws Exception possible exception thrown
     */
    @FXML
    protected void handleSaveButton(ActionEvent event) throws Exception {
        try {
            // Verify all input information is valid before attempting to save.
            StringBuilder sb = verifySave();

            // If invalid, display error alert.
            if(sb.length() > 0)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(bundle.getString("warning"));
                alert.setHeaderText(bundle.getString("completeFields"));
                alert.setContentText(sb.toString());
                alert.showAndWait();
            }
            // If valid, save to database
            else {
                if (!invalidSaveState) {
                    appointment.setTitle(tfTitle.getText());
                    appointment.setDescription(taDescription.getText());
                    appointment.setContactId(cbContact.getSelectionModel().getSelectedItem().getId());
                    appointment.setCustomerId(cbCustomer.getSelectionModel().getSelectedItem().getId());
                    appointment.setLocation(tfLocation.getText());
                    appointment.setStartTime(TimeService.convertToLocalDateTimeString(
                            dpStart.getValue(),
                            cbStartTime.getSelectionModel().getSelectedItem()
                            , ZoneId.systemDefault()));
                    appointment.setEndTime(TimeService.convertToLocalDateTimeString(
                                    dpEnd.getValue(),
                                    cbEndTime.getSelectionModel().getSelectedItem(),
                            ZoneId.systemDefault()));

                    appointmentDao.update(appointment);
                    closeWindow();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(bundle.getString("warning"));
                    alert.setHeaderText(bundle.getString("resolveErrors"));
                    alert.setContentText(sb.toString());
                    alert.showAndWait();
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * On cancel button click, closes window
     * @param event action event of button click
     * @throws Exception possible Exception thrown
     */
    @FXML
    protected void handleCancelButtonAction(ActionEvent event) throws Exception {
        closeWindow();
    }

    /**
     * Gets the stage and closes the window.
     */
    private void closeWindow(){
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Verifies all required information to save the appointment
     * @return String Builder with any reported errors.
     */
    private StringBuilder verifySave(){
        StringBuilder errorList = new StringBuilder();
        if(invalidTitle()){
            errorList.append(bundle.getString("invalidTitle")).append("\n");
        }

        if(invalidDescription()){
            errorList.append(bundle.getString("invalidDescription")).append("\n");
        }

        if(invalidType()){
            errorList.append(bundle.getString("invalidType")).append("\n");
        }

        if(invalidDateTime()){
            errorList.append(bundle.getString("invalidDateTime")).append("\n");
        }

        if(invalidLocation()){
            errorList.append(bundle.getString("invalidLocation")).append("\n");
        }

        if(invalidCustomer()){
            errorList.append(bundle.getString("invalidCustomer")).append("\n");
        }

        if(invalidContact()){
            errorList.append(bundle.getString("invalidContact")).append("\n");
        }

        if(invalidBusinessHours()){
            errorList.append(bundle.getString("invalidBusinessHours")).append("\n");
        }

        if(appointmentHasConflict()){
            errorList.append(bundle.getString("appointmentConflict")).append("\n");
        }
        return errorList;
    }

    /**
     * Sets the scene value to use
     * @param scene to set controller to reference
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    };

    /**
     * Is the title invalid?
     * @return true if invalid
     */
    private boolean invalidTitle(){
        return tfTitle.getText().isEmpty();
    }

    /**
     * Is the location invalid?
     * @return true if invalid
     */
    private boolean invalidLocation(){
        return tfLocation.getText().isEmpty();
    }

    /**
     * Is the Description invalid?
     * @return true if invalid
     */
    private boolean invalidDescription(){
        return taDescription.getText().isEmpty();
    }

    /**
     * Is the Type selected?
     * @return true if not selected
     */
    private boolean invalidType(){
        return cbType.getSelectionModel().getSelectedIndex() == -1;
    }

    /**
     * Is the Customer selected?
     * @return true if not selected
     */
    private boolean invalidCustomer(){
        return cbCustomer.getSelectionModel().getSelectedIndex() == -1;
    }

    /**
     * Is the Contact selected?
     * @return true if not selected
     */
    private boolean invalidContact(){
        return cbContact.getSelectionModel().getSelectedIndex() == -1;
    }

    /**
     * Is the Date and Time selections valid?
     * Is the Start Date before the End Date?
     * @return true if invalid
     */
    private boolean invalidDateTime(){
        if(        dpEnd.getValue() == null
                || dpStart.getValue() == null
                || cbStartTime.getSelectionModel().getSelectedIndex() == -1
                || cbEndTime.getSelectionModel().getSelectedIndex() == -1) {
            return true;
        }
        else if(dpEnd.getValue().equals(dpStart.getValue())){
            if(cbStartTime.getValue().equals(cbEndTime.getValue())
                    || TimeService.getLocalTimeFromString(cbStartTime.getValue())
                    .isAfter(TimeService.getLocalTimeFromString(cbEndTime.getValue()))){
                return true;
            }
            return dpStart.getValue().isAfter(dpEnd.getValue());
        }
        else return dpStart.getValue().isAfter(dpEnd.getValue());
    }

    /**
     * Is the start and end date within valid EST Business hours?
     * @return true if invalid
     */
    private boolean invalidBusinessHours() {
        return TimeService.outOfBusinessHours(dpStart.getValue(), cbStartTime.getValue(), ZoneId.systemDefault())
                || TimeService.outOfBusinessHours(dpEnd.getValue(), cbEndTime.getValue(), ZoneId.systemDefault());
    }

    /**
     * Do the start and end date conflict with any existing appointments?
     * @return true if conflict
     */
    private boolean appointmentHasConflict(){
        for(Appointment app : appointments){

            if(app.getId() != appointment.getId()){
                ZonedDateTime oldStartTime = TimeService.convertToBusinessHoursZonedDateTime(app.getStartTime(), ZoneId.systemDefault());
                ZonedDateTime oldEndTime = TimeService.convertToBusinessHoursZonedDateTime(app.getEndTime(), ZoneId.systemDefault());
                ZonedDateTime newStartTime = TimeService.convertToBusinessHoursZonedDateTime(dpStart.getValue(), cbStartTime.getValue(), ZoneId.systemDefault());
                ZonedDateTime newEndTime = TimeService.convertToBusinessHoursZonedDateTime(dpEnd.getValue(), cbEndTime.getValue(), ZoneId.systemDefault());

                if ((newStartTime.isAfter(oldStartTime) || newStartTime.isEqual(oldStartTime)) && (newStartTime.isBefore(oldEndTime))){
                    return true;
                }

                if (newEndTime.isAfter(oldStartTime) && (newEndTime.isBefore(oldEndTime) || newEndTime.isEqual(oldEndTime))){
                    return true;
                }
            }
        }
        return false;
    }
}
