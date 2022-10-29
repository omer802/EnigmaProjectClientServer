package client.javafx.contestPage.candidate;

import DTOS.agentInformationDTO.CandidateDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

import static util.CommonConstants.REFRESH_RATE;

public class CandidateController {

    @FXML
    private TableView<CandidateDTO> table;

    @FXML
    private TableColumn<CandidateDTO, String> candidateString;

    @FXML
    private TableColumn<CandidateDTO, String> howFound;
    CandidateRefresher candidateRefresher;
    private Consumer<Exception> httpRequestLoggerConsumer;
    private boolean isInRefresh = false;
    private Timer timer;
    @FXML
    private TableColumn<CandidateDTO, String> code;
    private SimpleIntegerProperty candidatesAmount = new SimpleIntegerProperty();
    private SimpleBooleanProperty inActiveContest = new SimpleBooleanProperty();

    public CandidateController() {
    }

    @FXML
    public void initialize() {
        candidateString.setCellValueFactory(new PropertyValueFactory<CandidateDTO, String>("candidateString"));
        howFound.setCellValueFactory(new PropertyValueFactory<CandidateDTO, String>("howFind"));
        code.setCellValueFactory(new PropertyValueFactory<CandidateDTO, String>("code"));
    }

    public void setActiveContestAndAgentsAmountProperty(SimpleBooleanProperty isActiveContest) {
        inActiveContest.bind(isActiveContest);
    }

    public void startCandidateRefresher() {
        if (!isInRefresh) {
            candidateRefresher = new CandidateRefresher(inActiveContest, httpRequestLoggerConsumer, this::addAllCandidates);
            timer = new Timer();
            timer.schedule(candidateRefresher, REFRESH_RATE, REFRESH_RATE);
            isInRefresh = true;
        }
    }

    public void setErrorHandlerMainController(Consumer<Exception> alertShowException) {
        httpRequestLoggerConsumer = alertShowException;
    }
    public void addAllCandidates(List<CandidateDTO> candidates) {
        if (candidates != null) {
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

    public void terminateCandidateRefresher() {
        timer.cancel();
        isInRefresh = false;
    }

    public void clearCandidateTable() {
        ObservableList<CandidateDTO> items = table.getItems();
        items.clear();
    }
}
