package engine.decryptionManager;

import DTOS.decryptionManager.DecryptionManagerDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import dictionary.Dictionary;
import dictionary.Trie;
import engine.decryptionManager.task.MissionTask;
import engine.decryptionManager.task.TasksManager;
import engine.decryptionManager.task.TimeToCalc;
import engine.enigma.Machine.EnigmaMachine;
import keyboard.Keyboard;
import registerManagers.clients.UBoat;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DM implements Cloneable {


    private Dictionary dictionary;

    private TimeToCalc timeToCalc;
    private Double missionSize;
    private EnigmaMachine machine;
    private TasksManager tasksCreator;

    public BlockingQueue<AgentTaskConfigurationDTO> getBlockingQueue() {
        return blockingQueue;
    }

    private BlockingQueue<AgentTaskConfigurationDTO> blockingQueue;
    public DM(Dictionary dictionary, EnigmaMachine machine){
        this.dictionary = dictionary;
        this.machine = machine;

    }
    public void DecipherMessage(DecryptionManagerDTO decryptionManagerDTO){

        this.missionSize = decryptionManagerDTO.getMissionSize();
        this.blockingQueue = new LinkedBlockingQueue<AgentTaskConfigurationDTO>(1000);

        // TODO: 9/16/2022 add check if agents amount ok
            this.timeToCalc = new TimeToCalc(System.currentTimeMillis());
            this.tasksCreator = new TasksManager(decryptionManagerDTO,machine.clone(),timeToCalc,dictionary,blockingQueue);

        new Thread(tasksCreator,"Manager tasks thread ").start();
    }
    public boolean isDictionaryContainString(String str){
        return dictionary.isDictionaryContainString(str);
    }
    public TimeToCalc getTimeToCalc(){
        return tasksCreator.getTimeToCalc();
    }
    public void cancelCurrentTask(){
        //System.out.println("cancal!");
    }
    public String cleanStringFromExcludeChars(String words){
        return dictionary.cleanStringFromExcludeChars(words);
    }
    public Trie getTrieFromDictionary(){
        return dictionary.getTrie();
    }


    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public EnigmaMachine getMachine() {
        return machine;
    }

    public void setMachine(EnigmaMachine machine) {
        this.machine = machine;
    }



   /* public int getAmountOfAgents() {
        return AgentAmount;
    }*/
    public  double calculateAmountOfTasks(long missionSize, UBoat.DifficultyLevel level) {
        double amountOfMission = 0;
        switch (level) {
            case EASY:
                amountOfMission = calculateAmountOfTasksEasyLevel(missionSize);
                break;
            case MEDIUM:
                amountOfMission = calculateAmountOfTasksMediumLevel(missionSize);
                break;
            case HARD:
                amountOfMission = calculateAmountOfTasksHardLevel(missionSize);
                break;
            case IMPOSSIBLE:
                amountOfMission = calculateAmountOfTasksImpossibleLevel(missionSize);
                break;
        }
        return amountOfMission;
    }
    //calculate the amount of options to choose k rotors from n rotors in machine
    private double calculateAmountOfTasksImpossibleLevel(long missionSize){
        BigInteger ret = BigInteger.ONE;
        int amountOfRotors = machine.getAmountOfRotors();
        int chosenAmount = machine.getRotorsAmountInUse();
        for (int k = 0; k < chosenAmount; k++) {
            ret = ret.multiply(BigInteger.valueOf(amountOfRotors-k))
                    .divide(BigInteger.valueOf(k+1));
        }
        return ret.doubleValue() * calculateAmountOfTasksHardLevel(missionSize);
    }

    //calculate amountOfRotors factorial
    private double calculateAmountOfTasksHardLevel(long missionSize) {
        int factorial =1;
        for (int i = 1; i <= machine.getRotorsAmountInUse() ; i++) {
            factorial *=i;
        }
        return factorial * calculateAmountOfTasksMediumLevel(missionSize);
    }


    private double calculateAmountOfTasksMediumLevel(long missionSize){
        return calculateAmountOfTasksEasyLevel(missionSize) * machine.getReflectorsAmount();
    }
    private double calculateAmountOfTasksEasyLevel(long missionSize){
        int amountOfTasks = Keyboard.alphabet.length();
        int exponent = machine.getRotorsAmountInUse();
        double numOfTask = Math.pow(amountOfTasks,exponent);
        return numOfTask/missionSize;
    }

    @Override
    public DM clone() throws CloneNotSupportedException {
        DM dm = (DM) super.clone();
        dm.setMachine(machine.clone());
        dm.setDictionary(dictionary);
        //dm.setMaxAgentAmount(maxAgentAmount);
        return dm;
    }
}
