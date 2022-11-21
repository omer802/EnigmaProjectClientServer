package client.javafx.contestPage.teamsDetails;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import client.javafx.contestPage.contestDataSmall.contestDataSmallController;
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
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.constants.AlliesConstants.REFRESH_RATE;

public class TeamsDetailsController {
    @FXML
    private TableView<AlliesDetailDTO> table;

    @FXML
    private TableColumn<AlliesDetailDTO, String> teamName;

    @FXML
    private TableColumn<AlliesDetailDTO, Integer> agentAmount;

    @FXML
    private TableColumn<AlliesDetailDTO, Long> missionSize;
    private Consumer<Exception> httpRequestLoggerConsumer;
    private Timer timer;
    private TimerTask participantTeams;
    private SimpleBooleanProperty autoUpdate;
    private contestDataSmallController chosenContestController;


    public int getParticipantTeamsInContest() {
        return participantTeamsInContest.get();
    }

    public SimpleIntegerProperty participantTeamsInContestProperty() {
        return participantTeamsInContest;
    }

    private SimpleIntegerProperty participantTeamsInContest;

    @FXML
    public void initialize() {
        teamName.setCellValueFactory(new PropertyValueFactory<AlliesDetailDTO, String>("teamName"));
        agentAmount.setCellValueFactory(new PropertyValueFactory<AlliesDetailDTO, Integer>("agentAmount"));
        missionSize.setCellValueFactory(new PropertyValueFactory<AlliesDetailDTO, Long>("missionSize"));
        autoUpdate = new SimpleBooleanProperty(true);
        participantTeamsInContest = new SimpleIntegerProperty(0);
    }
    public void startListRefresher() {
        participantTeams = new participantTeamsRefresher(
                autoUpdate,
                httpRequestLoggerConsumer,
                this::updateParticipantList);
        timer = new Timer();
        timer.schedule(participantTeams, REFRESH_RATE, REFRESH_RATE);
    }
    public void setErrorHandlerMainController(Consumer<Exception> httpRequestLoggerConsumer){
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
    }


    private void updateParticipantList(List<AlliesDetailDTO> contestInformationDTOList) {
        Platform.runLater(() -> {
            ObservableList<AlliesDetailDTO> items = table.getItems();
            if (!contestInformationDTOList.equals(items)) {
                items.clear();
                items.addAll(contestInformationDTOList);

            }
        });
    }


    public void setChosenContestController(contestDataSmallController chosenContestController) {
        this.chosenContestController = chosenContestController;
    }
    public void terminateTeamDetailRefresher(){
        timer.cancel();
    }


    /*public void setAlliesDetailDTOChosenContest(AlliesDetailDTO alliesDetailDTO){
        this.alliesDetailDTOChosenContest = alliesDetailDTO;
    }
    public void removeDetailDtoChosenContest(AlliesDetailDTO alliesDetailDTO){
        this.alliesDetailDTOChosenContest = null;
    }*/
}
