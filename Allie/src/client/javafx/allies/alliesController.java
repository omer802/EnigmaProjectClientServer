package client.javafx.allies;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import client.constants.AlliesConstants;
import client.javafx.contestPage.contestDataSmall.contestDataSmallController;
import client.javafx.contestPage.teamsDetails.TeamsDetailsController;
import client.javafx.contestsData.ContestsRefresher;
import client.javafx.contestsData.contestsDataController;
import client.javafx.teamsAgentsData.AgentDetail;
import client.javafx.teamsAgentsData.teamsAgentsDataController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static client.constants.AlliesConstants.REFRESH_RATE;
import static util.CommonConstants.GSON_INSTANCE;
import static util.CommonConstants.MAX_AMOUNT_ERROR_TO_SHOW;

public class alliesController {

    @FXML
    private Label userGreetingLabel;

    @FXML
    private TableView<AgentDetail> teamAgentsTableView;
    @FXML
    private teamsAgentsDataController teamAgentsTableViewController;

    @FXML
    private TableView<ContestInformationDTO> contestsDataTableView;
    @FXML
    private contestsDataController contestsDataTableViewController;
    @FXML
    private TableView<ContestInformationDTO> chosenContest;

    @FXML
    private contestDataSmallController chosenContestController;

    @FXML
    private TableView<AlliesDetailDTO> participantTeams;

    @FXML
    private TeamsDetailsController participantTeamsController;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Label stringToHackLabel;
    private SimpleStringProperty stringToHack = new SimpleStringProperty("");
    private  StringProperty errorMessageProperty = new SimpleStringProperty();

    private SimpleStringProperty userName;
    private Stage primaryStage;
    private SimpleBooleanProperty isActiveContest = new SimpleBooleanProperty(false);
    @FXML
    private Spinner<Integer> SpinnerMissionSize;
    private int missionSize;

    @FXML
    private Button Submit;
    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        userName = new SimpleStringProperty();
        contestsDataTableViewController.setErrorHandlerMainController(this::alertShowException);
        contestsDataTableViewController.setChosenContestController(chosenContestController);
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userName));
        stringToHackLabel.textProperty().bind(Bindings.concat("string to hack: ", stringToHack));
        stringToHackLabel.visibleProperty().bind(isActiveContest);
        contestsDataTableViewController.setMainController(this);
        setSpinner();
    }

    private void setSpinner() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE);
        valueFactory.setValue(1);
        SpinnerMissionSize.setValueFactory(valueFactory);
        SpinnerMissionSize.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {

                missionSize = newValue;
            }
        });
        EventHandler<KeyEvent> enterKeyEventHandler;

        enterKeyEventHandler = new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                // handle users "enter key event"
                if (event.getCode() == KeyCode.ENTER) {

                    try {
                        // yes, using exception for control is a bad solution ;-)
                        Integer.parseInt(SpinnerMissionSize.getEditor().textProperty().get());
                    }
                    catch (NumberFormatException e) {

                        // show message to user: "only numbers allowed"

                        // reset editor to INITAL_VALUE
                        SpinnerMissionSize.getEditor().textProperty().set("1");
                    }
                }
            }

        };
        SpinnerMissionSize.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, enterKeyEventHandler);
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void alertShowException(Exception e){
        Platform.runLater(() -> { List<Exception> exceptionList = new ArrayList<>();
        exceptionList.add(e);
        showListOfExceptions(exceptionList);
        }
        );

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

    public void startServerUpdateTimer() {
        contestsDataTableViewController.startListRefresher();

    }
    /*public void startAgentRefresher() {
        contestRefresher = new ContestsRefresher(
                autoUpdate,
                httpRequestLoggerConsumer,
                this::updateContests);
        timer = new Timer();
        timer.schedule(contestRefresher, REFRESH_RATE, REFRESH_RATE);
    }*/

    @FXML
    private void setChosenContest(ActionEvent event) {
        ContestInformationDTO chosenContest = contestsDataTableViewController.getChosenContest();
        if(chosenContest!=null)
            sendChosenContestToServer(chosenContest);
    }

    private void sendChosenContestToServer(ContestInformationDTO chosenContest) {

            String jsonChosenContestDTO = GSON_INSTANCE.toJson(chosenContest);
            RequestBody body =
                    new MultipartBody.Builder()
                            .addFormDataPart("jsonChosenContestDTO", jsonChosenContestDTO)
                            .build();
            Request finalUrl = new Request.Builder()
                    .url(AlliesConstants.UPDATE_ALLIE_CHOSEN_CONTEST)
                    .post(body)
                    .build();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            alertShowException(new RuntimeException("Something went wrong:\n " + e.getMessage()))
                    );
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
                            errorMessageProperty.set("Set Contest successfully ");
                            chosenContestController.setChosenContests(chosenContest);
                            contestsDataTableViewController.setChosenContestDTO(chosenContest);
                            participantTeamsController.startListRefresher();
                        });
                    }
                }
            });
        }


}

