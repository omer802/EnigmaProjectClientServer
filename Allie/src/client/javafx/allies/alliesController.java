package client.javafx.allies;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import client.constants.AlliesConstants;
import client.javafx.contestPage.AgentsProgressAndDM.AgentProgressController;
import client.javafx.contestPage.IsUBoatLogoutRefresher;
import client.javafx.contestPage.candidate.CandidateController;
import client.javafx.contestPage.contestDataSmall.contestDataSmallController;
import client.javafx.contestPage.teamsDetails.TeamsDetailsController;
import client.javafx.contestsData.ContestStatusRefresher;
import client.javafx.contestsData.contestsDataController;
import client.javafx.teamsAgentsData.teamsAgentsDataController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import registerManagers.clients.Allie;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static util.CommonConstants.*;

public class alliesController {

    @FXML
    private Label userGreetingLabel;

    @FXML
    private TableView<AgentInfoDTO> teamAgentsTableView;
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
    private TableView<CandidateDTO> candidates;

    @FXML
    private CandidateController candidatesController;
    @FXML
    private AnchorPane agentAndDMProgress;
    @FXML
    private AgentProgressController agentAndDMProgressController;
    @FXML
    private Tab dashboardTab;
    @FXML
    private Tab contestTab;


    @FXML
    private Label contestErrorLabel;
    private final SimpleStringProperty contestStatusAndErrorsProperty = new SimpleStringProperty("Press ready to start game");
    private final SimpleStringProperty errorUpdateMissionProperty = new SimpleStringProperty();
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Label stringToHackLabel;
    private SimpleStringProperty stringToHack = new SimpleStringProperty("");
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    private SimpleStringProperty userName;
    private Stage primaryStage;
    private final SimpleBooleanProperty isActiveContest = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isReady = new SimpleBooleanProperty(false);
    @FXML
    private Spinner<Integer> SpinnerMissionSize;
    private int missionSize = 1;
    private final SimpleBooleanProperty shouldUpdate = new SimpleBooleanProperty(true);
    private SimpleIntegerProperty agentAmount =new SimpleIntegerProperty(0);
    private Allie.AllieStatus allieStatus;
    @FXML
    private Button Submit;
    @FXML
    private Button readyButton;
    @FXML
    private Button updateMissionSize;
    private Timer timerContestStatusRefresher;
    private boolean isFirstConfig = true;
    private SimpleBooleanProperty isSigned = new SimpleBooleanProperty(false);
    @FXML
    private TabPane tabPaneManager;
    private SimpleBooleanProperty UBoatLogoutAction = new SimpleBooleanProperty(false);

