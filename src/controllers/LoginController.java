package controllers;

import biz.LoggingService;
import models.User;
import data.UserDaoImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController
{
    private Scene scene;
    private final UserDaoImpl userDao;
    private ResourceBundle bundle;

    @FXML
    private TextField tfUserName;

    @FXML
    private TextField tfPassword;

    @FXML
    private Label lblUsernameError;

    @FXML
    private Label lblPasswordError;

    @FXML
    private Label lblLocation;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    /**
     * Constructor, creates theLogin Controller
     */
    public LoginController(){
        userDao = new UserDaoImpl();
    }

    /**
     * Initialize Method, called after constructor
     */
    @FXML
    public void initialize(){
        bundle = ResourceBundle.getBundle("resources.UIResources");
        lblLocation.setText(ZoneId.systemDefault().toString());

        initializeBindings();
    }

    /**
     * Initializes button/ui bindings.
     */
    private void initializeBindings(){
        btnOk.setOnAction((event -> {
            try {
                handleOkButtonAction(event);
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
     * Sets the scene value to use
     * @param scene to set controller to reference
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    };

    /**
     * On OK button action, verifies login
     * If successful, logs in. Writes successful log
     * If failure, displays message. Writes failure log
     * @param event action event of button click
     * @throws Exception possible Exception thrown
     */
    @FXML
    protected void handleOkButtonAction(ActionEvent event) throws Exception {

        Optional<User> userOptional = userDao.getByUsername(tfUserName.getText());
        if(userOptional.isPresent()){
            lblUsernameError.setText("");

            User user = userOptional.get();
            if(!tfPassword.getText().equals(user.getPassword())){
                lblPasswordError.setText(bundle.getString("invalidPassword"));
                LoggingService.LogInfo("Invalid Password.");
            }
            else {
                lblPasswordError.setText("");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/HomeForm.fxml"), bundle);
                HomeController controller = new HomeController(user);
                loader.setController(controller);

                Stage stage = new Stage();
                stage.setTitle(bundle.getString("home"));
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                controller.setScene(scene);
                stage.show();
                controller.checkForUpcomingAppointments();
                LoggingService.LogInfo(user.getUserName() + " has successfully logged in.");
                closeWindow();

            }
        }
        else {
            lblUsernameError.setText(bundle.getString("invalidUsername"));
            LoggingService.LogInfo(tfUserName.getText() + " is an Invalid Username.");
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
        Stage stage = (Stage)btnOk.getScene().getWindow();
        stage.close();
    }
}
