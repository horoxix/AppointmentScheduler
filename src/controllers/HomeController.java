package controllers;

import biz.TimeService;
import models.Appointment;
import models.Customer;
import models.User;
import data.AppointmentDaoImpl;
import data.CustomerDaoImpl;
import data.Dao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeController {

    private ResourceBundle bundle;
    private final Dao<Appointment> appointmentDao;
    private final Dao<Customer> customerDao;
    private final User user;
    private Scene scene;
    private ObservableList<Appointment> appointments;
    private final ObservableList<Appointment> upcomingAppointments;
    private ObservableList<Customer> customers;

    @FXML
    private Button btnAddAppointment;

    @FXML
    private Button btnAddCustomer;

    @FXML
    private TableView<Appointment> tvAppointments;

    @FXML
    private TableView<Customer> tvCustomers;

    @FXML
    private RadioButton rbMonth;

    @FXML
    private RadioButton rbWeek;

    @FXML
    private RadioButton rbAll;

    @FXML
    private Label lblUpcoming;

    @FXML
    private ListView<Appointment> lvUpcoming;

    @FXML
    private Button viewReports;

    public HomeController(User user){
        this.user = user;
        upcomingAppointments = FXCollections.observableArrayList();
        appointmentDao = new AppointmentDaoImpl(user);
        customerDao = new CustomerDaoImpl(user);
    }

    private void initializeAppointments()
    {
        appointments = appointmentDao.getAll();
        loadAppointments();
    }

    private void loadCustomers()
    {
        customers = customerDao.getAll();
        tvCustomers.setItems(customers);
    }

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("resources.UIResources");

        rbAll.setSelected(true);
        initializeAppointments();
        loadCustomers();

        MenuItem deleteMenuItem = new MenuItem(bundle.getString("delete"));
        MenuItem updateMenuItem = new MenuItem(bundle.getString("update"));
        MenuItem deleteCustomerMenuItem = new MenuItem(bundle.getString("delete"));
        MenuItem updateCustomerMenuItem = new MenuItem(bundle.getString("update"));

        deleteMenuItem.setOnAction((ActionEvent event) -> {
            try {
                handleDeleteAppointmentAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        updateMenuItem.setOnAction((event -> {
            try {
                handleUpdateAppointmentAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        viewReports.setOnAction((event -> {
            try {
                handleViewReportsAction(event);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }));

        deleteCustomerMenuItem.setOnAction((ActionEvent event) -> {
            Object item = tvAppointments.getSelectionModel().getSelectedItem();
        });

        updateCustomerMenuItem.setOnAction((event -> {
            try {
                handleUpdateCustomerAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        rbWeek.setOnAction((event -> {
            try {
                handleRadioButtonSelected(event);
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        rbMonth.setOnAction((event -> {
            try {
                handleRadioButtonSelected(event);
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        rbAll.setOnAction((event -> {
            try {
                handleRadioButtonSelected(event);
            } catch (Exception e){
                e.printStackTrace();
            }
        }));

        ContextMenu menu = new ContextMenu();
        menu.getItems().add(deleteMenuItem);
        menu.getItems().add(updateMenuItem);
        tvAppointments.setContextMenu(menu);

        ContextMenu customerMenu = new ContextMenu();
        customerMenu.getItems().add(deleteCustomerMenuItem);
        customerMenu.getItems().add(updateCustomerMenuItem);
        tvCustomers.setContextMenu(customerMenu);

        btnAddAppointment.setOnAction(event -> {
            try {
                handleAddAppointmentAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnAddCustomer.setOnAction(event -> {
            try {
                handleAddCustomerAction(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void checkForUpcomingAppointments(){
        StringBuilder sb = new StringBuilder();
        for(Appointment app : appointments){
            if(TimeService.convertToZonedDateTime(app.getStartTime())
                    .isAfter(ZonedDateTime.now()) && TimeService.convertToZonedDateTime(app.getStartTime())
                    .isBefore(TimeService.convertToZonedDateTime(app.getStartTime()).plusMinutes(15))){
                upcomingAppointments.add(app);
                sb.append(app.getId())
                        .append(" - ")
                        .append(app.getTitle())
                        .append(" ")
                        .append(bundle.getString("reminderAlertString"))
                        .append(" ")
                        .append(app.getStartTime())
                        .append(".");
            }
        }
        if(upcomingAppointments.size() > 0){
            lvUpcoming.setCellFactory(param -> new ListCell<Appointment>() {
                @Override
                protected void updateItem(Appointment app, boolean empty) {
                    super.updateItem(app, empty);

                    if (empty || app == null || app.getTitle() == null) {
                        setText(null);
                    } else {
                        setText(app.getId() + " - " + app.getTitle() + " " + bundle.getString("reminderAlertString") + " " + app.getStartTime() + ".");
                    }
                }
            });

            lblUpcoming.setStyle("-fx-text-fill: red ;");
            lvUpcoming.setItems(upcomingAppointments);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(sb.toString());
            alert.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("There are no upcoming appointments.");
            alert.show();
        }
    }

    @FXML
    protected void handleAddAppointmentAction(ActionEvent event) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddAppointmentForm.fxml"), bundle);
            AddAppointmentController controller = new AddAppointmentController(user, appointments);
            loader.setController(controller);

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("addAppointment"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            controller.setScene(scene);
            stage.show();

            // Bind window closing event
            stage.setOnHiding(windowEvent -> {
                tvAppointments.refresh();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    protected void handleAddCustomerAction(ActionEvent event) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddCustomerForm.fxml"), bundle);
            AddCustomerController controller = new AddCustomerController(user, customers);
            loader.setController(controller);

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("addCustomer"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            controller.setScene(scene);
            stage.show();

            // Bind window closing event
            stage.setOnHiding(windowEvent -> {
                tvCustomers.refresh();
            });
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    protected void handleUpdateCustomerAction(ActionEvent event) throws Exception {
        try {
            Customer customer = tvCustomers.getSelectionModel().getSelectedItem();
            if(customer == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(bundle.getString("selectCustomerErrorMessage"));
                alert.show();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ModifyCustomerForm.fxml"), bundle);
            ModifyCustomerController controller = new ModifyCustomerController(customer, user);
            loader.setController(controller);

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("modifyCustomer"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            controller.setScene(scene);
            stage.show();

            // Bind window closing event
            stage.setOnHiding(windowEvent -> {
                tvCustomers.refresh();
            });
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    protected void handleUpdateAppointmentAction(ActionEvent event) throws Exception {
        try {
            Appointment appointment = tvAppointments.getSelectionModel().getSelectedItem();
            if(appointment == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(bundle.getString("selectAppointmentErrorMessage"));
                alert.show();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ModifyAppointmentForm.fxml"), bundle);
            ModifyAppointmentController controller = new ModifyAppointmentController(appointment, user, appointments);
            loader.setController(controller);

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("modifyAppointment"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            controller.setScene(scene);
            stage.show();

            // Bind window closing event
            stage.setOnHiding(windowEvent -> {
                tvAppointments.refresh();
            });
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void handleDeleteAppointmentAction(ActionEvent event) throws Exception {
        try {
            Appointment appointment = tvAppointments.getSelectionModel().getSelectedItem();
            if(appointment == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(bundle.getString("selectAppointmentErrorMessage"));
                alert.show();
            }
            else {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete " + appointment.getTitle() + "?",
                        ButtonType.YES,
                        ButtonType.NO,
                        ButtonType.CANCEL);

                confirmationAlert.showAndWait();
                if(confirmationAlert.getResult() == ButtonType.YES){
                    appointmentDao.delete(appointment);
                    appointments.remove(appointment);
                    tvAppointments.refresh();
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    protected void handleViewReportsAction(ActionEvent event) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/ReportsForm.fxml"), bundle);
            ReportsController controller = new ReportsController(user, appointments);
            loader.setController(controller);

            Stage stage = new Stage();
            stage.setTitle(bundle.getString("viewReports"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            controller.setScene(scene);
            stage.show();

            // Bind window closing event
            stage.setOnHiding(windowEvent -> {
                tvCustomers.refresh();
            });
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Sets the scene value to use
     * @param scene to set controller to reference
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    };

    /**
     * On radio button selected, determines which information to display based on Part instance type
     * @param event action event of button click
     * @throws Exception possible Exception thrown
     */
    @FXML
    protected void handleRadioButtonSelected(ActionEvent event) throws Exception {
        loadAppointments();
    }

    private void loadAppointments(){
        if(rbAll.isSelected()){
            loadAllAppointments();
        }
        else if (rbMonth.isSelected()){
            loadMonthlyAppointments();
        }
        else {
            loadWeeklyAppointments();
        }
    }

    private void loadAllAppointments(){
        tvAppointments.setItems(appointments);
    }

    private void loadMonthlyAppointments(){
        tvAppointments.setItems(appointments
                .stream()
                .filter(app -> (TimeService.isCurrentMonth(TimeService.convertToZonedDateTime(app.getStartTime()))))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private void loadWeeklyAppointments(){
        tvAppointments.setItems(appointments
                .stream()
                .filter(app -> (TimeService.isCurrentWeek(TimeService.convertToZonedDateTime(app.getStartTime()))))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }
}
