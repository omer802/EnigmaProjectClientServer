package miniEngine;

import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import com.sun.jmx.snmp.tasks.Task;
import contants.AgentConstants;
import dictionary.Dictionary;
import engine.decryptionManager.CustomThreadPool.CustomThreadPoolExecutor;
import engine.decryptionManager.CustomThreadPool.ThreadFactoryBuilder;
import engine.decryptionManager.MathCalculations.CodeGenerator;
import engine.decryptionManager.task.MissionTask;
import engine.enigma.Machine.EnigmaMachine;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import keyboard.Keyboard;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;

public class AgentDecipherManager implements Task {
    private EnigmaMachine enigmaMachine;
    private Dictionary dictionary;
    private int missionSize;
    private AgentInfoDTO agentInfoDTO;
    private BooleanProperty inContest;
    private List<AgentTaskConfigurationDTO> currentAgentTaskConfigurationDTOList;
    Consumer<String> errorMessageConsumer;
    private int missionAmount;
    private boolean isEmptyQueue;
    private BlockingQueue<Runnable> blockingQueue;
    private CustomThreadPoolExecutor executor;
    private CodeGenerator codeGenerator;
    private int positionsLength;
    private Keyboard keyboard;
    private static final int OVERHEAD = 10;

    public AgentDecipherManager(EnigmaMachine enigmaMachine, Dictionary dictionary, int missionSize, AgentInfoDTO agentInfoDTO,
                                int missionAmount, Consumer<String> errorMessageConsumer, String alphabet) {
        this.enigmaMachine = enigmaMachine;
        this.dictionary = dictionary;
        this.missionSize = missionSize;
        this.agentInfoDTO = agentInfoDTO;
        this.missionAmount = missionAmount;
        this.inContest = new SimpleBooleanProperty(true);
        this.errorMessageConsumer = errorMessageConsumer;
        this.isEmptyQueue = true;
        this.positionsLength = enigmaMachine.getRotorsAmountInUse();
        this.keyboard = new Keyboard(alphabet);
        this.codeGenerator = new CodeGenerator(positionsLength);
        setThreadPool(agentInfoDTO.getThreadAmount(),agentInfoDTO.getMissionAmount());
    }

    private void setThreadPool(int threadAmount, int missionAmount) {
        this.blockingQueue =
                new LinkedBlockingQueue<>(missionAmount+OVERHEAD);

        ThreadFactory customThreadFactory = new ThreadFactoryBuilder()
                .setNamePrefix("Thread")
                .setPriority(Thread.NORM_PRIORITY)
                //set Uncaught exception to get the thread how throw UncaughtException
                .setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        System.err.println(String.format(
                                "Thread %s threw exception - %s", t.getName(),
                                e.getMessage()));

                    }
                }).build();
        this.executor =
                new CustomThreadPoolExecutor(threadAmount, threadAmount, 1000, TimeUnit.MINUTES,
                        this.blockingQueue, customThreadFactory);
    }

    @Override
    public void run() {

    }
    public void tryRun() {
        executor.prestartAllCoreThreads();
        /*System.out.println("Fetch tasks from server");
        getTasksFromServer();*/
        if(isEmptyQueue){
            getTasksFromServer();
        }
    }

    private void getTasksFromServer() {

        String finalUrl = HttpUrl
                .parse(AgentConstants.GET_MISSIONS)
                .newBuilder()
                .addQueryParameter("missionAmount", String.valueOf(missionAmount))
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    errorMessageConsumer.accept("faild call: Something went wrong:" +e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonTasksFromServer = response.body().string();
                System.out.println(jsonTasksFromServer);
                if (response.code() != 200) {
                     errorMessageConsumer.accept("Something went wrong while fetching agent data from server: "+ jsonTasksFromServer);
                } else {
                    AgentTaskConfigurationDTO[] agentTaskConfigurationDTOS = GSON_INSTANCE.fromJson(jsonTasksFromServer, AgentTaskConfigurationDTO[].class);
                    currentAgentTaskConfigurationDTOList = Arrays.asList(agentTaskConfigurationDTOS);
                    pushMissionsToQueue(currentAgentTaskConfigurationDTOList);
                    Platform.runLater(() -> {
                        errorMessageConsumer.accept("");


                    });
                }
            }
        });
    }
    public void pushMissionsToQueue(List<AgentTaskConfigurationDTO> agentTaskConfigurationDTOS){
        for (AgentTaskConfigurationDTO agentConfig: agentTaskConfigurationDTOS) {
            enigmaMachine.selectInitialCodeConfiguration(agentConfig.getUserConfigurationDTO());
            List<String> positionList = codeGenerator.generatePositionsGivenStartAndSize(agentConfig.getStartPosition(),agentConfig.getMissionSize());
            MissionTask missionTask = new MissionTask(enigmaMachine.clone(),positionList,agentConfig.getMessageToDecode(),dictionary);
            blockingQueue.add(missionTask);
        }
    }

    @Override
    public void cancel() {

    }
}
