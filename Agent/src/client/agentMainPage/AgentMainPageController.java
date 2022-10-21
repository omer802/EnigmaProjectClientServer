package client.agentMainPage;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.enigmaComponentContainers.ConfigurationForAgentBruteForceDTO;
import client.agentMainPage.ContestInfo.contestDataController;
import contants.AgentConstants;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import miniEngine.AgentDecipherManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;

import static util.CommonConstants.GSON_INSTANCE;

public class AgentMainPageController {

    @FXML
    private Label userGreetingLabel;
    private SimpleStringProperty userGreetingString;
    SimpleStringProperty userName;


    @FXML
    private Label allieNameLabel;
    private SimpleStringProperty allieName;

    @FXML
    private Label missionFromServerAmountLabel;
    private SimpleLongProperty missionCounterFromServer;
    @FXML
    private Label completedMissionAmountLabel;
    private SimpleLongProperty completedMissionAmount;

    @FXML
    private Label candidateAmountLabel;
    private SimpleIntegerProperty candidateAmount;

    @FXML
    private Label amountOfMissionsInQueueLabel;
    private SimpleIntegerProperty queueMissionAmount;
    @FXML
    private Label agentStatusLabel;
    private Stage agentStage;
    private SimpleBooleanProperty isActive;

    private ContestInformationDTO contestInformationDTO;
    @FXML
    private TableView<ContestInformationDTO> contestInfo;
    @FXML
    private contestDataController contestInfoController;
    @FXML
    private Label errorMessageLabel;
    private SimpleStringProperty errorMessage;

    private int missionAmount;
    private int threadAmount;
    private AgentDecipherManager decipherManager;
    private AgentInfoDTO agentInfoDTO;

    public AgentMainPageController() {
        this.userGreetingString = new SimpleStringProperty();
        this.allieName = new SimpleStringProperty();
        this.missionCounterFromServer = new SimpleLongProperty();
        this.completedMissionAmount = new SimpleLongProperty();
        this.candidateAmount = new SimpleIntegerProperty();
        this.queueMissionAmount = new SimpleIntegerProperty();
        this.isActive = new SimpleBooleanProperty();
        this.userName = new SimpleStringProperty();
        this.errorMessage = new SimpleStringProperty();
    }

    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userName));
        allieNameLabel.textProperty().bind(Bindings.concat("Signed to ", allieName));
        amountOfMissionsInQueueLabel.textProperty().bind(Bindings.format("%,d", queueMissionAmount));
        missionFromServerAmountLabel.textProperty().bind(Bindings.format("%,d", missionCounterFromServer));
        completedMissionAmountLabel.textProperty().bind(Bindings.format("%,d", completedMissionAmount));
        candidateAmountLabel.textProperty().bind(Bindings.format("%,d", candidateAmount));
        errorMessageLabel.textProperty().bind(errorMessage);
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

    public void updateAgentStatus(boolean isActive) {
        this.isActive.set(isActive);
        //if(isActive){
        contestInfoController.fetchContestFromServer((errorMessage) ->
                Platform.runLater(() ->
                        this.errorMessage.set(errorMessage)));
        fetchMachineAndDictionaryFromAllie();
        //}
    }

    private void fetchMachineAndDictionaryFromAllie() {
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
                String jsonMachineAndDictionary = response.body().string();
                if (response.code() != 200) {
                    errorMessage.set("Something went wrong: "+ jsonMachineAndDictionary);
                } else {
                    Platform.runLater(() -> {

                            ConfigurationForAgentBruteForceDTO ConfigurationForAgentBruteForce = GSON_INSTANCE.fromJson(jsonMachineAndDictionary, ConfigurationForAgentBruteForceDTO.class);
                        System.out.println(ConfigurationForAgentBruteForce.getMachine().getChosenRotors());
                        System.out.println(ConfigurationForAgentBruteForce.getDictionary().getExcludeChars());
                        decipherManager = new AgentDecipherManager(ConfigurationForAgentBruteForce.getMachine(),
                                ConfigurationForAgentBruteForce.getDictionary(),ConfigurationForAgentBruteForce.getMissionSize(),agentInfoDTO);
                    });
                }
            }
        });

    }
}
