package client.javafx.contestPage.AgentsProgressAndDM;

import DTOS.AllieInformationDTO.AgentAndDMProgressDTO;
import DTOS.AllieInformationDTO.DMProgressDTO;
import DTOS.agentInformationDTO.AgentProgressDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

import static util.CommonConstants.REFRESH_RATE;

public class AgentProgressController {

    @FXML
    private TableView<AgentProgressDTO> table;

    @FXML
    private TableColumn<AgentProgressDTO, String> agentName;

    @FXML
    private TableColumn<AgentProgressDTO, Long> AcceptedMissions;

    @FXML
    private TableColumn<AgentProgressDTO, Integer> WaitingMissions;

    @FXML
    private TableColumn<AgentProgressDTO, Integer> candidatesAmount;
    @FXML
    private Label totalMissionAmountLabel;
    private SimpleLongProperty totalMissionAmountProperty = new SimpleLongProperty(0);
    @FXML
    private Label totalGeneratedMissionsLabel;
    private SimpleLongProperty totalGeneratedMissionsProperty = new SimpleLongProperty(0);
    @FXML
    private Label totalFinishMissionsLabel;
    private SimpleLongProperty totalFinishMissionsProperty = new SimpleLongProperty(0);

    private SimpleBooleanProperty isActiveContest = new SimpleBooleanProperty();
    private SimpleStringProperty stringToHackProperty = new SimpleStringProperty("");
    private agentAndDmProgressRefresher agentAndDmProgressRefresher;
    private Consumer<Exception> httpRequestLoggerConsumer;
    private Timer timer;
    private boolean firstTimeFetchStringToHack;

    @FXML
    public void initialize() {
        totalMissionAmountLabel.textProperty().bind(Bindings.concat("Total Mission Amount: ", totalMissionAmountProperty));
        totalGeneratedMissionsLabel.textProperty().bind(Bindings.concat("Total generated missions Amount: ", totalGeneratedMissionsProperty));
        totalFinishMissionsLabel.textProperty().bind(Bindings.concat("Total completed missions ", totalFinishMissionsProperty));
        agentName.setCellValueFactory(new PropertyValueFactory<AgentProgressDTO, String>("agentName"));
        AcceptedMissions.setCellValueFactory(new PropertyValueFactory<AgentProgressDTO, Long>("AcceptedMissions"));
        WaitingMissions.setCellValueFactory(new PropertyValueFactory<AgentProgressDTO, Integer>("WaitingMissions"));
        candidatesAmount.setCellValueFactory(new PropertyValueFactory<AgentProgressDTO, Integer>("CandidatesAmount"));
    }

/*    private void updateSignedAlliesList(List<AgentProgressController> AgentsProgressListDTO) {
        Platform.runLater(() -> {
            ObservableList<AgentProgressController> items = table.getItems();
            if (!AgentsProgressListDTO.equals(items)) {
                items.clear();
                items.addAll(AgentsProgressListDTO);
            }
        });
    }*/

    public void setStringToHackAndActiveContestProperty(SimpleBooleanProperty isActiveContest, SimpleStringProperty stringToHackProperty, Consumer<Exception> httpRequestLoggerConsumer) {
        this.isActiveContest.bind(isActiveContest);
        stringToHackProperty.bind(this.stringToHackProperty);
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
    }

    public void startProgressRefresher() {
        firstTimeFetchStringToHack = true;
        agentAndDmProgressRefresher = new agentAndDmProgressRefresher(isActiveContest,httpRequestLoggerConsumer,this::updateProgress);
        timer = new Timer();
        timer.schedule(agentAndDmProgressRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void updateProgress(AgentAndDMProgressDTO agentAndDMProgressDTO) {
        Platform.runLater(()->{
            if (agentAndDMProgressDTO != null) {
                if(firstTimeFetchStringToHack&&agentAndDMProgressDTO.getStringToHack()!=null) {
                    System.out.println(agentAndDMProgressDTO.getStringToHack());
                    stringToHackProperty.set(agentAndDMProgressDTO.getStringToHack());
                    firstTimeFetchStringToHack = false;
                }

            List<AgentProgressDTO> agentProgressDTOS = agentAndDMProgressDTO.getAgentProgressDTO();
            if (agentProgressDTOS != null) {
                ObservableList<AgentProgressDTO> items = table.getItems();
                if (!agentProgressDTOS.equals(items)) {
                    items.clear();
                    items.addAll(agentProgressDTOS);
                }
                DMProgressDTO dmProgressDTO = agentAndDMProgressDTO.getDmProgress();
                if(dmProgressDTO!=null){
                    totalMissionAmountProperty.set(dmProgressDTO.getTotalMissionsAmount());
                    totalGeneratedMissionsProperty.set(dmProgressDTO.getGeneratedMissions());
                    totalFinishMissionsProperty.set(dmProgressDTO.getFinishedMissionsByAgents());
                }
            }
        }});


    }
}


