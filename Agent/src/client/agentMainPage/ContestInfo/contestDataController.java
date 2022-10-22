package client.agentMainPage.ContestInfo;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import contants.AgentConstants;
import dictionary.Dictionary;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;
import static util.CommonConstants.REFRESH_RATE;

public class contestDataController {
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
    private ContestInformationDTO chosenContest;
    private SimpleBooleanProperty shouldUpdate = new SimpleBooleanProperty(true);
    private Timer timer;
    private ContestRefresher contestRefresher;

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
    public void startContestRefresher(){

    }

    public void fetchContestFromServer(Consumer<String> alertException){
        this.contestRefresher  = new ContestRefresher(shouldUpdate,alertException,this::setChosenContests);
        timer = new Timer();
        timer.schedule(contestRefresher, REFRESH_RATE,REFRESH_RATE);
        /*String finalUrl = HttpUrl
                .parse(AgentConstants.GET_CHOSEN_CONTEST)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    alertException.accept("faild call :Something went wrong:" +e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonChosenContest = response.body().string();
                if (response.code() != 200) {
                    alertException.accept("Something went wrong: "+jsonChosenContest);
                } else {
                    Platform.runLater(() -> {
                        alertException.accept("");
                        ContestInformationDTO chosenContest = GSON_INSTANCE.fromJson(jsonChosenContest, ContestInformationDTO.class);
                        setChosenContests(chosenContest);

                    });
                }
            }
        });
*/
    }
}
