package controllers;

import data.Dao;
import models.Country;
import models.Customer;
import models.Division;
import models.User;
import data.CountryDaoImpl;
import data.CustomerDaoImpl;
import data.DivisionDaoImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ModifyCustomerController {
    private final Customer customer;
    private Scene scene;
    private ResourceBundle bundle;
    private ObservableList<Country> countries;
    private ObservableList<Division> divisions;
    private final Dao<Customer> customerDao;
    private final Dao<Country> countryDao;
    private final Dao<Division> divisionDao;
    private boolean invalidSaveState;

    @FXML
    private TextField customerName;

    @FXML
    private TextField address;

    @FXML
    private TextField postalCode;

    @FXML
    private TextField customerId;

    @FXML
    private TextField tfPhone;

    @FXML
    private ComboBox<Division> cbDivision;

    @FXML
    private ComboBox<Country> cbCountry;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    /**
     * Constructor, creates the Modify Customer Controller
     * @param customer the selected customer to modify
     * @param user the current App User
     */
    public ModifyCustomerController(Customer customer, User user){
        // Initialize user and customer list
        this.customer = customer;

        // Initialize countries and divisions
        countries = FXCollections.observableArrayList();
        divisions = FXCollections.observableArrayList();
        countryDao = new CountryDaoImpl(user);
        divisionDao = new DivisionDaoImpl(user);
        customerDao = new CustomerDaoImpl(user);
    }

    /**
     * Initialize Method, called after constructor
     */
    public void initialize(){
        bundle = ResourceBundle.getBundle("resources.UIResources");

        countries = countryDao.getAll();
        divisions = divisionDao.getAll();

        customerId.setText(String.valueOf(customer.getId()));
        customerName.setText(customer.getName());
        address.setText(customer.getAddress());
        postalCode.setText(customer.getPostalCode());
        tfPhone.setText(String.valueOf(customer.getPhone()));

        cbCountry.setItems(countries);
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

        cbCountry.getSelectionModel().selectedItemProperty().addListener((o, ol, nw) -> {
            Country c = (Country)cbCountry.getValue();
            handleCountryChange(c);
        });

        Optional<Country> oc = countries
                .stream()
                .filter(item -> (item.getId() == customer.getDivision().getCountryId()))
                .findFirst();

        oc.ifPresent(country -> cbCountry.getSelectionModel().select(country));

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
    }

    /**
     * Handles when country combo box value is changed
     * Updates Divisions with appropriate options
     * @param country new country that combo box has changed to
     */
    private void handleCountryChange(Country country){
        ObservableList<Division> filteredDivisionList = divisions
                .stream()
                .filter(division -> (division.getCountryId() == (country.getId())))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        // if results, enable combo box and set division items
        if(filteredDivisionList.size() > 0){
            cbDivision.setDisable(false);
            cbDivision.setItems(filteredDivisionList);
            cbDivision.getSelectionModel().select(0);
        }
        // if no results, disable division combo box
        else {
            cbDivision.setDisable(true);
        }
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
                    customer.setName(customerName.getText());
                    customer.setAddress(address.getText());
                    customer.setPhone(tfPhone.getText());
                    customer.setPostalCode(postalCode.getText());
                    customer.setDivisionId(cbDivision.getSelectionModel().getSelectedItem().getId());
                    customerDao.update(customer);
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
        if(invalidName()){
            errorList.append(bundle.getString("invalidName")).append("\n");
        }

        if(invalidAddress()){
            errorList.append(bundle.getString("invalidAddress")).append("\n");
        }

        if(invalidPhoneNumber()){
            errorList.append(bundle.getString("invalidPhone")).append("\n");
        }

        if(invalidPostalCode()){
            errorList.append(bundle.getString("invalidPostalCode")).append("\n");
        }

        if(invalidDivision()){
            errorList.append(bundle.getString("invalidDivision")).append("\n");
        }

        if(invalidCountry()){
            errorList.append(bundle.getString("invalidCountry")).append("\n");
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
     * Is the name invalid?
     * @return true if invalid
     */
    private boolean invalidName(){
        return customerName.getText().isEmpty();
    }

    /**
     * Is the phone number invalid?
     * @return true if invalid
     */
    private boolean invalidPhoneNumber(){
        return tfPhone.getText().isEmpty();
    }

    /**
     * Is the address invalid?
     * @return true if invalid
     */
    private boolean invalidAddress(){
        return address.getText().isEmpty();
    }

    /**
     * Is the postal code invalid?
     * @return true if invalid
     */
    private boolean invalidPostalCode(){
        return postalCode.getText().isEmpty();
    }

    /**
     * Is the division selected?
     * @return true if not selected
     */
    private boolean invalidDivision(){
        return cbDivision.getItems().size() > 0 && cbDivision.getSelectionModel().isEmpty();
    }

    /**
     * Is the country selected?
     * @return true if not selected
     */
    private boolean invalidCountry() { return cbCountry.getSelectionModel().isEmpty();}
}
