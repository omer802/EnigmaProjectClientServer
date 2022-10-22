package client.agentLogin;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import contants.AgentConstants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;

public class AlliesRefresher extends TimerTask {
    private Consumer<List<AlliesDetailDTO>> alliesNameConsumer;
    private Consumer<String> exceptionConsumer;
    private BooleanProperty shouldUpdate;
    private boolean isServerUp;

    public AlliesRefresher(BooleanProperty shouldUpdate,
                           Consumer<String> alertException,
                           Consumer<List<AlliesDetailDTO>> alliesNameConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.exceptionConsumer = alertException;
        this.alliesNameConsumer = alliesNameConsumer;
        this.isServerUp = false;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(AgentConstants.GET_POSSIBLES_ALLIES, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                exceptionConsumer.accept("faild call :Something went wrong: " + e.getMessage());
                isServerUp = false;

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    exceptionConsumer.accept("Something went wrong: "+responseBody);
                    isServerUp = false;
                } else {
                    if(!isServerUp) {
                        exceptionConsumer.accept("");
                        isServerUp = true;
                    }
                    String jsonAlliesNameArray = responseBody;
                    AlliesDetailDTO[] AlliesInformationDTO = GSON_INSTANCE.fromJson(jsonAlliesNameArray, AlliesDetailDTO[].class);
                    Platform.runLater(()-> alliesNameConsumer.accept(Arrays.asList(AlliesInformationDTO)));

                }

            }
        });
    }

}

