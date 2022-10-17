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
    private TableView<AlliesTeamDetailDTO> table;

    @FXML
    private TableColumn<AlliesTeamDetailDTO, String> teamName;

    @FXML
    private TableColumn<AlliesTeamDetailDTO, Integer> agentAmount;

    @FXML
    private TableColumn<AlliesTeamDetailDTO, Double> missionSize;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamName.setCellValueFactory(new PropertyValueFactory<AlliesTeamDetailDTO,String>("teamName"));
        agentAmount.setCellValueFactory(new PropertyValueFactory<AlliesTeamDetailDTO, Integer>("agentAmount"));
        missionSize.setCellValueFactory(new PropertyValueFactory<AlliesTeamDetailDTO, Double>("missionSize"));
    }
    public void addTeamToTable(AlliesTeamDetailDTO teamDetail){
        table.getItems().add(teamDetail);
    }
}
