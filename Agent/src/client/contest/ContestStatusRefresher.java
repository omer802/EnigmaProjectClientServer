package client.contest;

import client.agentMainPage.AgentMainPageController;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import registerManagers.clients.Allie;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ContestStatusRefresher extends TimerTask {
    private final AgentMainPageController.AgentStatus shouldRefreshContestStatus;
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<Allie.AllieStatus> contestInformationConsumer;

    public ContestStatusRefresher(AgentMainPageController.AgentStatus agentStatus, Consumer<String> httpRequestLoggerConsumer, Consumer<Allie.AllieStatus> contestStatusConsumer) {
        this.shouldRefreshContestStatus = agentStatus;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.contestInformationConsumer = contestStatusConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(CommonConstants.GET_CONTEST_STATUS)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    httpRequestLoggerConsumer.accept("faild call :Something went wrong:" + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String AllieStatus = response.body().string();
                if (response.code() != 200) {
                    httpRequestLoggerConsumer.accept("Something went wrong: " + AllieStatus);
                } else {
                    Platform.runLater(() -> {
                                httpRequestLoggerConsumer.accept("");

                                Allie.AllieStatus allieStatus = Allie.AllieStatus.valueOf(AllieStatus);
                                contestInformationConsumer.accept(allieStatus);
                            }
                    );

                }
            }

        });
    }
}

