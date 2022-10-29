package client.javafx.teamsAgentsData;

import DTOS.agentInformationDTO.AgentInfoDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.function.Consumer;

import static util.CommonConstants.REFRESH_RATE;

public class teamsAgentsDataController implements Initializable {

    @FXML
    private TableView<AgentInfoDTO> table;

    @FXML
    private TableColumn<AgentInfoDTO, String> agentName;

    @FXML
    private TableColumn<AgentInfoDTO, Integer> threadAmount;

    @FXML
    private TableColumn<AgentInfoDTO, Integer> missionAmount;
    private SimpleIntegerProperty agentAmount = new SimpleIntegerProperty(0);
    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentName.setCellValueFactory(new PropertyValueFactory<AgentInfoDTO,String>("userName"));
        threadAmount.setCellValueFactory(new PropertyValueFactory<AgentInfoDTO, Integer>("threadAmount"));
        missionAmount.setCellValueFactory(new PropertyValueFactory<AgentInfoDTO, Integer>("missionAmount"));
    }
    public void addAgent(AgentInfoDTO agentInfoDTO){
        table.getItems().add(agentInfoDTO);
    }
    public void startSignedAgentsRefresher(Consumer<String> httpRequestLoggerConsumer, SimpleBooleanProperty shouldUpdate, SimpleIntegerProperty agentAmount){
        agentAmount.bind(this.agentAmount);
        SignedAgentsRefresher signedAgentsRefresher = new SignedAgentsRefresher(shouldUpdate, httpRequestLoggerConsumer,this::updateAgents);
        timer = new Timer();
        timer.schedule(signedAgentsRefresher,REFRESH_RATE,REFRESH_RATE);

    }
    public void updateAgents(List<AgentInfoDTO> agentInfoDTOList){
        if(agentInfoDTOList!= null) {
        Platform.runLater(() -> {
            ObservableList<AgentInfoDTO> items = table.getItems();
            if (!agentInfoDTOList.equals(items)) {
                items.clear();
                items.addAll(agentInfoDTOList);
                agentAmount.set(agentInfoDTOList.size());
            }
        });
        }

    }

}
