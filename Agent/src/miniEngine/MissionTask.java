package miniEngine;

import DTOS.Configuration.UserConfigurationDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import client.contest.PropertiesToUpdate;
import dictionary.Dictionary;
import engine.decryptionManager.task.AgentCandidatesList;
import engine.enigma.Machine.EnigmaMachine;
import keyboard.Keyboard;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class MissionTask implements Runnable{

    public volatile static AtomicBoolean finish;
    private EnigmaMachine machine;

    public List<String> getPositions() {
        return positions;
    }

    // TODO: 9/11/2022 return positions to private
    public List<String> positions;
    private String toDecode;

    public BlockingDeque<AgentCandidatesList> getCandidateBlockingQueue() {
        return candidateBlockingQueue;
    }

    BlockingDeque<AgentCandidatesList> candidateBlockingQueue;
    Keyboard keyboard;
    private long StartingTime;


    public AgentCandidatesList getCandidatesList() {
        return candidatesList;
    }

    public AgentCandidatesList candidatesList;
    private Dictionary dictionary;

    private CountDownLatch cdl;
    BlockingDeque<CandidateDTO> candidateQueue;
    PropertiesToUpdate propertiesToUpdate;

    public MissionTask(EnigmaMachine machine, List<String> positions,
                       String toDecode, Dictionary dictionary,
                       CountDownLatch cdl, BlockingDeque<CandidateDTO> candidateQueue, PropertiesToUpdate propertiesToUpdate){
        this.machine = machine;
        this.positions = positions;
        this.toDecode = toDecode;
        this.dictionary = dictionary;
        this.cdl = cdl;
        finish = new AtomicBoolean(false);
        this.candidateQueue = candidateQueue;
        this.propertiesToUpdate = propertiesToUpdate;

    }


    @Override
    public void run() {
        this.StartingTime = System.nanoTime();
        String threadName = Thread.currentThread().getName();
        this.candidatesList = new AgentCandidatesList(StartingTime, threadName);
        exectuteMission(positions);
        if (!candidatesList.isEmpty()) {
            candidatesList.setDuration();
        }
        synchronized (propertiesToUpdate){
            propertiesToUpdate.addOneToCompletedMissions();
        }
        cdl.countDown();
        updateTime();


    }

    private void updateTime(){
        /*synchronized (timeToCalc){
            //updateProgress.run();
            long totalTime = System.nanoTime() - StartingTime;
            timeToCalc.addTimeToAverageMissionTime(totalTime);
            timeToCalc.updateTotalTasksTime();
        }*/
    }

    private void exectuteMission(List<String> positions) {
        for (String position : positions){
            processPosition(position);

            }
        }

    private void processPosition(String position) {
        String decipherResult = decipher(position);
        if (decipherResult != null) {
            machine.setPositions(position);//return to the position that found the candidate
            updateCandidate(decipherResult);
        }
    }

    public String decipher(String position)
    {
        machine.setPositions(position);
        String decryptionResult = machine.encodeString(toDecode);
        List<String> words = splitDecryptionToWords(decryptionResult);
        if(dictionary.isWordsInDictionary(words)) {
            return decryptionResult;
        }
        else
            return null;
    }

    public List<String> splitDecryptionToWords(String decryptionResult){
        return Arrays.asList(decryptionResult.split(" "));
    }

    // TODO: 9/14/2022 maybe update in after execute 
    public void updateCandidate(String words) {
        String threadName = Thread.currentThread().getName();
        UserConfigurationDTO configurationDTO = new UserConfigurationDTO(machine);

        StringBuilder currConfig = configurationDTO.getCodeConfigurationString();
        synchronized (candidateQueue) {
            CandidateDTO candidate = new CandidateDTO(words,currConfig.toString());
            boolean isPused = candidateQueue.offer(candidate);
        }
        synchronized (propertiesToUpdate){
            propertiesToUpdate.addOneToCandidateAmount();
        }
        //candidatesList.addCandidate(words,currConfig.toString());
    }
}
