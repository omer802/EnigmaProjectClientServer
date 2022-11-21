package client.javafx.contestPage.contestDataSmall;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.function.Consumer;

public class contestDataSmallController {
    @FXML
    private TableView<ContestInformationDTO> table;

    @FXML
    private TableColumn<ContestInformationDTO, String> battlefield;

    @FXML
    private TableColumn<ContestInformationDTO, String> UBoatName;

    @FXML
    private TableColumn<ContestInformationDTO, String> status;

    @FXML
    private TableColumn<ContestInformationDTO, String> level;

    @FXML
    private TableColumn<ContestInformationDTO, Integer> requiredAllies;

    @FXML
    private TableColumn<ContestInformationDTO, Integer> signedAllies;
    private ContestInformationDTO contestInformationDTOList;

    @FXML
    public void initialize() {
        battlefield.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("battlefieldName"));
        UBoatName.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("UBoatName"));
        level.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("level"));
        status.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("status"));
        requiredAllies.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, Integer>("requiredAllies"));
        signedAllies.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, Integer>("signedAllies"));

    }

    public void setChosenContests(ContestInformationDTO contestInformationDTOList) {
        ObservableList<ContestInformationDTO> items = table.getItems();
        items.clear();
        items.add(contestInformationDTOList);
        this.contestInformationDTOList = contestInformationDTOList;
    }
    public ContestInformationDTO getChosenContest(){
        return contestInformationDTOList;
    }

    public void removeContest() {
        ObservableList<ContestInformationDTO> items = table.getItems();
        items.clear();
        this.contestInformationDTOList = null;

    }
}

