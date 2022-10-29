package client.javafx.contestPage.AgentsProgressAndDM;

import DTOS.AllieInformationDTO.AgentAndDMProgressDTO;
import DTOS.agentInformationDTO.AgentProgressDTO;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;

public class agentAndDmProgressRefresher extends TimerTask {


    private final Consumer<AgentAndDMProgressDTO> candidatesConsumer;
    private final Consumer<Exception> httpRequestLoggerConsumer;
    private BooleanProperty shouldUpdate = new SimpleBooleanProperty();


    public agentAndDmProgressRefresher(BooleanProperty shouldUpdate,
                                       Consumer<Exception> httpRequestLoggerConsumer,
                                       Consumer<AgentAndDMProgressDTO> ContestDataConsumer) {
        this.shouldUpdate.bind(shouldUpdate);
        this.candidatesConsumer = ContestDataConsumer;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
    }

    @Override
    public void run() {
        /*if (!shouldUpdate.get()) {
            return;
        }*/

        HttpClientUtil.runAsync(CommonConstants.GET_AGENT_AND_DM_PROGRESS, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept(new RuntimeException("faild call :Something went wrong: " + e.getMessage()));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (!response.isSuccessful()) {
                    httpRequestLoggerConsumer.accept(new RuntimeException("Something went wrong:" + responseBody));
                } else {
                    AgentAndDMProgressDTO agentAndDMProgressDTO  = GSON_INSTANCE.fromJson(responseBody,AgentAndDMProgressDTO.class);
                    if (agentAndDMProgressDTO == null)
                        return;
                    candidatesConsumer.accept(agentAndDMProgressDTO);
                }

            }
        });
    }
}
