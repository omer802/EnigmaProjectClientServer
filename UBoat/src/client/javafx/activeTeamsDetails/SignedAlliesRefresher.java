package client.javafx.activeTeamsDetails;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import client.constants.ConstantsUBoat;
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

public class SignedAlliesRefresher extends TimerTask {

    private final Consumer<List<AlliesDetailDTO>> AlliesDataConsumer;
    private final Consumer<Exception> httpRequestLoggerConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;

    public SignedAlliesRefresher(BooleanProperty shouldUpdate, Consumer<Exception> httpRequestLoggerConsumer, Consumer<List<AlliesDetailDTO>> alliesDataConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.AlliesDataConsumer = alliesDataConsumer;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        requestNumber = 0;
    }


    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        HttpClientUtil.runAsync(ConstantsUBoat.GET_SIGNED_ALLIES, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept(new RuntimeException
                        ("Users Request # " + finalRequestNumber + e.getMessage() + " | Ended with failure..."));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfContests =response.body().string();
                if (response.code() != 200) {
                    if(response.code() == 204){

                    }
                } else {

                        AlliesDetailDTO[] contestInformationDTO = GSON_INSTANCE.fromJson(jsonArrayOfContests, AlliesDetailDTO[].class);
                       // if(contestInformationDTO!=null) {
                            AlliesDataConsumer.accept(Arrays.asList(contestInformationDTO));
                       // }
                }
            }
        });
    }
}

