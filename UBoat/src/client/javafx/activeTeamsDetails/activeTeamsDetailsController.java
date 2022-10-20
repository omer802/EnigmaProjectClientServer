package client.javafx.activeTeamsDetails;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.Closeable;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.constants.ConstantsUBoat.REFRESH_RATE;

public class activeTeamsDetailsController implements Initializable, Closeable {

    @FXML
    private TableView<AlliesDetailDTO> table;

    @FXML
    private TableColumn<AlliesDetailDTO, String> teamName;

    @FXML
    private TableColumn<AlliesDetailDTO, Integer> agentAmount;

    @FXML
    private TableColumn<AlliesDetailDTO, Long> missionSize;
    private Timer timer;
    private TimerTask signedAlliesRefresher;
    private Consumer<Exception> httpRequestLoggerConsumer;
    private SimpleBooleanProperty autoUpdate;
    public activeTeamsDetailsController(){
        this.autoUpdate = new SimpleBooleanProperty(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamName.setCellValueFactory(new PropertyValueFactory<AlliesDetailDTO,String>("teamName"));
        agentAmount.setCellValueFactory(new PropertyValueFactory<AlliesDetailDTO, Integer>("agentAmount"));
        missionSize.setCellValueFactory(new PropertyValueFactory<AlliesDetailDTO, Long>("missionSize"));
    }
    public void addTeamToTable(AlliesDetailDTO teamDetail){
        table.getItems().add(teamDetail);
    }

    public void startListRefresher() {
        signedAlliesRefresher = new SignedAlliesRefresher(
                autoUpdate,
                httpRequestLoggerConsumer,
                this::updateSignedAlliesList);
        timer = new Timer();
        timer.schedule(signedAlliesRefresher, REFRESH_RATE, REFRESH_RATE);
    }
    public void setErrorHandlerMainController(Consumer<Exception> httpRequestLoggerConsumer){
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
    }
    private void updateSignedAlliesList(List<AlliesDetailDTO> contestInformationDTOList) {
        Platform.runLater(() -> {
            ObservableList<AlliesDetailDTO> items = table.getItems();
            if (!contestInformationDTOList.equals(items)) {
                items.clear();
                items.addAll(contestInformationDTOList);
            }
        });
    }
    @Override
    public void close() {
        table.getItems().clear();
        if (signedAlliesRefresher != null && timer != null) {
            signedAlliesRefresher.cancel();
            timer.cancel();
        }
    }
}
