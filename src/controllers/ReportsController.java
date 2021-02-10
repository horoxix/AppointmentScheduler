package controllers;

import biz.TimeService;
import data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import models.*;
import util.Helper;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ReportsController {

    private Scene scene;
    private final ObservableList<Appointment> appointments;
    private final ObservableList<Contact> contacts;
    private final ObservableList<Division> divisions;
    private final ObservableList<Customer> customers;
    private final ObservableList<Country> countries;
    private boolean invalidSaveState;

    @FXML
    private ComboBox<String> cbType;

    @FXML
    private ComboBox<String> cbMonth;

    @FXML
    private Button queryButton;

    @FXML
    private Label lblQueryResults;

    @FXML
    private ComboBox<Contact> cbContact;

    @FXML
    private TableView<Appointment> tvAppointments;

    @FXML
    private ComboBox<Division> cbDivision;

    @FXML
    private ListView<Customer> lvCustomersByDivision;

    @FXML
    private ComboBox<Country> cbCountry;

    /**
     * Constructor, creates the Reports Controller
     * @param user the current App User
     * @param appointments the current List of Appointments
     */
    public ReportsController(User user, ObservableList<Appointment> appointments){
        Dao<Contact> contactDao = new ContactDaoImpl(user);
        Dao<Customer> customerDao = new CustomerDaoImpl(user);
        Dao<Country> countryDao = new CountryDaoImpl(user);
        Dao<Division> divisionDao = new DivisionDaoImpl(user);

        countries = countryDao.getAll();
        contacts = contactDao.getAll();
        divisions = divisionDao.getAll();
        customers = customerDao.getAll();
        this.appointments = appointments;
    }

    /**
     * Initialize Method, called after constructor
     */
    @FXML
    public void initialize() {
        cbContact.setItems(contacts);
        cbMonth.setItems(Helper.getMonths());
        cbType.setItems(Helper.getAppointmentTypes());

        lvCustomersByDivision.setPlaceholder(Helper.getNoResultsLabel());
        tvAppointments.setPlaceholder(Helper.getNoResultsLabel());

        cbCountry.setItems(countries);
        cbCountry.getSelectionModel().select(0);
        StringConverter<Country> converter = new StringConverter<Country>() {
            @Override
            public String toString(Country country) {
                return country.getName();
            }

            @Override
            public Country fromString(String id) {
                return countries.stream()
                        .filter(item -> String.valueOf(item.getId()).equals(id))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)).get(0);
            }
        };
        cbCountry.setConverter(converter);

        initializeBindings();
    }

    /**
     * Initializes button/ui bindings.
     */
    private void initializeBindings(){
        queryButton.setOnAction((event -> {
            try {
                handleQueryAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        cbCountry.getSelectionModel().selectedItemProperty().addListener((o, ol, nw) -> {
            Country c = (Country)cbCountry.getValue();
            handleCountryChange(c);
        });

        cbDivision.getSelectionModel().selectedItemProperty().addListener((o, ol, nw) -> {
            if(cbDivision.getValue() != null){
                Division d = cbDivision.getValue();
                handleDivisionChange(d);
            }
        });

        cbContact.getSelectionModel().selectedItemProperty().addListener((o, ol, nw) -> {
            Contact c = cbContact.getValue();
            handleContactChange(c);
        });
    }

    /**
     * Handles the Query Button click
     * Loads total amount of appointments by Type and Month
     * Using forEach lambda expression to easily, and cleanly go through the appointment list
     * and check for each valid appointment
     * @param event Button Click Event
     * @throws Exception possible exception thrown
     */
    @FXML
    protected void handleQueryAction(ActionEvent event) throws Exception {
        try {
            String type = cbType.getSelectionModel().getSelectedItem();
            String month = cbMonth.getSelectionModel().getSelectedItem();
            AtomicInteger count = new AtomicInteger();

            appointments.forEach((app) -> {
                if(app.getType().equals(type)
                        && (TimeService.convertToLocalDate(app
                        .getStartTime())
                        .getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .equals(month)
                        || TimeService.convertToLocalDate(app
                        .getEndTime())
                        .getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .equals(month))){
                    count.getAndIncrement();
                }
            });

            lblQueryResults.setText(String.valueOf(count.get()));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles when division combo box value is changed
     * Updates Division Report List with appropriate customers
     * Using lambda in the filter method to compare each divisionId with the division.getId
     * @param division new division that combo box has changed to
     */
    private void handleDivisionChange(Division division){
        lvCustomersByDivision.setItems(customers
                .stream()
                .filter(c -> (c.getDivisionId() == division.getId()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    /**
     * Handles when contact combo box value is changed
     * Updates appointment schedule list with appropriate appointments
     * Using lambda in the filter method to compare each divisionId with the division.getId
     * @param contact new contact that combo box has changed to
     */
    private void handleContactChange(Contact contact){
        tvAppointments.setItems(appointments
                .stream()
                .filter(a -> (a.getContactId() == contact.getId()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    /**
     * Handles when country combo box value is changed
     * Updates Divisions with appropriate options
     * Using lambda in the filter method to compare each divisionId with the division.getId
     * @param country new country that combo box has changed to
     */
    private void handleCountryChange(Country country){

        ObservableList<Division> filteredDivisionList = divisions
                .stream()
                .filter(division -> (division.getCountryId() == (country.getId())))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        lvCustomersByDivision.getItems().clear();

        cbDivision.setDisable(filteredDivisionList.size() <= 0);
        cbDivision.setItems(filteredDivisionList);
        cbDivision.getSelectionModel().select(0);
    }

    /**
     * Sets the scene value to use
     * @param scene to set controller to reference
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    };

}
