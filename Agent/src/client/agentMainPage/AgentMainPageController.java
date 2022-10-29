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
   // private SimpleLongProperty missionCounterFromServer;
    @FXML
    private Label completedMissionAmountLabel;
    //private SimpleLongProperty completedMissionAmount;

    @FXML
    private Label candidateAmountLabel;
   // private SimpleIntegerProperty candidateAmount;

    @FXML
    private Label amountOfMissionsInQueueLabel;
    //private SimpleIntegerProperty queueMissionAmount;
    @FXML
    private Label agentStatusLabel;
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
    public AgentMainPageController() {
        this.userGreetingString = new SimpleStringProperty();
        this.allieName = new SimpleStringProperty();
        //this.missionCounterFromServer = new SimpleLongProperty();
        //this.completedMissionAmount = new SimpleLongProperty();
        //this.candidateAmount = new SimpleIntegerProperty();
        //this.queueMissionAmount = new SimpleIntegerProperty();
        this.propertiesToUpdateInBruteForce = new PropertiesToUpdate();
        this.isWaiting = new SimpleBooleanProperty();
        this.userName = new SimpleStringProperty();
        this.errorMessage = new SimpleStringProperty();
        this.agentStatus = AgentStatus.WAITING_FOR_CONTEST;
        this.prevAllieStatus = Allie.AllieStatus.IDLE;
    }

    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userName));
        allieNameLabel.textProperty().bind(Bindings.concat("Signed to ", allieName));
        propertiesToUpdateInBruteForce.bindUIComponent(amountOfMissionsInQueueLabel,missionFromServerAmountLabel,completedMissionAmountLabel,candidateAmountLabel,userName);
       //amountOfMissionsInQueueLabel.textProperty().bind(Bindings.format("%,d", queueMissionAmount));
       //missionFromServerAmountLabel.textProperty().bind(Bindings.format("%,d", missionCounterFromServer));
       //completedMissionAmountLabel.textProperty().bind(Bindings.format("%,d", completedMissionAmount));
       //candidateAmountLabel.textProperty().bind(Bindings.format("%,d", candidateAmount));
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
        if (!agentStatus.equals(AgentStatus.WAITING_CONTEST_TO_END)) {
            startContestStatusRefresher(agentStatus);
            contestInfoController.fetchContestFromServerRefresher((errorMessage) ->
                  Platform.runLater(() ->
                           this.errorMessage.set(errorMessage)));

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
                if(prevAllieStatus.equals(Allie.AllieStatus.FINISHED_CONTEST)){
                    //cleanshowingdata
                    this.prevAllieStatus = Allie.AllieStatus.IDLE;
                }
                break;
            case IN_CONTEST:
                fetchMachineAndDictionaryFromAllieAndStartPullingMissions();
                startAgentProgressUpdates();
                this.prevAllieStatus = Allie.AllieStatus.IN_CONTEST;
                break;
            case FINISHED_CONTEST:
                prevAllieStatus = Allie.AllieStatus.FINISHED_CONTEST;
                decipherManager.shutDownBruteForce();
                //show alert


        }

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
                    System.out.println("badddddddddddddd");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonConfigurationForAgentBruteForceDTO = response.body().string();
                if (response.code() != 200) {
                    System.out.println(response.code());
                    System.out.println("badddddddddddddd wahhtaattttt");

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
                                                errorMessage.set(responseBody)),configurationForAgentBruteForce.getAlphabet(),candidatesController,propertiesToUpdateInBruteForce);
                        new Thread(decipherManager, "Decipher manager thread").start();
                    });
                }
            }
        });

    }
}

