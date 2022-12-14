package miniEngine;

import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import client.agentMainPage.candidate.CandidateController;
import client.contest.PropertiesToUpdate;
import com.sun.jmx.snmp.tasks.Task;
import contants.AgentConstants;
import dictionary.Dictionary;
import miniEngine.CustomThreadPool.CustomThreadPoolExecutor;
import miniEngine.CustomThreadPool.ThreadFactoryBuilder;
import engine.decryptionManager.MathCalculations.CodeGenerator;
import engine.enigma.Machine.EnigmaMachine;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import keyboard.Keyboard;
import okhttp3.*;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;
import static util.CommonConstants.REFRESH_RATE;

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
    private BlockingQueue<Runnable> threadPoolBlockingQueue;
    private CustomThreadPoolExecutor executor;
    private CodeGenerator codeGenerator;
    private int positionsLength;
    private Keyboard keyboard;
    private static final int OVERHEAD = 10;

    public boolean isIsActiveContest() {
        return isActiveContest.get();
    }

    public SimpleBooleanProperty isActiveContestProperty() {
        return isActiveContest;
    }

    public void setIsActiveContest(boolean isActiveContest) {
        this.isActiveContest.set(isActiveContest);
    }

    public SimpleBooleanProperty isActiveContest = new SimpleBooleanProperty();
    private CandidateController candidatesController;
    private BlockingDeque<CandidateDTO> candidateQueue;
    final PropertiesToUpdate propertiesToUpdate;
    private CountDownLatch cdl;

    public AgentDecipherManager(EnigmaMachine enigmaMachine, Dictionary dictionary, int missionSize, AgentInfoDTO agentInfoDTO,
                                int missionAmount, Consumer<String> errorMessageConsumer, String alphabet, CandidateController candidatesController, PropertiesToUpdate propertiesToUpdateInBruteForce, SimpleBooleanProperty isActiveContest) {
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
        this.isActiveContest.bind(isActiveContest);
        this.candidatesController = candidatesController;
        this.candidateQueue = new LinkedBlockingDeque<>();
        this.propertiesToUpdate = propertiesToUpdateInBruteForce;
        setThreadPool(agentInfoDTO.getThreadAmount(), agentInfoDTO.getMissionAmount());
    }

    private void setThreadPool(int threadAmount, int missionAmount) {
        this.threadPoolBlockingQueue =
                new LinkedBlockingQueue<>(missionAmount + OVERHEAD);

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
                        this.threadPoolBlockingQueue, customThreadFactory);
    }

    @Override
    public void run() {
        executor.prestartAllCoreThreads();

        while (isActiveContest.get()) {
            try {
                currentAgentTaskConfigurationDTOList = getTasksFromServerAndUpdateCDL();
                while (currentAgentTaskConfigurationDTOList == null && isActiveContest.getValue()) {
                    currentAgentTaskConfigurationDTOList = getTasksFromServerAndUpdateCDL();

                }
                if (!isActiveContest.getValue())
                    return;
                cdl = new CountDownLatch(currentAgentTaskConfigurationDTOList.size());
                pushMissionsToQueue(currentAgentTaskConfigurationDTOList);
                try {
                    cdl.await();
                    synchronized (propertiesToUpdate) {

                        if (isActiveContest.get()) {
                            synchronized (threadPoolBlockingQueue) {
                                propertiesToUpdate.setMissionInQueue(threadPoolBlockingQueue.size());
                            }
                            if ((!candidateQueue.isEmpty())) {
                                List<CandidateDTO> candidateList = new ArrayList<>();

                                while ((!candidateQueue.isEmpty())) {
                                    CandidateDTO candidate = candidateQueue.poll();
                                    candidateList.add(candidate);
                                }
                                candidatesController.updateCandidate(candidateList);
                            }
                        }
                    }
                    if (!isActiveContest.get()) {
                        try {
                            executor.awaitTermination(20, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("throw exception while waiting for thread-pool to finish");
                        }
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExceptionInInitializerError e) {
                isActiveContest.set(false);
                try {
                    executor.awaitTermination(20, TimeUnit.SECONDS);
                } catch (InterruptedException ex) {
                    e.printStackTrace();
                }
            }

        }
    }




    public void shutDownBruteForce() {
        initPropertiesToUpdate();
    }

    private void initPropertiesToUpdate() {
        propertiesToUpdate.setMissionInQueue(0);
        propertiesToUpdate.setMissionFromServer(0);
        propertiesToUpdate.setCandidates(0);
        propertiesToUpdate.setCompletedMissions(0);

    }


    private List<AgentTaskConfigurationDTO> getTasksFromServerAndUpdateCDL() throws IOException {

        String finalUrl = HttpUrl
                .parse(AgentConstants.GET_MISSIONS)
                .newBuilder()
                .addQueryParameter("missionAmount", String.valueOf(missionAmount))
                .build()
                .toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Response response = HttpClientUtil.runSync(request);
        if (response.code() == 200) {
            String jsonTasksFromServer = response.body().string();
            AgentTaskConfigurationDTO[] agentTaskConfigurationDTOS = GSON_INSTANCE.fromJson(jsonTasksFromServer, AgentTaskConfigurationDTO[].class);
            if (agentTaskConfigurationDTOS == null) {
                return null;
            }
            List<AgentTaskConfigurationDTO> agentTaskConfigurationDTOList = Arrays.asList(agentTaskConfigurationDTOS);
            Platform.runLater(() -> {
                errorMessageConsumer.accept("");

            });
            return agentTaskConfigurationDTOList;
        } else if (response.code() == 403) {
            throw new ExceptionInInitializerError("finish game allready");
        } else {
            String jsonTasksFromServer = response.body().string();
            errorMessageConsumer.accept("Something went wrong while fetching agent data from server: " + jsonTasksFromServer);
            return null;
        }
    }




       /* HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    errorMessageConsumer.accept("faild call: Something went wrong:" +e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonTasksFromServer = response.body().string();
                if (response.code() != 200) {
                     errorMessageConsumer.accept("Something went wrong while fetching agent data from server: "+ jsonTasksFromServer);
                } else {
                    AgentTaskConfigurationDTO[] agentTaskConfigurationDTOS = GSON_INSTANCE.fromJson(jsonTasksFromServer, AgentTaskConfigurationDTO[].class);
                    currentAgentTaskConfigurationDTOList = Arrays.asList(agentTaskConfigurationDTOS);
                    cdl = new CountDownLatch(currentAgentTaskConfigurationDTOList.size());
                    pushMissionsToQueue(currentAgentTaskConfigurationDTOList,cdl);
                    Platform.runLater(() -> {
                        errorMessageConsumer.accept("");

                    });
                }
            }
        });*/




    public void pushMissionsToQueue(List<AgentTaskConfigurationDTO> agentTaskConfigurationDTOS){
        synchronized (propertiesToUpdate) {
            propertiesToUpdate.addAmountToMissionFromServer(agentTaskConfigurationDTOS.size());
        }
        for (AgentTaskConfigurationDTO agentConfig: agentTaskConfigurationDTOS) {
            enigmaMachine.selectInitialCodeConfiguration(agentConfig.getUserConfigurationDTO());
            List<String> positionList = codeGenerator.generatePositionsGivenStartAndSize(agentConfig.getStartPosition(), agentConfig.getMissionSize());
            MissionTask missionTask = new MissionTask(enigmaMachine.clone(), positionList, agentConfig.getMessageToDecode(), dictionary, cdl, candidateQueue, propertiesToUpdate);
            threadPoolBlockingQueue.offer(missionTask);
            synchronized (threadPoolBlockingQueue) {
                // waiting missions = threadPoolBlockingQueue size - remaining capacity
                propertiesToUpdate.setMissionInQueue(threadPoolBlockingQueue.size());
            }
        }
    }

    @Override
    public void cancel() {

    }
}
