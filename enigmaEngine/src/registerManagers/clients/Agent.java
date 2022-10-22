package registerManagers.clients;

import DTOS.agentInformationDTO.AgentInfoDTO;
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
}
