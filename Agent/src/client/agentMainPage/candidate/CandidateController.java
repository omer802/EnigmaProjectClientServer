package client.agentMainPage.candidate;

import DTOS.agentInformationDTO.CandidateDTO;
import contants.AgentConstants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static util.CommonConstants.GSON_INSTANCE;

public class CandidateController {

    @FXML
    private TableView<CandidateDTO> table;

    @FXML
    private TableColumn<CandidateDTO, String> candidateString;


    @FXML
    private TableColumn<CandidateDTO, String> code;
    private BlockingDeque<CandidateDTO> blockingQueueCandidates = new LinkedBlockingDeque<>();
    private SimpleStringProperty errorMessage;

    private SimpleStringProperty agentName = new SimpleStringProperty();

    @FXML
    public void initialize() {
        candidateString.setCellValueFactory(new PropertyValueFactory<CandidateDTO,String>("candidateString"));
        code.setCellValueFactory(new PropertyValueFactory<CandidateDTO,String>("code"));
    }
    public void updateCandidate(List<CandidateDTO> candidateList) {
        if (candidateList != null)
        {
            Platform.runLater(() -> {
                ObservableList<CandidateDTO> items = table.getItems();
                items.forEach(candidateDTO-> candidateDTO.setHowFind(getAgentName()));
                items.addAll(candidateList);
                sendCandidateToServer(candidateList);

            });

        }
    }
    public String getAgentName() {
        return agentName.getValue();
    }

    public void setAgentName(SimpleStringProperty agentName) {
        this.agentName.bind(agentName);
    }
    public void sendCandidateToServer(List<CandidateDTO> candidateList){
        candidateList.forEach(candidate->candidate.setHowFind(getAgentName()));
        String candidateListDTO = GSON_INSTANCE.toJson(candidateList);
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("candidateListDTO", candidateListDTO)
                        .build();
        Request finalUrl = new Request.Builder()
                .url(AgentConstants.UPDATE_CANDIDATE)
                .post(body)
                .build();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> errorMessage.set("Something went wrong: " + e.getMessage()));
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessage.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        // todo when sending candidate successed

                    });
                }
            }
        });
    }

    public void setErrorLabel(SimpleStringProperty errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void restCandidates() {
        ObservableList<CandidateDTO> items = table.getItems();
        items.clear();
    }
}
