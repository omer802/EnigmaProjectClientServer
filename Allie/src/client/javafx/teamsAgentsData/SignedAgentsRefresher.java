package client.javafx.teamsAgentsData;


import DTOS.agentInformationDTO.AgentInfoDTO;
import client.constants.AlliesConstants;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public class SignedAgentsRefresher extends TimerTask {

    private final Consumer<List<AgentInfoDTO>> agentsConsumer;
    private final Consumer<String> httpRequestLoggerConsumer;
    private BooleanProperty shouldUpdate =new SimpleBooleanProperty();

    public SignedAgentsRefresher(BooleanProperty shouldUpdate,
                                 Consumer<String> httpRequestLoggerConsumer,
                                 Consumer<List<AgentInfoDTO>> ContestDataConsumer) {
        this.shouldUpdate.bind(shouldUpdate);
        this.agentsConsumer = ContestDataConsumer;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
    }
    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        HttpClientUtil.runAsync(AlliesConstants.GET_SIGNED_AGENTS, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept("faild call :Something went wrong: "+e.getMessage());

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if(!response.isSuccessful()){
                    httpRequestLoggerConsumer.accept("Something went wrong:"+ responseBody);
                }
                else {
                    AgentInfoDTO[] agentInfoDTO = GSON_INSTANCE.fromJson(responseBody, AgentInfoDTO[].class);
                    if(agentInfoDTO== null)
                        return;
                    agentsConsumer.accept(Arrays.asList(agentInfoDTO));
                }

            }
        });
    }
}
