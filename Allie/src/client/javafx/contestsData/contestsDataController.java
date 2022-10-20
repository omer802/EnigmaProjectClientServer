package client.javafx.contestsData;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import client.javafx.allies.alliesController;
import client.javafx.contestPage.contestDataSmall.contestDataSmallController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.constants.AlliesConstants.REFRESH_RATE;

public class contestsDataController implements Closeable {

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



    private contestDataSmallController contestDataSmallController;
    private Timer timer;
    private TimerTask contestRefresher;
    private BooleanProperty autoUpdate;
    private IntegerProperty amountOfContests;
    private alliesController mainController;

    public ContestInformationDTO getChosenContestDTO() {
        return chosenContestDTO;
    }

    public void setChosenContestDTO(ContestInformationDTO chosenContestDTO) {
        this.chosenContestDTO = chosenContestDTO;
    }

    private ContestInformationDTO chosenContestDTO = null;

    private Consumer<Exception> httpRequestLoggerConsumer;

    public contestsDataController() {
        this.autoUpdate = new SimpleBooleanProperty(true);
        this.amountOfContests = new SimpleIntegerProperty();
    }
    public void setErrorHandlerMainController(Consumer<Exception> httpRequestLoggerConsumer){
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
    }
    public void setChosenContestController(client.javafx.contestPage.contestDataSmall.contestDataSmallController contestDataSmallController) {
        this.contestDataSmallController = contestDataSmallController;
    }



    @FXML
    public void initialize() {
        battlefield.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO,String>("battlefieldName"));
        UBoatName.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("UBoatName"));
        level.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("level"));
        status.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, String>("status"));
        requiredAllies.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, Integer>("requiredAllies"));
        signedAllies.setCellValueFactory(new PropertyValueFactory<ContestInformationDTO, Integer>("signedAllies"));

    }
    public void addContestInformation(ContestInformationDTO contestInformation){
        table.getItems().add(contestInformation);
    }
    public void startListRefresher() {
        contestRefresher = new ContestsRefresher(
                autoUpdate,
                httpRequestLoggerConsumer,
                this::updateContests);
        timer = new Timer();
        timer.schedule(contestRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateContests(List<ContestInformationDTO> contestInformationDTOList) {
        Platform.runLater(() -> {
            ObservableList<ContestInformationDTO> items = table.getItems();
            if (!contestInformationDTOList.equals(items)) {
                items.clear();
                items.addAll(contestInformationDTOList);
                ContestInformationDTO contestInformationDTO = null;
                if(chosenContestDTO != null){
                contestInformationDTO = contestInformationDTOList.
                        stream().filter(u -> u.getUBoatName().equals(chosenContestDTO.getUBoatName())).findFirst().get();
                }
                contestDataSmallController.setChosenContests(contestInformationDTO);
                amountOfContests.set(contestInformationDTOList.size());
            }
        });
    }
    @Override
    public void close() {
        table.getItems().clear();
        amountOfContests.set(0);
        if (contestRefresher != null && timer != null) {
            contestRefresher.cancel();
            timer.cancel();
        }
    }

    public void setMainController(alliesController mainController) {
        this.mainController = mainController;
    }
    public ContestInformationDTO getChosenContest(){
        return table.getSelectionModel().getSelectedItem();

    }
}
