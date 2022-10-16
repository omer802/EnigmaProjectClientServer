package client.javafx.teamsAgentsData;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class teamsAgentsDataController implements Initializable {

    @FXML
    private TableView<AgentDetail> table;

    @FXML
    private TableColumn<AgentDetail, String> agentName;

    @FXML
    private TableColumn<AgentDetail, Integer> threadAmount;

    @FXML
    private TableColumn<AgentDetail, Double> missionSize;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        agentName.setCellValueFactory(new PropertyValueFactory<AgentDetail,String>("agentName"));
        threadAmount.setCellValueFactory(new PropertyValueFactory<AgentDetail, Integer>("threadAmount"));
        missionSize.setCellValueFactory(new PropertyValueFactory<AgentDetail, Double>("missionSize"));
    }
    public void addAgent(AgentDetail agentDetail){
        table.getItems().add(agentDetail);
    }
}
