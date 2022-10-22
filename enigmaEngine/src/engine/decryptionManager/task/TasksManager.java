package engine.decryptionManager.task;

import DTOS.decryptionManager.DecryptionManagerDTO;
import dictionary.Dictionary;
import engine.decryptionManager.MathCalculations.RotorsPermuter;
import engine.decryptionManager.MathCalculations.CodeGenerator;
import engine.enigma.Machine.EnigmaMachine;
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



    private BlockingQueue<MissionTask> blockingQueue;
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

    public TasksManager(DecryptionManagerDTO decryptionManagerDTO, EnigmaMachine machine, TimeToCalc timeToCalc, Dictionary dictionary, BlockingQueue<MissionTask> blockingQueue) {
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

    }

    @Override
    public void run() {
        //wait(1000 );
        //updateProgress(0, totalMissionAmount);
        CodeGenerator codeGenerator = new CodeGenerator(positionLength);
        //UpdateCandidateConsumer candidateConsumer = new UpdateCandidateConsumer(candidateBlockingQueue);
        //blockingConsumer = new Thread(candidateConsumer, "AgentCandidatesList consumer thread");
       // blockingConsumer.start();
        try {
            generateMissionByLevel(difficulty, codeGenerator);

        } catch (InterruptedException e) {
        } finally {


            //candidateConsumer.finish();
            synchronized (timeToCalc) {
                timeToCalc.notifyAll();
            }
        }
    }



    private void generateMissionByLevel(UBoat.DifficultyLevel difficulty, CodeGenerator codeGenerator) throws InterruptedException {
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
                new LinkedBlockingQueue<MissionTask>(1000);

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

    private void impossible(CodeGenerator codeGenerator) throws InterruptedException {
        List<String> possibleRotors = machine.getPossibleRotors();
        List<int[]> possibleRotorsIndex = codeGenerator.generateNChooseK(possibleRotors.size(),machine.getRotorsAmountInUse());
        for (int[] indexArray: possibleRotorsIndex) {
            List<String> p = getRotorsByIndexList(indexArray,possibleRotors);
            machine.setChosenRotors(p);
            hardDifficultyLevel(codeGenerator);

        }

    }
    //in this level we dont have the reflector, position, and location of rotors in the slots
    private void hardDifficultyLevel(CodeGenerator codeGenerator) throws InterruptedException {
        List <String> chosenRotors = machine.getChosenRotors();
        RotorsPermuter permuter = new RotorsPermuter(chosenRotors.size());
        int[] indexList = permuter.getNext();
        while (indexList!=null){
            List<String> chosenRotorsNewOrder = getRotorsByIndexList(indexList,chosenRotors);
            machine.setChosenRotors(chosenRotorsNewOrder);
            indexList = permuter.getNext();
            mediumDifficultyLevel(codeGenerator);
        }
    }
    private List<String> getRotorsByIndexList(int[] indexList, List<String> chosenRotors){
        List<String> chosenRotorsToReturn = new ArrayList<>(chosenRotors.size());
        for (int i = 0; i < indexList.length; i++) {
            chosenRotorsToReturn.add(i,chosenRotors.get(indexList[i]));
        }
        return chosenRotorsToReturn;
    }
    //in this level we dont have the reflector and the positions
    private void mediumDifficultyLevel(CodeGenerator codeGenerator) throws InterruptedException {
        List<String> reflectorList = machine.getPossibleReflectors();
        for (String reflector:
             reflectorList) {
            machine.setReflector(reflector);
            easyDifficultyLevel(codeGenerator);
        }
    }

    //in this level we dont have the positions
    private void easyDifficultyLevel(CodeGenerator codeGenerator) throws InterruptedException {
        double numOfTasks = calculateAmountOfCodes()/missionSize;
        double leakageSizeTask = ((int)calculateAmountOfCodes())  % missionSize;

        if(leakageSizeTask>0) {
            generateTaskAndPushToBlockingQueue(codeGenerator,leakageSizeTask);
            numOfTasks--;
        }


        for (int i = 0; i <numOfTasks ; i++)
            generateTaskAndPushToBlockingQueue(codeGenerator,missionSize);
    }

    private void generateTaskAndPushToBlockingQueue(CodeGenerator codeGenerator, Double missionSize) throws InterruptedException {
        List<String> positionsList = codeGenerator.generateNextPositionsListForTask(missionSize);
        System.out.println(positionsList);
        MissionTask task = new MissionTask(machine.clone(),positionsList,messageToDecode,dictionary, timeToCalc);


        pushTaskToBlockingQueue(task);
    }
    private void pushTaskToBlockingQueue(MissionTask task) throws InterruptedException {
        boolean insertToBlockingQueue = false;
        while (!insertToBlockingQueue) {
            synchronized (blockingQueue) {
                insertToBlockingQueue = blockingQueue.offer(task);
            }
        }
    }
    public double calculateAmountOfCodes(){
        int alphabetSize = Keyboard.alphabet.length();
        int exponent = machine.getRotorsAmountInUse();
        double amountOfCodes = Math.pow(alphabetSize,exponent);

        return amountOfCodes;
    }

}
