package miniEngine;

import DTOS.agentInformationDTO.AgentInfoDTO;
import dictionary.Dictionary;
import engine.enigma.Machine.EnigmaMachine;

public class AgentDecipherManager {
    private EnigmaMachine enigmaMachine;
    private Dictionary dictionary;
    private int missionSize;
    private AgentInfoDTO agentInfoDTO;

    public AgentDecipherManager(EnigmaMachine enigmaMachine, Dictionary dictionary, int missionSize, AgentInfoDTO agentInfoDTO) {
        this.enigmaMachine = enigmaMachine;
        this.dictionary = dictionary;
        this.missionSize = missionSize;
        this.agentInfoDTO = agentInfoDTO;
    }
}
