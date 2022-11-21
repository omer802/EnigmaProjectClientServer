package client.javafx.Candidate;

import DTOS.agentInformationDTO.CandidateDTO;
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

import static client.constants.ConstantsUBoat.REFRESH_RATE;

public class candidateController implements Initializable {

    @FXML
    private TableView<CandidateDTO> table;

    @FXML
    private TableColumn<CandidateDTO, String> candidateString;

    @FXML
    private TableColumn<CandidateDTO, String> teamName;

    @FXML
    private TableColumn<CandidateDTO, String> code;
    private SimpleIntegerProperty candidatesAmount = new SimpleIntegerProperty();
    private SimpleBooleanProperty inActiveContest = new SimpleBooleanProperty();
    CandidateRefresher candidateRefresher;
    private Consumer<Exception> httpRequestLoggerConsumer;
    private boolean isInRefresh = false;
    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        candidateString.setCellValueFactory(new PropertyValueFactory<CandidateDTO, String>("candidateString"));
        teamName.setCellValueFactory(new PropertyValueFactory<CandidateDTO, String>("howFind"));
        code.setCellValueFactory(new PropertyValueFactory<CandidateDTO, String>("code"));
    }
    public void setErrorHandlerMainController(Consumer<Exception> httpRequestLoggerConsumer, SimpleBooleanProperty inActiveContest) {
        this.inActiveContest.bind(inActiveContest);
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;

    }

    public void updateCandidates(List<CandidateDTO> candidates){
        if(candidates != null) {
            Platform.runLater(() -> {
                ObservableList<CandidateDTO> items = table.getItems();
                if (!candidates.equals(items)) {
                    items.clear();
                    items.addAll(candidates);
                    candidatesAmount.set(candidates.size());
                }
            });
        }
    }
    public void startCandidateRefresher() {
        if (!isInRefresh) {
            candidateRefresher = new CandidateRefresher(this.inActiveContest, httpRequestLoggerConsumer, this::updateCandidates);
            timer = new Timer();
            timer.schedule(candidateRefresher, REFRESH_RATE, REFRESH_RATE);
            isInRefresh = true;

        }
    }
    public void terminateCandidateRefresher(){
        isInRefresh = false;
        timer.cancel();
    }

    public void cleanCandidateTable() {
        ObservableList<CandidateDTO> items = table.getItems();
        items.clear();
    }
}

