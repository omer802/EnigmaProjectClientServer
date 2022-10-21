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

    // TODO: 10/14/2022 replace status with enum
    @FXML
    private TableColumn<ContestInformationDTO, String> status;

    // TODO: 10/14/2022 same as above
    @FXML
    private TableColumn<ContestInformationDTO, String> level;

    @FXML
    private TableColumn<ContestInformationDTO, Integer> requiredAllies;

    @FXML
    private TableColumn<ContestInformationDTO, Integer> signedAllies;

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
    }

    public void removeContest() {
        ObservableList<ContestInformationDTO> items = table.getItems();
        items.clear();
    }

    public void updateAmountOfSignedTeam(int newValue) {
      /*  System.out.println(newValue+1);
        if(table.getItems().get(0)!=null) {
            // table.getItems().get(0).setSignedAllies(newValue+1);
            ContestInformationDTO contestInfo = table.getItems().get(0);
            //table.getItems().removeAll();
            contestInfo.setSignedAllies(newValue+1);
            table.getItems().add(contestInfo);*/
    }
}

