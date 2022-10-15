package engine.decryptionManager;

import DTOS.decryptionManager.DecryptionManagerDTO;
import dictionary.Dictionary;
import dictionary.Trie;
import engine.decryptionManager.task.TasksManager;
import engine.decryptionManager.task.TimeToCalc;
import engine.enigma.Machine.EnigmaMachine;
import keyboard.Keyboard;
import registerManagers.UBoatManager.UBoat;

import java.math.BigInteger;

public class DM {




    // TODO: 9/10/2022 change from agents to tasks
   /* public static enum DifficultyLevel{
        EASY, MEDIUM, HARD, IMPOSSIBLE
    }*/
    private int maxAgentAmount;
    private Dictionary dictionary;

    private TimeToCalc timeToCalc;
    private Double missionSize;
    private EnigmaMachine machine;
    private TasksManager tasksCreator;
    public DM(Dictionary dictionary, EnigmaMachine machine){
        this.dictionary = dictionary;
        this.machine = machine;

    }



    public void DecipherMessage(DecryptionManagerDTO decryptionManagerDTO, Runnable onFinish){

        this.missionSize = decryptionManagerDTO.getMissionSize();

        // TODO: 9/16/2022 add check if agents amount ok
        if(decryptionManagerDTO.getAmountOfAgentsForProcess()>maxAgentAmount)
            throw new RuntimeException();
            this.timeToCalc = new TimeToCalc(System.currentTimeMillis());
            this.tasksCreator = new TasksManager(decryptionManagerDTO,machine.clone(),timeToCalc,dictionary);

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
        tasksCreator.cancel();
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



    public int getAmountOfAgents() {
        return maxAgentAmount;
    }
    public  double calculateAmountOfTasks(Integer missionSize, UBoat.DifficultyLevel level) {
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
    private double calculateAmountOfTasksImpossibleLevel(Integer missionSize){
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
    private double calculateAmountOfTasksHardLevel(Integer missionSize) {
        int factorial =1;
        for (int i = 1; i <= machine.getRotorsAmountInUse() ; i++) {
            factorial *=i;
        }
        return factorial * calculateAmountOfTasksMediumLevel(missionSize);
    }


    private double calculateAmountOfTasksMediumLevel(Integer missionSize){
        return calculateAmountOfTasksEasyLevel(missionSize) * machine.getReflectorsAmount();
    }
    private double calculateAmountOfTasksEasyLevel(Integer missionSize){
        int amountOfTasks = Keyboard.alphabet.length();
        int exponent = machine.getRotorsAmountInUse();
        double numOfTask = Math.pow(amountOfTasks,exponent);
        return numOfTask/missionSize;
    }

}
