package engine.decryptionManager.task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AgentCandidatesList {
    long startingTime;

    long duration;
    String agentName;


    private List<String> candidates;
    private List<String> configurationList;

    public boolean isEmpty() {
        return empty;
    }

    public boolean isPoisonPill() {
        return PoisonPill;
    }

    private boolean PoisonPill;
    private boolean empty;
    public AgentCandidatesList(){}
    public AgentCandidatesList(long startingTime, String agentName){
        this.candidates = new ArrayList<>();
        this.configurationList = new ArrayList<>();
        this.startingTime = startingTime;
        this.agentName = agentName;
        this.empty = true;
        this.PoisonPill = false;
    }
    public void setPoisonPill(){
        PoisonPill = true;
    }
    public void setDuration(){
        long end = System.nanoTime();
        duration = end - startingTime;
    }

    public void addCandidate(String candidate, String configuration){
        this.empty = false;
        this.candidates.add(candidate);
        this.configurationList.add(configuration);
    }
    public List<String> getCandidates() {
        return candidates;
    }

    public long getStartingTime() {
        return startingTime;
    }

    public String getAgentName() {
        return agentName;
    }
    public List<String> getConfigurationList() {
        return configurationList;
    }
    public long getDuration() {
        return duration;
    }
}
