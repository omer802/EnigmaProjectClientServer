package client.javafx.Login;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static client.constants.AlliesConstants.MAIN_PAGE_FXML_RESOURCE_LOCATION;


public class AlliesLoginController {

    private Scene alliesScene;
    private client.javafx.allies.alliesController alliesController;
    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private Stage primaryStage;

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        HttpClientUtil.setCookieManagerLoggingFacility(line ->
                Platform.runLater(() ->
                       updateHttpStatusLine(line)));
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(CommonConstants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("client_type","Allie")
                .build()
                .toString();


        updateHttpStatusLine("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                errorMessageProperty.set("faild call :Something went wrong: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        primaryStage.setScene(alliesScene);
                        primaryStage.show();
                        alliesController.setUserName(userName);
                        alliesController.startServerUpdateTimer();
                    });
                }
            }
        });
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

    private void updateHttpStatusLine(String data) {
        //chatAppMainController.updateHttpLine(data);
    }

    public void backgroundLoadUBoatScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL alliesScreenURL = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(alliesScreenURL);
        try {
            Parent AllieScreenToShow = fxmlLoader.load(alliesScreenURL.openStream());
            alliesController = fxmlLoader.getController();
            alliesController.setPrimaryStage(primaryStage);
            primaryStage.setTitle("Allies Client");
            alliesScene = new Scene(AllieScreenToShow, 800,600);
            primaryStage.setMinHeight(300f);
            primaryStage.setMinWidth(400f);
        }
        catch (IOException ignore){ignore.printStackTrace();}
    }

}
