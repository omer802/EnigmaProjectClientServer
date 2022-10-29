package client.contest.ContestInfo;

import DTOS.agentInformationDTO.AgentProgressDTO;
import client.agentMainPage.AgentMainPageController;
import client.contest.PropertiesToUpdate;
import contants.AgentConstants;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import registerManagers.clients.Allie;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;

public class UpdateAgentProgressRefresher extends TimerTask {
    private AgentMainPageController.AgentStatus shouldRefreshContestStatus;
    private Consumer<String> httpRequestLoggerConsumer;
    private Consumer<Boolean> contestInformationConsumer;
    private PropertiesToUpdate propertiesToUpdate;

    public UpdateAgentProgressRefresher(AgentMainPageController.AgentStatus agentStatus, Consumer<String> httpRequestLoggerConsumer, PropertiesToUpdate propertiesToUpdate) {
        this.shouldRefreshContestStatus = agentStatus;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.propertiesToUpdate = propertiesToUpdate;
    }

    @Override
    public void run() {
      //  if (shouldRefreshContestStatus.equals(AgentMainPageController.AgentStatus.WAITING_CONTEST_TO_END)
      //  ){//|| shouldRefreshContestStatus.equals(AgentMainPageController.AgentStatus.FINISHED)) {
      //      return;
      //  }

        AgentProgressDTO agentProgressDTO = propertiesToUpdate.createAgentProgressDTO();
        String agentProgressDTOString = GSON_INSTANCE.toJson(agentProgressDTO);
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("agentProgressDTO", agentProgressDTOString)
                        .build();
        Request request = new Request.Builder()
                .url(CommonConstants.UPDATE_AGENT_PROGRESS)
                .post(body)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    httpRequestLoggerConsumer.accept("failed call :Something went wrong:" + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String AllieStatus = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->  httpRequestLoggerConsumer.accept("Something went wrong: " + AllieStatus));
                } else {
                    Platform.runLater(() -> {
                 //    httpRequestLoggerConsumer.accept("");
                 //    if (shouldRefreshContestStatus.equals(AgentMainPageController.AgentStatus.WAITING_FOR_CONTEST)) {
                 //        Allie.AllieStatus allieStatus = Allie.AllieStatus.valueOf(AllieStatus);
                 //        switch (allieStatus) {
                 //            case IN_CONTEST:
                 //                contestInformationConsumer.accept(true);
                 //                shouldRefreshContestStatus = AgentMainPageController.AgentStatus.IN_CONTEST;
                 //                break;
                 //            case FINISHED_CONTEST:
                 //                shouldRefreshContestStatus = AgentMainPageController.AgentStatus.FINISHED;
                 //                break;

                 //           }
                //        }
                    });

                }
            }
        });

    }
}