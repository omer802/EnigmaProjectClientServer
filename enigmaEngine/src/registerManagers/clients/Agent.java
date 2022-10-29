package registerManagers.clients;

import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.agentInformationDTO.AgentProgressDTO;
import dictionary.Dictionary;
import engine.enigma.Machine.EnigmaMachine;
import registerManagers.mediators.Mediator;

public class Agent implements Client,User {
    private Mediator mediator;
    private String agentName;
    private String chosenAlliesName;



    private EnigmaMachine enigmaMachine;
    private Dictionary dictionary;
    private boolean isReadyForContest;
    private int missionAmount;
    private int threadAmount;

    private long acceptedMissions;
    private int waitingMissions;
    private int candidateAmount;
    private long completedMissions;

    public String getChosenAlliesName() {
        return chosenAlliesName;
    }


    public Agent(AgentInfoDTO agentInfoDTO) {
        this.agentName = agentInfoDTO.getUserName();
        this.chosenAlliesName = agentInfoDTO.getAllieName();
        this.missionAmount = agentInfoDTO.getMissionAmount();
        this.threadAmount = agentInfoDTO.getThreadAmount();
        this.isReadyForContest = false;
    }
    //public AgentInfoDTO(String userName, String allieName, int threadAmount, int missionAmount) {

    public AgentInfoDTO createAgentInfoDTO() {
        return new AgentInfoDTO(getUserName(),getChosenAlliesName(),threadAmount,missionAmount);
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void startContest() {

    }

    @Override
    public String getUserName() {
        return agentName;
    }

    public void setMachineAndDictionary(EnigmaMachine enigmaMachine, Dictionary dictionary) {
        this.enigmaMachine = enigmaMachine;
        this.dictionary = dictionary;
        this.isReadyForContest = true;
    }
    public EnigmaMachine getEnigmaMachine() {
        return enigmaMachine;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    synchronized public void updateProgress(AgentProgressDTO agentProgressDTO) {
        this.acceptedMissions = agentProgressDTO.getAcceptedMissions();
        this.waitingMissions = agentProgressDTO.getWaitingMissions();
        this.candidateAmount = agentProgressDTO.getCandidatesAmount();
        this.completedMissions = agentProgressDTO.getCompletedMissionsAmount();

    }
    synchronized public AgentProgressDTO getProgressDTO(){
       return new AgentProgressDTO(agentName,acceptedMissions,waitingMissions,candidateAmount, completedMissions);
    }
}
