package client.javafx.activeTeamsDetails;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class activeTeamsDetailsController implements Initializable {

    @FXML
    private TableView<TeamDetail> table;

    @FXML
    private TableColumn<TeamDetail, String> teamName;

    @FXML
    private TableColumn<TeamDetail, Integer> agentAmount;

    @FXML
    private TableColumn<TeamDetail, Double> missionSize;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamName.setCellValueFactory(new PropertyValueFactory<TeamDetail,String>("teamName"));
        agentAmount.setCellValueFactory(new PropertyValueFactory<TeamDetail, Integer>("agentAmount"));
        missionSize.setCellValueFactory(new PropertyValueFactory<TeamDetail, Double>("missionSize"));
    }
    public void addTeamToTable(TeamDetail teamDetail){
        table.getItems().add(teamDetail);
    }
}
