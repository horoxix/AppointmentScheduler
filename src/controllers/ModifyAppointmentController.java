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
import java.time.ZonedDateTime;
import java.util.Optional;

import static biz.TimeService.timeFormatter;

public class ModifyAppointmentController {
    private final Appointment appointment;
    private final ObservableList<Appointment> appointments;
    private final User user;
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


    public ModifyAppointmentController(Appointment appointment, User user, ObservableList<Appointment> appointments){
        // Initialize Modifying Appointment
        this.appointment = appointment;
        this.appointments = appointments;
        this.user = user;

        // Initialize Data Access Objects
        appointmentDao = new AppointmentDaoImpl(user);
        customerDao = new CustomerDaoImpl(user);
        contactDao = new ContactDaoImpl(user);
    }

    public void initialize(){
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

        cbContact.getSelectionModel().select(appointment.getContact());

        Optional<Customer> customer = cbCustomer
                .getItems()
                .stream()
                .filter(x -> (x.getId() == appointment.getCustomerId()))
                .findFirst();

        customer.ifPresent(value -> cbCustomer.getSelectionModel().select(value));

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

    @FXML
    protected void handleSaveButton(ActionEvent event) throws Exception {
        try {
            StringBuilder sb = verifySave();
            if(sb.length() > 0)
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Modify Customer Warning");
                alert.setHeaderText("Please complete all fields.");
                alert.setContentText(sb.toString());
                alert.showAndWait();
            }
            else {
                if (!invalidSaveState) {
                    appointment.setTitle(tfTitle.getText());
                    appointment.setDescription(taDescription.getText());
                    appointment.setContactId(cbContact.getSelectionModel().getSelectedItem().getId());
                    appointment.setCustomerId(cbCustomer.getSelectionModel().getSelectedItem().getId());
                    appointment.setLocation(tfLocation.getText());
                    appointment.setStartTime(TimeService.convertToLocalDateTimeString(
                            dpStart.getValue(),
                            cbStartTime.getSelectionModel().getSelectedItem()));
                    appointment.setEndTime(TimeService.convertToLocalDateTimeString(
                                    dpEnd.getValue(),
                                    cbEndTime.getSelectionModel().getSelectedItem()));

                    appointmentDao.update(appointment);
                    closeWindow();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Modify Customer Warning");
                    alert.setHeaderText("Please resolve any error messages.");
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
            errorList.append(" - Please enter a title.\n");
        }

        if(invalidDescription()){
            errorList.append(" - Please enter a description.\n");
        }

        if(invalidType()){
            errorList.append(" - Please select a valid Type.\n");
        }

        if(invalidDateTime()){
            errorList.append(" - Please enter a valid Date and Time combination.\n");
        }

        if(invalidCustomer()){
            errorList.append(" - Please select a Customer.\n");
        }

        if(invalidContact()){
            errorList.append(" - Please select a Contact.\n");
        }

        if(invalidBusinessHours()){
            errorList.append(" - Start and End date/time must be within business hours of 8:00 AM - 10:00 PM EST.");
        }

        if(appointmentHasConflict()){
            errorList.append(" - Appointment Start and End date/time conflict with an existing appointment.");
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
        return cbType.getSelectionModel().isEmpty();
    }

    /**
     * Is the Customer selected?
     * @return true if not selected
     */
    private boolean invalidCustomer(){
        return cbCustomer.getSelectionModel().isEmpty();
    }

    /**
     * Is the Contact selected?
     * @return true if not selected
     */
    private boolean invalidContact(){
        return cbContact.getSelectionModel().isEmpty();
    }

    /**
     * Is the Date and Time selections valid?
     * Is the Start Date before the End Date?
     * @return true if invalid
     */
    private boolean invalidDateTime(){
        if(        dpEnd.getValue() == null
                || dpStart.getValue() == null
                || cbStartTime.getSelectionModel().isEmpty()
                || cbEndTime.getSelectionModel().isEmpty()) {
            return true;
        }
        else if(dpEnd.getValue() == dpStart.getValue()
                && TimeService.getLocalTimeFromString(cbStartTime.getValue())
                .isAfter(TimeService.getLocalTimeFromString(cbEndTime.getValue()))){
            return true;
        }
        else return dpStart.getValue().isAfter(dpEnd.getValue());
    }

    /**
     * Is the start and end date within valid EST Business hours?
     * @return true if invalid
     */
    private boolean invalidBusinessHours() {
        return TimeService.outOfBusinessHours(dpStart.getValue(), cbStartTime.getValue())
                || TimeService.outOfBusinessHours(dpEnd.getValue(), cbEndTime.getValue());
    }

    /**
     * Do the start and end date conflict with any existing appointments?
     * @return true if conflict
     */
    private boolean appointmentHasConflict(){
        for(Appointment app : appointments){
            ZonedDateTime startTime = TimeService.convertToBusinessHoursZonedDateTime(app.getStartTime());
            ZonedDateTime endTime = TimeService.convertToBusinessHoursZonedDateTime(app.getEndTime());
            ZonedDateTime newStartTime = TimeService.convertToBusinessHoursZonedDateTime(dpStart.getValue(), cbStartTime.getValue());
            ZonedDateTime newEndTime = TimeService.convertToBusinessHoursZonedDateTime(dpEnd.getValue(), cbEndTime.getValue());

            if(newStartTime.isAfter(startTime) && newStartTime.isBefore(endTime)
                    || newEndTime.isBefore(endTime) && newEndTime.isAfter(startTime)){
                return true;
            }
        }
        return false;
    }
}
