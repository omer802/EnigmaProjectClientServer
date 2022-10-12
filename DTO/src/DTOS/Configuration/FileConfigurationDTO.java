package DTOS.Configuration;

import engine.enigma.Machine.EnigmaMachine;
import engine.enigma.keyboard.Keyboard;

import java.util.List;

public class FileConfigurationDTO {

    private final int countOfRotors;



    private int countOfRotorsInUse;
    //private List<NotchAndLetterAtPeekPane> NotchAndIds;
    private final int countOfReflectors;

    private int numberOfMessageEncrypted;

    private boolean isConfigFromUser;

    private List<String> possibleRotors;



    private String alphabet;


    private List<String> possibleReflectors;

    public FileConfigurationDTO(final EnigmaMachine machineInput) {
        //setPairOfNotchAndRotorId(machineInput);
        this.countOfRotors = machineInput.getRotorsObject().getRotorsAmount();
        this.countOfRotorsInUse = machineInput.getRotorsAmountInUse();
        this.countOfReflectors = machineInput.getReflectorsObject().getReflectorsAmount();
        this.numberOfMessageEncrypted = machineInput.getTheNumberOfStringsEncrypted();
        this.isConfigFromUser = machineInput.isConfigFromUser();
        this.possibleRotors = machineInput.getPossibleRotors();
        this.possibleReflectors = machineInput.getPossibleReflectors();
        this.alphabet = machineInput.getAlphabet();
    }
    public int getCountOfRotors() {
        return countOfRotors;
    }

    public int getCountOfReflectors() {
        return countOfReflectors;
    }
    public int getCountOfRotorsInUse() {
        return countOfRotorsInUse;
    }

    public int getNumberOfMessageEncrypted() {
        return numberOfMessageEncrypted;
    }
    public boolean isConfigFromUser() {
        return isConfigFromUser;
    }
    public List<String> getPossibleRotors() {
        return possibleRotors;
    }

    public List<String> getPossibleReflectors() {
        return possibleReflectors;
    }
    public String getAlphabet() {
        return alphabet;
    }

}
