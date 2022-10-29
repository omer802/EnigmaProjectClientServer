package client.javafx.contest;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import registerManagers.clients.UBoat;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ContestStatusRefresher extends TimerTask {

    private final Consumer<Exception> errorConsumer;
    private final Consumer<UBoat.GameStatus> gameStatusConsumer;
    private BooleanProperty isReadyOrInContest = new SimpleBooleanProperty();

    public ContestStatusRefresher(BooleanProperty isReady, Consumer<Exception> errorConsumer, Consumer<UBoat.GameStatus> gameStatusConsumer) {
        isReadyOrInContest.bind(isReady);
        this.errorConsumer = errorConsumer;
        this.gameStatusConsumer = gameStatusConsumer;
    }

    @Override
    public void run() {
        if (!isReadyOrInContest.getValue()) {
            //replace with cancel
            return;
        }

        HttpClientUtil.runAsync(CommonConstants.GET_CONTEST_STATUS, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                errorConsumer.accept(new RuntimeException("faild call :Something went wrong: " + e.getMessage()));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String statusResponse = response.body().string();
                System.out.println(statusResponse);
                if (!response.isSuccessful()) {
                    errorConsumer.accept(new RuntimeException("Something went wrong:" + statusResponse));
                } else {
                    Platform.runLater(() -> {
                        UBoat.GameStatus gameStatus = UBoat.GameStatus.valueOf(statusResponse);
                        gameStatusConsumer.accept(gameStatus);

                    });

                }
            }
        });

    }
}


