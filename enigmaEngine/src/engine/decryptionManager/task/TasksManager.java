package engine.decryptionManager.task;

import DTOS.Configuration.UserConfigurationDTO;
import DTOS.decryptionManager.DecryptionManagerDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import dictionary.Dictionary;
import engine.decryptionManager.MathCalculations.RotorsPermuter;
import engine.decryptionManager.MathCalculations.CodeGenerator;
import engine.enigma.Machine.EnigmaMachine;
import javafx.beans.property.SimpleLongProperty;
import keyboard.Keyboard;
import registerManagers.clients.UBoat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class TasksManager implements Runnable {
    private Double missionSize;

    // TODO: 9/11/2022 change difficulty to enum
    private UBoat.DifficultyLevel difficulty;
    private String messageToDecode;
    private int possibleAmountOfCodes;
    private EnigmaMachine machine;



    private BlockingQueue<AgentTaskConfigurationDTO> blockingQueue;
    private int positionLength;
    private double totalMissionAmount;
    private AtomicLong totalMissionAmountToSend;

    public TimeToCalc getTimeToCalc() {
        return timeToCalc;
    }

    TimeToCalc timeToCalc;
    private final int[] missionCount;

   // BlockingDeque<AgentCandidatesList> candidateBlockingQueue;
    //private Thread blockingConsumer;
    private Dictionary dictionary;
    private SimpleLongProperty generatedMissionsAmount = new SimpleLongProperty();
    public int counter = 0;
    public boolean isActiveContest() {
        return activeContest;
    }

    public void setActiveContest(boolean activeContest) {
        this.activeContest = activeContest;
    }

    private boolean activeContest;

    public TasksManager(DecryptionManagerDTO decryptionManagerDTO, EnigmaMachine machine, TimeToCalc timeToCalc, Dictionary dictionary, BlockingQueue<AgentTaskConfigurationDTO> blockingQueue, SimpleLongProperty generatedMissionsAmount) {
        this.missionSize = decryptionManagerDTO.getMissionSize();
        this.difficulty = decryptionManagerDTO.getLevel();
        this.positionLength = machine.getRotorsAmountInUse();
        this.missionCount = new int[1];
        this.missionCount[0] = 1;
        this.totalMissionAmount = decryptionManagerDTO.getTotalMissionAmount();
        setMessageToDecipher(decryptionManagerDTO.getMessageToDecipher());
        this.blockingQueue = blockingQueue;
        //candidateBlockingQueue = new LinkedBlockingDeque<>();
        this.machine = machine;
        this.timeToCalc = timeToCalc;
        this.totalMissionAmountToSend = new AtomicLong((long) totalMissionAmount);
        this.dictionary = dictionary;
        generatedMissionsAmount.bind(this.generatedMissionsAmount);
        activeContest = true;

    }

    @Override
    public void run() {
        //wait(1000 );
        //updateProgress(0, totalMissionAmount);
        try {
            CodeGenerator codeGenerator = new CodeGenerator(positionLength);
            generateMissionByLevel(difficulty, codeGenerator);
            //candidateConsumer.finish();
            synchronized (timeToCalc) {
                timeToCalc.notifyAll();
            }
        } catch (RuntimeException e) {
            return;
        }

        //UpdateCandidateConsumer candidateConsumer = new UpdateCandidateConsumer(candidateBlockingQueue);
        //blockingConsumer = new Thread(candidateConsumer, "AgentCandidatesList consumer thread");
        // blockingConsumer.start();


    }




    private void generateMissionByLevel(UBoat.DifficultyLevel difficulty, CodeGenerator codeGenerator)  {
        switch (difficulty) {
            case EASY:
                easyDifficultyLevel(codeGenerator);
                break;
            case MEDIUM:
                mediumDifficultyLevel(codeGenerator);
                break;
            case HARD:
                hardDifficultyLevel(codeGenerator);
                break;
            case IMPOSSIBLE:
                impossible(codeGenerator);
                break;
        }
    }


    public void setMessageToDecipher(String messageToDecipher){
        this.messageToDecode = messageToDecipher;
    }
    public void createBlockingQueue(){
        this.blockingQueue =
                new LinkedBlockingQueue<AgentTaskConfigurationDTO>(1000);

        /*ThreadFactory customThreadFactory = new ThreadFactoryBuilder()
                .setNamePrefix("Agent")
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
                new CustomThreadPoolExecutor(agentsAmount, agentsAmount, 1000, TimeUnit.MINUTES,
                        this.blockingQueue, customThreadFactory);*/
    }

    private void impossible(CodeGenerator codeGenerator)  {
        List<String> possibleRotors = machine.getPossibleRotors();
        List<int[]> possibleRotorsIndex = codeGenerator.generateNChooseK(possibleRotors.size(),machine.getRotorsAmountInUse());
        for (int[] indexArray: possibleRotorsIndex) {
            List<String> p = getRotorsByIndexList(indexArray,possibleRotors);
            machine.setChosenRotors(p);
            if(activeContest) {
                hardDifficultyLevel(codeGenerator);
            }
        }

    }
    //in this level we dont have the reflector, position, and location of rotors in the slots
    private void hardDifficultyLevel(CodeGenerator codeGenerator)  {
        List <String> chosenRotors = machine.getChosenRotors();
        RotorsPermuter permuter = new RotorsPermuter(chosenRotors.size());
        int[] indexList = permuter.getNext();
        while (indexList!=null) {
            List<String> chosenRotorsNewOrder = getRotorsByIndexList(indexList, chosenRotors);
            machine.setChosenRotors(chosenRotorsNewOrder);
            indexList = permuter.getNext();
            if (activeContest) {
                mediumDifficultyLevel(codeGenerator);
            }
        }
    }

    //in this level we dont have the reflector and the positions
    private void mediumDifficultyLevel(CodeGenerator codeGenerator) {
        List<String> reflectorList = machine.getPossibleReflectors();
        for (String reflector:
             reflectorList) {
            machine.setReflector(reflector);
            if (activeContest) {
                easyDifficultyLevel(codeGenerator);
            }
        }
    }

    //in this level we dont have the positions
    private void easyDifficultyLevel(CodeGenerator codeGenerator) {
        double numOfTasks = calculateAmountOfCodes() / missionSize;
        double leakageSizeTask = ((int) calculateAmountOfCodes()) % missionSize;

        if (leakageSizeTask > 0) {
            generateTaskAndPushToBlockingQueue(codeGenerator, leakageSizeTask);
            numOfTasks--;
        }


        for (int i = 0; i < numOfTasks; i++) {
            if (activeContest) {
                generateTaskAndPushToBlockingQueue(codeGenerator, missionSize);
            }

        }
    }

    private void generateTaskAndPushToBlockingQueue(CodeGenerator codeGenerator, Double missionSize) {
        List<String> positionsList = codeGenerator.generateNextPositionsListForTask(missionSize);
        String startingPosition = positionsList.get(0);
        AgentTaskConfigurationDTO agentTaskConfiguration =
                new AgentTaskConfigurationDTO(new UserConfigurationDTO(machine), startingPosition, missionSize, messageToDecode);
        //System.out.println(" in task manger!!");
        //System.out.println(positionsList);
        //MissionTask task = new MissionTask(machine.clone(),positionsList,messageToDecode,dictionary, timeToCalc);
        if (activeContest) {
            pushTaskToBlockingQueue(agentTaskConfiguration);
        }
    }
    private void pushTaskToBlockingQueue(AgentTaskConfigurationDTO agentTaskConfiguration)  {
        boolean insertToBlockingQueue = false;
        generatedMissionsAmount.setValue(generatedMissionsAmount.getValue()+1);
        System.out.println(counter++);
        while (!insertToBlockingQueue) {
            synchronized (blockingQueue) {
                if (activeContest) {
                    insertToBlockingQueue = blockingQueue.offer(agentTaskConfiguration);
                }
                else{
                    System.out.println("************* task manager finish task");
                    throw new RuntimeException();
                }
            }
        }
    }
    public double calculateAmountOfCodes(){
        int alphabetSize = Keyboard.alphabet.length();
        int exponent = machine.getRotorsAmountInUse();
        double amountOfCodes = Math.pow(alphabetSize,exponent);

        return amountOfCodes;
    }
    private List<String> getRotorsByIndexList(int[] indexList, List<String> chosenRotors){
        List<String> chosenRotorsToReturn = new ArrayList<>(chosenRotors.size());
        for (int i = 0; i < indexList.length; i++) {
            chosenRotorsToReturn.add(i,chosenRotors.get(indexList[i]));
        }
        return chosenRotorsToReturn;
    }
}
