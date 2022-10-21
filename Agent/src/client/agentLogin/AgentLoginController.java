package client.agentLogin;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import client.agentMainPage.AgentMainPageController;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

import static contants.AgentConstants.MAIN_PAGE_FXML_RESOURCE_LOCATION;
import static util.CommonConstants.*;

public class AgentLoginController {


    @FXML
    private TextField userNameTextField;

    @FXML
    private Button loginButton;
    @FXML
    private ComboBox<String> alliesComboBox;


    @FXML
    private Label errorMessageLabel;
    private StringProperty errorMessageProperty;
    private SimpleIntegerProperty threadAmount;
    private AlliesRefresher alliesRefresher;

    @FXML
    private Slider threadsAmountSlider;
    @FXML
    private TextField missionAmount;
    private Stage primaryStage;
    private SimpleBooleanProperty autoUpdate;
    private SimpleStringProperty chosenAllieNameChoiceBox;
    private AgentInfoDTO agentInfoDTO;
    private int missionAmountInteger;
    private StringProperty userName;
    private Timer timer;
    private AgentMainPageController mainPageController;
    private Scene AgentScene;

    public AgentLoginController() {
        this.errorMessageProperty = new SimpleStringProperty();
        this.threadAmount = new SimpleIntegerProperty();
        this.autoUpdate = new SimpleBooleanProperty(true);
        chosenAllieNameChoiceBox = new SimpleStringProperty();
        userName = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        threadAmount.bind(threadsAmountSlider.valueProperty());
        userName.bind(userNameTextField.textProperty());
        missionAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    missionAmount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        HttpClientUtil.setCookieManagerLoggingFacility(line ->
                Platform.runLater(() ->
                        updateHttpStatusLine(line)));
        fetchAllAllies();

    }

    private void updateHttpStatusLine(String data) {
        //chatAppMainController.updateHttpLine(data);
    }

    private void fetchAllAllies() {
        alliesRefresher = new AlliesRefresher(
                autoUpdate,
                (responseBody) ->
                        Platform.runLater(() ->
                                errorMessageProperty.set(responseBody)),
                this::updateAllies);
        timer = new Timer();
        timer.schedule(alliesRefresher, REFRESH_RATE, REFRESH_RATE);

    }

    public void updateAllies(List<AlliesDetailDTO> alliesDetailDTOS) {
        ObservableList<String> items = alliesComboBox.getItems();
        List<String> allieNameList = alliesDetailDTOS.stream().map(AlliesDetailDTO::getTeamName).collect(Collectors.toList());
        //commonUtilsFunctions.listEqualsIgnoreOrder(allieNameList,items);
        if (!allieNameList.equals(items)) {
            items.clear();
            items.addAll(allieNameList);
        }
    }

    private boolean isFormValid() {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return false;
        }
        String chosenAllieName = chosenAllieNameChoiceBox.getValue();
        if (chosenAllieName == null) {
            errorMessageProperty.set("It is not possible to register without selecting a group");
            return false;
        }
        int missionAmountParse;
        try {
            missionAmountParse = Integer.parseInt(missionAmount.getText());
            if (missionAmountParse < 1) {
                errorMessageProperty.set("It is not possible to register without a positive amount of mission");
                return false;
            }
        } catch (NumberFormatException ex) {
            errorMessageProperty.set("It is not possible to register without a positive amount of mission");
            return false;
        }
        return true;
    }

    @FXML
    void loginButtonClicked(ActionEvent event) {
        if (!isFormValid())
            return;


        missionAmountInteger = Integer.parseInt(missionAmount.getText());
        agentInfoDTO = new AgentInfoDTO(userName.getValue(), chosenAllieNameChoiceBox.getValue(), threadAmount.get(), missionAmountInteger);
        String jsonAgentInfoDTO = GSON_INSTANCE.toJson(agentInfoDTO);

        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("jsonAgentInfoDTO", jsonAgentInfoDTO)
                        .build();

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(CommonConstants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName.getValue())
                .addQueryParameter("client_type", "Agent")
                .build()
                .toString();


        HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
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
                    String needToActiveResponse = response.body().string();

                    Platform.runLater(() -> {
                        mainPageController.setAgentInfo(agentInfoDTO);
                        boolean needToProcess = needToActiveResponse.equals("true");
                        mainPageController.updateAgentStatus(needToProcess);
                        //if need to active start refresher
                        primaryStage.setScene(AgentScene);
                        primaryStage.show();
                        System.out.println(needToProcess);
                        // primaryStage.setScene(alliesScene);
                        //primaryStage.show();
                        //alliesController.setUserName(userName);
                        //alliesController.startServerUpdateTimer();
                    });
                }
            }
        });
    }

    @FXML
    void quitButtonClicked(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    public void alertShowException(Exception e) {
        List<Exception> exceptionList = new ArrayList<>();
        exceptionList.add(e);
        showListOfExceptions(exceptionList);
    }

    private void showListOfExceptions(List<Exception> exceptionList) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        String errorMessage = "";
        int amountOfMessageShowed = 0;
        for (Exception message : exceptionList) {
            errorMessage = errorMessage + message.getMessage() + "\n";
            amountOfMessageShowed++;
            if (amountOfMessageShowed > MAX_AMOUNT_ERROR_TO_SHOW)
                break;
        }
        alert.setContentText(errorMessage + "\n");
        alert.setTitle("Error!");
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }


    public void backgroundLoadUBoatScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL UBoatScreenURL = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
        fxmlLoader.setLocation(UBoatScreenURL);
        try {
            Parent AgentScreen = fxmlLoader.load(UBoatScreenURL.openStream());
            mainPageController = fxmlLoader.getController();
            mainPageController.setPrimaryStage(primaryStage);
            primaryStage.setTitle("Agent Client");
            AgentScene = new Scene(AgentScreen, 700,500);
            primaryStage.setMinHeight(300f);
            primaryStage.setMinWidth(400f);
        } catch (IOException ignore) {
            ignore.printStackTrace();
        }
    }

    @FXML
    void alliesComboBoxAction(ActionEvent event) {
        String selectedAllieName =  alliesComboBox.getSelectionModel().getSelectedItem();
        if(selectedAllieName != null){
            chosenAllieNameChoiceBox.set(selectedAllieName);
        }
    }
}
