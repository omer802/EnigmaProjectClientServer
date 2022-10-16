package client.javafx.allies;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import client.javafx.contestsData.contestsDataController;
import client.javafx.teamsAgentsData.AgentDetail;
import client.javafx.teamsAgentsData.teamsAgentsDataController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static util.CommonConstants.MAX_AMOUNT_ERROR_TO_SHOW;

public class alliesController {

    @FXML
    private Label userGreetingLabel;

    @FXML
    private TableView<AgentDetail> teamAgentsTableView;
    @FXML
    private teamsAgentsDataController teamAgentsTableViewController;

    @FXML
    private TableView<ContestInformationDTO> contestsDataTableView;
    @FXML
    private contestsDataController contestsDataTableViewController;
    private SimpleStringProperty userName;
    private Stage primaryStage;

    @FXML
    private Button Submit;
    @FXML
    public void initialize() {
        userName = new SimpleStringProperty();
        contestsDataTableViewController.setErrorHandlerMainController(this::alertShowException);
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", userName));
        contestsDataTableViewController.setMainController(this);
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public void alertShowException(Exception e){
        List<Exception> exceptionList = new ArrayList<>();
        exceptionList.add(e);
        showListOfExceptions(exceptionList);
    }

    private void showListOfExceptions(List<Exception> exceptionList) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        String errorMessage = "";
        int amountOfMessageShowed = 0;
        for (Exception message : exceptionList) {
            errorMessage = errorMessage + message.getMessage() + "\n";
            amountOfMessageShowed++;
            if (amountOfMessageShowed > MAX_AMOUNT_ERROR_TO_SHOW)
                break;
        }
        alert.setContentText(errorMessage + "\n");
        alert.setTitle("Error!");
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

    public void startServerUpdateTimer() {
        contestsDataTableViewController.startListRefresher();
    }

    @FXML
    void setChosenContest(ActionEvent event) {
        ContestInformationDTO chosenContest = contestsDataTableViewController.getChosenContest();
        System.out.println(chosenContest.getBattlefieldName());
    }

}

