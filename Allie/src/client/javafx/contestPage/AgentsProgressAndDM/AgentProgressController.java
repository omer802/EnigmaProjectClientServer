package client.javafx.contestPage.AgentsProgressAndDM;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AgentProgressController {

    @FXML
    private TableView<AgentProgressController> table;

    @FXML
    private TableColumn<AgentProgressController, String> agentName;

    @FXML
    private TableColumn<AgentProgressController, Long> AcceptedMissions;

    @FXML
    private TableColumn<AgentProgressController, Integer> WaitingMissions;

    @FXML
    private TableColumn<AgentProgressController, Integer> candidatesAmount;

    @FXML
    public void initialize() {
        agentName.setCellValueFactory(new PropertyValueFactory<AgentProgressController, String>("teamName"));
        AcceptedMissions.setCellValueFactory(new PropertyValueFactory<AgentProgressController, Long>("agentAmount"));
        WaitingMissions.setCellValueFactory(new PropertyValueFactory<AgentProgressController, Integer>("missionSize"));
        candidatesAmount.setCellValueFactory(new PropertyValueFactory<AgentProgressController, Integer>("missionSize"));
    }

    private void updateSignedAlliesList(List<AgentProgressController> AgentsProgressListDTO) {
        Platform.runLater(() -> {
            ObservableList<AgentProgressController> items = table.getItems();
            if (!AgentsProgressListDTO.equals(items)) {
                items.clear();
                items.addAll(AgentsProgressListDTO);
            }
        });
    }
}

