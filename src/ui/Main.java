package ui;

import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("resources.UIResources");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginForm.fxml"), bundle);
        LoginController controller = new LoginController();
        loader.setController(controller);

        // Set up scene to display
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle(bundle.getString("login"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
