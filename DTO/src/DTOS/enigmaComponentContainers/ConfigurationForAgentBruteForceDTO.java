package DTOS.enigmaComponentContainers;

import dictionary.Dictionary;
import engine.enigma.Machine.EnigmaMachine;
import keyboard.Keyboard;

public class ConfigurationForAgentBruteForceDTO {
    private EnigmaMachine machine;
    private Dictionary dictionary;

    public String getAlphabet() {
        return alphabet;
    }

    private String alphabet;

    public int getMissionSize() {
        return missionSize;
    }

    private int missionSize;


    public ConfigurationForAgentBruteForceDTO(EnigmaMachine machine, Dictionary dictionary) {
        this.machine = machine;
        this.dictionary = dictionary;
        this.alphabet = Keyboard.getAlphabet();
    }
    public EnigmaMachine getMachine() {
        return machine;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }
}
