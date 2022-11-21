package client.agentMainPage;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.enigmaComponentContainers.ConfigurationForAgentBruteForceDTO;
import client.contest.ContestInfo.UpdateAgentProgressRefresher;
import client.contest.ContestInfo.contestDataController;
import DTOS.agentInformationDTO.CandidateDTO;
import client.agentMainPage.candidate.CandidateController;
import client.contest.ContestStatusRefresher;
import client.contest.PropertiesToUpdate;
import contants.AgentConstants;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import miniEngine.AgentDecipherManager;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import registerManagers.clients.Allie;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;

import static util.CommonConstants.GSON_INSTANCE;
import static util.CommonConstants.REFRESH_RATE;

public class AgentMainPageController {

    private Timer timer;

    public enum AgentStatus {
        WAITING_FOR_CONTEST, IN_CONTEST, WAITING_CONTEST_TO_END, FINISHED
    };
    private AgentStatus agentStatus;
    @FXML
    private Label userGreetingLabel;
    private SimpleStringProperty userGreetingString;
    SimpleStringProperty userName;


    @FXML
    private Label allieNameLabel;
    private SimpleStringProperty allieName;

    @FXML
    private Label missionFromServerAmountLabel;
    @FXML
    private Label completedMissionAmountLabel;

    @FXML
    private Label candidateAmountLabel;

    @FXML
    private Label amountOfMissionsInQueueLabel;
    @FXML
    private Label agentStatusLabel;
    private SimpleStringProperty agentStatusProperty;
    private Stage agentStage;
    private SimpleBooleanProperty isWaiting;

    private ContestInformationDTO contestInformationDTO;
    @FXML
    private TableView<ContestInformationDTO> contestInfo;
    @FXML
    private contestDataController contestInfoController;
    @FXML
    private Label errorMessageLabel;
    private SimpleStringProperty errorMessage;
    private boolean decipherUp;


    @FXML
    private TableView<CandidateDTO> candidates;
    @FXML
    private CandidateController candidatesController;

    private int missionAmount;
    private int threadAmount;
    private AgentDecipherManager decipherManager;
    private AgentInfoDTO agentInfoDTO;
    private ContestStatusRefresher contestStatusRefresher;
    private PropertiesToUpdate propertiesToUpdateInBruteForce;
    private UpdateAgentProgressRefresher updateAgentProgressRefresher;
    private Timer timerAgentUpdate;
    private Allie.AllieStatus prevAllieStatus;
    private SimpleBooleanProperty isActiveContest;
    private Timer stillWaitingTimer;
    private StillWaitingStatusRefresher stillWaitingStatusRefresher;

