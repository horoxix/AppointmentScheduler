package controllers;

import biz.TimeService;
import data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import models.*;
import util.Helper;

import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReportsController {
    private Scene scene;
    private final User user;
    private final ObservableList<Appointment> appointments;
    private final ObservableList<Contact> contacts;
    private final ObservableList<Division> divisions;
    private final ObservableList<Customer> customers;
    private final Dao<Contact> contactDao;
    private final Dao<Customer> customerDao;
    private final Dao<Country> countryDao;
    private final Dao<Division> divisionDao;
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

    /**
     * Constructor, creates the Reports Controller
     * @param user the current App User
     * @param appointments the current List of Appointments
     */
    public ReportsController(User user, ObservableList<Appointment> appointments){
        this.user = user;

        contactDao = new ContactDaoImpl(user);
        customerDao = new CustomerDaoImpl(user);
        countryDao = new CountryDaoImpl(user);
        divisionDao = new DivisionDaoImpl(user);

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
        cbDivision.setItems(divisions);
        cbMonth.setItems(Helper.getMonths());
        cbType.setItems(Helper.getAppointmentTypes());

        queryButton.setOnAction((event -> {
            try {
                handleQueryAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        cbDivision.getSelectionModel().selectedItemProperty().addListener((o, ol, nw) -> {
            Division d = cbDivision.getValue();
            handleDivisionChange(d);
        });

        cbContact.getSelectionModel().selectedItemProperty().addListener((o, ol, nw) -> {
            Contact c = cbContact.getValue();
            handleContactChange(c);
        });
    }

    @FXML
    protected void handleQueryAction(ActionEvent event) throws Exception {
        try {
            String type = cbType.getSelectionModel().getSelectedItem();
            String month = cbMonth.getSelectionModel().getSelectedItem();
            int count = 0;

            for(Appointment app : appointments){
                if(app.getType().equals(type)
                        && (TimeService.convertToLocalDate(app
                        .getStartTime())
                        .getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .equals(month))
                        || TimeService.convertToLocalDate(app
                        .getEndTime())
                        .getMonth()
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .equals(month)){
                    count ++;
                }
            }

            lblQueryResults.setText(String.valueOf(count));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleDivisionChange(Division division){
        lvCustomersByDivision.setItems(customers
                .stream()
                .filter(c -> (c.getDivisionId() == division.getId()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private void handleContactChange(Contact contact){
        tvAppointments.setItems(appointments
                .stream()
                .filter(a -> (a.getContactId() == contact.getId()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    };

}
