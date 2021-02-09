package controllers;

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
import java.util.stream.Collectors;

public class ModifyCustomerController {
    private final Customer customer;
    private Scene scene;
    private ObservableList<Country> countries;
    private ObservableList<Division> divisions;
    private final CustomerDaoImpl customerDao;
    private final CountryDaoImpl countryDao;
    private final DivisionDaoImpl divisionDao;
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

    public void initialize(){
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

    private void handleCountryChange(Country country){
        cbDivision.setItems(divisions
                .stream()
                .filter(division -> (division.getCountryId() == (country.getId())))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        cbDivision.getSelectionModel().select(0);
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
                    customer.setName(customerName.getText());
                    customer.setAddress(address.getText());
                    customer.setPhone(tfPhone.getText());
                    customer.setPostalCode(postalCode.getText());
                    customer.setDivisionId(cbDivision.getSelectionModel().getSelectedItem().getId());
                    customerDao.update(customer);
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
        if(invalidName()){
            errorList.append(" - Please enter a valid Name.\n");
        }

        if(invalidAddress()){
            errorList.append(" - Please enter a valid Address.\n");
        }

        if(invalidPhoneNumber()){
            errorList.append(" - Please enter a valid Phone Number.\n");
        }

        if(invalidPostalCode()){
            errorList.append(" - Please enter a valid Postal Code.\n");
        }

        if(invalidDivision()){
            errorList.append(" - Please select a Division.\n");
        }

        if(invalidCountry()){
            errorList.append(" - Please select a Country.\n");
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

    private boolean invalidName(){
        return customerName.getText().isEmpty();
    }

    private boolean invalidPhoneNumber(){
        return tfPhone.getText().isEmpty();
    }

    private boolean invalidAddress(){
        return address.getText().isEmpty();
    }

    private boolean invalidPostalCode(){
        return postalCode.getText().isEmpty();
    }

    private boolean invalidDivision(){
        return cbDivision.getSelectionModel().isEmpty();
    }

    private boolean invalidCountry() { return cbCountry.getSelectionModel().isEmpty();}
}
