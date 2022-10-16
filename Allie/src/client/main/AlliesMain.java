package client.main;

import client.javafx.Login.AlliesLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import static client.constants.AlliesConstants.LOGIN_PAGE_CSS_RESOURCE_LOCATION;
import static client.constants.AlliesConstants.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

public class AlliesMain  extends Application {

    private AlliesLoginController loginController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinHeight(100);
        primaryStage.setMinWidth(200);
        primaryStage.setTitle("Allies Login");
        URL loginPage = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPage);
            Parent root = fxmlLoader.load();
            loginController = fxmlLoader.getController();
            loginController.backgroundLoadUBoatScreen(primaryStage);
            Scene scene = new Scene(root, 303, 192);
            scene.getStylesheets().add(getClass().getResource(LOGIN_PAGE_CSS_RESOURCE_LOCATION).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void stop() throws Exception {
        //HttpClientUtil.shutdown();
    }

    public static void main(String[] args) {launch(args);}

}
