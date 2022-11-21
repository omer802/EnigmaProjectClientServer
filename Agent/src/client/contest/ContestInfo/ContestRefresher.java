package client.contest.ContestInfo;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import contants.AgentConstants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;

public class ContestRefresher extends TimerTask {
    private BooleanProperty shouldUpdate;
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<ContestInformationDTO> contestInformationConsumer;

    public ContestRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<ContestInformationDTO> contestInformationConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.contestInformationConsumer = contestInformationConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(AgentConstants.GET_CHOSEN_CONTEST)
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
                String jsonChosenContest = response.body().string();
                if (response.code() != 200) {
                    contestInformationConsumer.accept(null);
                } else {
                    Platform.runLater(() -> {
                        httpRequestLoggerConsumer.accept("");
                        ContestInformationDTO chosenContest = GSON_INSTANCE.fromJson(jsonChosenContest, ContestInformationDTO.class);
                        contestInformationConsumer.accept(chosenContest);

                    });
                }
            }
        });

    }
}