    private Timer isLogoutTimer;
    private IsUBoatLogoutRefresher isUBoatLogoutRefresher;
    @FXML
    public void initialize() {
        contestErrorLabel.textProperty().bind(contestStatusAndErrorsProperty);
        errorMessageLabel.textProperty().bind(errorMessageProperty);
        userName = new SimpleStringProperty();
        contestsDataTableViewController.setErrorHandlerMainController(this::alertShowException);
        contestsDataTableViewController.setChosenContestController(chosenContestController);
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userName));
        stringToHackLabel.textProperty().bind(Bindings.concat("string to hack: ", stringToHack));
        stringToHackLabel.setVisible(false);
        contestsDataTableViewController.setMainController(this);
        candidatesController.setErrorHandlerMainController(this::alertShowException);
        candidatesController.setActiveContestAndAgentsAmountProperty(isActiveContest);
        agentAndDMProgressController.setStringToHackAndActiveContestProperty(isActiveContest,stringToHack,this::alertShowException);
        readyButton.disableProperty().bind(isReady);
        updateMissionSize.disableProperty().bind(isReady);

        dashboardTab.disableProperty().bind(isSigned);
        contestTab.disableProperty().bind(isSigned.not());
        isSigned.addListener((observable, oldValue, newValue) ->{
            if(newValue){
                tabPaneManager.getSelectionModel().select(contestTab);
            }else
                tabPaneManager.getSelectionModel().select(dashboardTab);
        });

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
    @FXML
    private void makeAllieReadyAction(ActionEvent event){
        if(agentAmount.getValue() < 1){
            contestStatusAndErrorsProperty.set("Allie can't be ready without agents");

        }
        else{
            String finalUrl = HttpUrl
                    .parse(CommonConstants.MAKE_CLIENT_READY)
                    .newBuilder()
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> contestStatusAndErrorsProperty.set("Something went wrong:" + e.getMessage()));

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String bodyResponse = response.body().string();
                    if (response.code() != 200) {
                        Platform.runLater(() -> contestStatusAndErrorsProperty.set("Something went wrong:" + bodyResponse));

                    } else {
                        Platform.runLater(() -> {
                            isReady.set(true);
                            startContestRefresher();

                        });
                    }
                }

            });
        }
    }
    public void startContestRefresher(){

        candidatesController.startCandidateRefresher();
        isFirstConfig = true;
        ContestStatusRefresher contestStatusRefresher = new ContestStatusRefresher(isReady,this::alertShowException,this::updateCurrentState);
        timerContestStatusRefresher = new Timer();
        timerContestStatusRefresher.schedule(contestStatusRefresher,REFRESH_RATE,REFRESH_RATE);
    }
    public void updateCurrentState(Allie.AllieStatus allieStatus){
        this.allieStatus = allieStatus;
        switch (allieStatus){
            case IDLE:
                break;
            case READY:
                Platform.runLater(()->{isReady.set(true);
                contestStatusAndErrorsProperty.set("Ready, Waiting for contest to start...");
                });
                break;
            case IN_CONTEST:
                if(isFirstConfig) {
                    Platform.runLater(()-> {
                        contestStatusAndErrorsProperty.set("In active game...");
                        stringToHackLabel.setVisible(true);
                        isActiveContest.set(true);
                        isFirstConfig = false;
                    });
                }
                break;
            case FINISHED_CONTEST:
                Platform.runLater(()-> {
                    stringToHackLabel.setVisible(true);
                    finishGame();
                });
                break;
        }
    }
    private void finishGame(){
        isReady.set(false);
        isActiveContest.set(false);
        contestStatusAndErrorsProperty.set("finished...");
        try {
            getWinnerFromSever();
        } catch (IOException e) {
            e.printStackTrace();
        }
        contestStatusAndErrorsProperty.set("Press ready to start game");
    }
    private void signoutFromUBoat(){
        HttpClientUtil.runAsync(CommonConstants.REST_CONTEST, new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        alertShowException(new RuntimeException("failed call :Something went wrong: " + e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String winnerResponse = response.body().string();
                        if (!response.isSuccessful()) {
                            alertShowException(new RuntimeException("Something went wrong:" + winnerResponse));
                        } else {
                            Platform.runLater(() -> {
                              // move to before contest page

                            });
                        }
                    }
                }
        );
    }

    private void getWinnerFromSever() throws IOException {

        HttpClientUtil.runAsync(CommonConstants.GET_WINNER, new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        alertShowException(new RuntimeException("failed call :Something went wrong: " + e.getMessage()));
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String winnerResponse = response.body().string();
                        if (winnerResponse.equals("null")) {
                            cleanContestData();
                            signoutFromUBoat();
                            return;
                        }
                        if (!response.isSuccessful()) {
                            alertShowException(new RuntimeException("Something went wrong:" + winnerResponse));
                        } else {
                            Platform.runLater(() -> {
                                CandidateDTO candidatesDTO = GSON_INSTANCE.fromJson(winnerResponse, CandidateDTO.class);
                                StringBuilder sb = new StringBuilder();
                                Alert winnerAlert = new Alert(Alert.AlertType.INFORMATION);
                                winnerAlert.setTitle( "Message from Allie " + userName.getValue() +": finish contest message");
                                sb.append("The Winner is: " + candidatesDTO.getHowFind() + "\n");
                                sb.append("The string found in the following code: \n" + candidatesDTO.getCode());
                                winnerAlert.setHeaderText("Message");
                                winnerAlert.setContentText(sb.toString());
                                winnerAlert.showAndWait();
                                cleanContestData();
                                signoutFromUBoat();

                            });
                        }
                    }
                }
        );
    }

    private void cleanContestData() {
        isSigned.set(false);
        stringToHackLabel.setVisible(false);
        candidatesController.terminateCandidateRefresher();
        agentAndDMProgressController.terminateAgentAndDMProgressRefresher();
        candidatesController.clearCandidateTable();
        timerContestStatusRefresher.cancel();
        errorMessageProperty.set("Select contest");
        contestsDataTableViewController.removeChosenContestDTO();
        chosenContestController.removeContest();
        participantTeamsController.terminateTeamDetailRefresher();
        agentAndDMProgressController.clearProgress();
        isLogoutTimer.cancel();

    }



    public void startServerUpdateTimer() {
        contestsDataTableViewController.startListRefresher();
        teamAgentsTableViewController
                .startSignedAgentsRefresher((errorMessage)->errorMessageProperty.set(errorMessage),
                        shouldUpdate,agentAmount);

    }


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
                                errorMessageProperty.set("Something went wrong: wala" + responseBody)
                        );
                    } else {
                        Platform.runLater(() -> {
                            errorMessageProperty.set("Set Contest successfully ");
                            chosenContestController.setChosenContests(chosenContest);
                            contestsDataTableViewController.setChosenContestDTO(chosenContest);
                            participantTeamsController.startListRefresher();
                            agentAndDMProgressController.startProgressRefresher();
                            startLogoutRefresher();
                            isSigned.set(true);

                        });
                    }
                }
            });
        }

    private void startLogoutRefresher() {
        isUBoatLogoutRefresher = new IsUBoatLogoutRefresher(this::uBoatLogoutAction,this::alertShowException);
        isLogoutTimer = new Timer();
        isLogoutTimer.schedule(isUBoatLogoutRefresher,REFRESH_RATE,REFRESH_RATE);
    }
    public void uBoatLogoutAction(boolean isLogout){
       if(isLogout){
           Platform.runLater(()->{ ContestInformationDTO contestInformationDTO = chosenContestController.getChosenContest();
               Alert logoutMessage = new Alert(Alert.AlertType.INFORMATION);
               logoutMessage.setTitle( "Allie " + userName.getValue() +" message");
               logoutMessage.setHeaderText("Message");
               logoutMessage.setContentText("UBoat player:"+ contestInformationDTO.getUBoatName()+ " have logged out");
               logoutMessage.showAndWait();
               cleanContestData();


               String finalUrl = HttpUrl
                       .parse(CommonConstants.LOGOUT_REQUEST)
                       .newBuilder()
                       .addQueryParameter(CommonConstants.LOGOUT_ACTION_TYPE, "deleteAllieFromUBoat")
                       .build()
                       .toString();
               Request request = new Request.Builder()
                       .url(finalUrl)
                       .build();


               HttpClientUtil.runAsync(request, new Callback() {

                   @Override
                   public void onFailure(@NotNull Call call, @NotNull IOException e) {
                       alertShowException(new RuntimeException
                               ("faild call :Something went wrong:"  + e.getMessage()));

                   }

                   @Override
                   public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                       String responseString = response.body().string();
                       if (response.code() != 200) {
                           alertShowException(new RuntimeException(responseString));
                       }
                       else{
                       }


                   }
               });
           });}



    }

    @FXML
    private void updateMissionSizeAction(ActionEvent event){
        String finalUrl = HttpUrl
                .parse(AlliesConstants.UPDATE_MISSION_SIZE)
                .newBuilder()
                .addQueryParameter("missionSize", String.valueOf(missionSize))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    contestStatusAndErrorsProperty.set("faild call :Something went wrong: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            contestStatusAndErrorsProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        contestStatusAndErrorsProperty.set("Mission size updated successfully");
                    });
                }
            }
        });
    }
}

