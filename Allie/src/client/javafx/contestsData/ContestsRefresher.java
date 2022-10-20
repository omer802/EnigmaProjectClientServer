package client.javafx.contestsData;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import client.constants.AlliesConstants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;


public class ContestsRefresher extends TimerTask {
    private final Consumer<List<ContestInformationDTO>> ContestsCounsumer;
    private final Consumer<Exception> httpRequestLoggerConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;

    public ContestsRefresher(BooleanProperty shouldUpdate,
                             Consumer<Exception> httpRequestLoggerConsumer,
                             Consumer<List<ContestInformationDTO>> ContestDataConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.ContestsCounsumer = ContestDataConsumer;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        requestNumber = 0;
    }



    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        HttpClientUtil.runAsync(AlliesConstants.GET_CONTESTS, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept(new RuntimeException
                        (e.getMessage()));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if(!response.isSuccessful()){
                    httpRequestLoggerConsumer.accept(new RuntimeException(responseBody));
                }
                else {
                    String jsonArrayOfContests = responseBody;
                    ContestInformationDTO[] contestInformationDTO = GSON_INSTANCE.fromJson(jsonArrayOfContests, ContestInformationDTO[].class);
                    ContestsCounsumer.accept(Arrays.asList(contestInformationDTO));
                }

            }
        });
    }
}
