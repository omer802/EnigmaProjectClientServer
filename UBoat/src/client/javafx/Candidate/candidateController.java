package client.javafx.Candidate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class candidateController implements Initializable {

    @FXML
    private TableView<Candidate> table;

    @FXML
    private TableColumn<Candidate, String> candidateString;

    @FXML
    private TableColumn<Candidate, String> teamName;

    @FXML
    private TableColumn<Candidate, String> code;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        candidateString.setCellValueFactory(new PropertyValueFactory<Candidate, String>("candidateString"));
        teamName.setCellValueFactory(new PropertyValueFactory<Candidate, String>("teamName"));
        code.setCellValueFactory(new PropertyValueFactory<Candidate, String>("code"));
    }
    public void addCandidate(Candidate candidate){
        table.getItems().add(candidate);
    }
}

