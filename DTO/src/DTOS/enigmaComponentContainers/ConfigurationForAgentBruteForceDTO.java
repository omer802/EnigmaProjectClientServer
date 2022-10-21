package DTOS.enigmaComponentContainers;

import dictionary.Dictionary;
import engine.enigma.Machine.EnigmaMachine;

public class ConfigurationForAgentBruteForceDTO {
    private EnigmaMachine machine;
    private Dictionary dictionary;

    public int getMissionSize() {
        return missionSize;
    }

    private int missionSize;


    public ConfigurationForAgentBruteForceDTO(EnigmaMachine machine, Dictionary dictionary) {
        this.machine = machine;
        this.dictionary = dictionary;
    }
    public EnigmaMachine getMachine() {
        return machine;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }
}