    public AgentMainPageController() {
        this.userGreetingString = new SimpleStringProperty();
        this.allieName = new SimpleStringProperty();
        this.agentStatusProperty = new SimpleStringProperty("");
        this.propertiesToUpdateInBruteForce = new PropertiesToUpdate();
        this.isWaiting = new SimpleBooleanProperty();
        this.userName = new SimpleStringProperty();
        this.errorMessage = new SimpleStringProperty();
        this.agentStatus = AgentStatus.WAITING_FOR_CONTEST;
        this.prevAllieStatus = Allie.AllieStatus.IDLE;
        this.isActiveContest = new SimpleBooleanProperty(false);
        this.decipherUp = false;
    }
    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userName));
        allieNameLabel.textProperty().bind(Bindings.concat("Signed to ", allieName));
        agentStatusLabel.textProperty().bind(Bindings.format("%s",agentStatusProperty));
        propertiesToUpdateInBruteForce.bindUIComponent(amountOfMissionsInQueueLabel,missionFromServerAmountLabel,completedMissionAmountLabel,candidateAmountLabel,userName);
        errorMessageLabel.textProperty().bind(errorMessage);
        candidatesController.setAgentName(userName);
        candidatesController.setErrorLabel(errorMessage);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.agentStage = primaryStage;
    }

    public void setAgentInfo(AgentInfoDTO agentInfoDTO) {
        this.userName.set(agentInfoDTO.getUserName());
        this.allieName.set(agentInfoDTO.getAllieName());
        this.threadAmount = agentInfoDTO.getThreadAmount();
        this.missionAmount = agentInfoDTO.getMissionAmount();
        this.agentInfoDTO = agentInfoDTO;
    }

    public void updateAgentStatus(boolean needToWait) {
        agentStatus = needToWait ? AgentStatus.WAITING_CONTEST_TO_END : AgentStatus.WAITING_FOR_CONTEST;
        this.isWaiting.set(needToWait);
        if (isWaiting.getValue()) {
            agentStatusProperty.set("Waiting for the contest to end");
            stillWaitingStatusRefresher = new StillWaitingStatusRefresher((error) -> errorMessage.set(error), isWaiting, this::startAgentProcessByRefresher);
            stillWaitingTimer = new Timer();
            stillWaitingTimer.schedule(stillWaitingStatusRefresher, REFRESH_RATE, REFRESH_RATE);
        } else {
            startAgentProcess();
        }

    }
    public void startAgentProcessByRefresher(boolean startProcess) {
        if (startProcess) {
            stillWaitingTimer.cancel();
            agentStatus = AgentStatus.WAITING_FOR_CONTEST;
            isWaiting.unbind();
            startAgentProcess();

        }
    }

    private void startAgentProcess() {
        if (!agentStatus.equals(AgentStatus.WAITING_CONTEST_TO_END)) {
            Platform.runLater(()->{ agentStatusProperty.set("Ready for contest");
                startContestStatusRefresher(agentStatus);
                contestInfoController.fetchContestFromServerRefresher((errorMessage) ->
                        Platform.runLater(() ->
                                this.errorMessage.set(errorMessage)));});

        }

    }

    private boolean isAgentFinishWaiting() throws IOException {
        String finalUrl = HttpUrl
                .parse(AgentConstants.CURRENT_CONTEST_STATUS)
                .newBuilder()
                .build()
                .toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Response response = HttpClientUtil.runSync(request);
        String responseBody = response.body().string();
        if (response.code() == 200) {
            responseBody = responseBody.trim();
            errorMessage.set("Finish waiting");
            return responseBody.equals("true");
        }
        else {
            errorMessage.set("Something went wrong while check if contest is finished");
            return false;
        }
    }

    public void startContestStatusRefresher(AgentStatus agentStatus){

        contestStatusRefresher = new ContestStatusRefresher(agentStatus,(responseBody) ->
                Platform.runLater(() ->
                        errorMessage.set(responseBody)),this::startContestIfThereIsActiveContest);
        timer = new Timer();
        timer.schedule(contestStatusRefresher,REFRESH_RATE,REFRESH_RATE);
    }
    public void startContestIfThereIsActiveContest(Allie.AllieStatus contestStatus){
        switch (contestStatus){
            case IDLE:
                restAgentProgress();
                isActiveContest.set(false);
                if(prevAllieStatus.equals(Allie.AllieStatus.FINISHED_CONTEST)){
                    //cleanshowingdata
                    decipherManager.shutDownBruteForce();
                    this.prevAllieStatus = Allie.AllieStatus.IDLE;
                }
                break;
            case IN_CONTEST:
                if(prevAllieStatus.equals(Allie.AllieStatus.IDLE)) {
                    isActiveContest.set(true);
                    fetchMachineAndDictionaryFromAllieAndStartPullingMissions();
                    startAgentProgressUpdates();
                    this.prevAllieStatus = Allie.AllieStatus.IN_CONTEST;
                }
                break;
            case FINISHED_CONTEST:
                if(prevAllieStatus.equals(Allie.AllieStatus.IN_CONTEST)) {
                    isActiveContest.set(false);
                    prevAllieStatus = Allie.AllieStatus.FINISHED_CONTEST;
                    if (decipherUp) {
                        decipherUp = false;
                    }
                }


        }

    }

    private void restAgentProgress() {
        candidatesController.restCandidates();

    }

    public void startAgentProgressUpdates(){
        updateAgentProgressRefresher = new UpdateAgentProgressRefresher(agentStatus,(responseBody) ->
                Platform.runLater(() ->
                        errorMessage.set(responseBody)),propertiesToUpdateInBruteForce);
        timerAgentUpdate = new Timer();
        timerAgentUpdate.schedule(updateAgentProgressRefresher,REFRESH_RATE,REFRESH_RATE);

    }

    private void fetchMachineAndDictionaryFromAllieAndStartPullingMissions()  {
        String finalUrl = HttpUrl
                .parse(AgentConstants.GET_ENIGMA_AND_DICTIONARY_FROM_ALLIE)
                .newBuilder()
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    errorMessage.set("faild call :Something went wrong:" +e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonConfigurationForAgentBruteForceDTO = response.body().string();
                if (response.code() != 200) {


                    Platform.runLater(() ->errorMessage.set("Something went wrong: "+ jsonConfigurationForAgentBruteForceDTO));
                } else {
                    Platform.runLater(() -> {
                        errorMessage.set("");
                        ConfigurationForAgentBruteForceDTO configurationForAgentBruteForce = GSON_INSTANCE.fromJson(jsonConfigurationForAgentBruteForceDTO, ConfigurationForAgentBruteForceDTO.class);
                        decipherManager = new AgentDecipherManager(configurationForAgentBruteForce.getMachine(),
                                configurationForAgentBruteForce.getDictionary(),
                                configurationForAgentBruteForce.getMissionSize(), agentInfoDTO, missionAmount,
                                (responseBody) ->
                                        Platform.runLater(() ->
                                                errorMessage.set(responseBody)),configurationForAgentBruteForce.getAlphabet(),candidatesController,propertiesToUpdateInBruteForce,isActiveContest);
                        new Thread(decipherManager, "Decipher manager thread").start();
                        decipherUp = true;
                    });
                }
            }
        });

    }
}

