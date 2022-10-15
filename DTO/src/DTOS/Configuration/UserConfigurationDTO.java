package DTOS.Configuration;

import engine.enigma.Machine.EnigmaMachine;
import engine.enigma.Machine.NotchAndLetterAtPeekPane;
import engine.enigma.PlugBoard.PlugBoard;
import engine.enigma.reflector.Reflectors;
import engine.enigma.rotors.RotatingRotor;

import java.io.Serializable;
import java.util.List;

public class UserConfigurationDTO implements Serializable {
    private int numberOfMessageEncrypted;
    private List<String> chosenRotorsWithOrder;
    private String RotorsStartingPosition;
    private boolean isPlugged;

    private PlugBoard plugBoardConnections;
    static boolean haveConfigFromUser = false;
    private Reflectors.ReflectorEnum chosenReflector;

    private List<NotchAndLetterAtPeekPane> NotchAndLetterPair;


    private String encryptedMessage;

    public void removePlug(){
        isPlugged = false;
    }

    //constructor for transferring data from ui to engine
    public UserConfigurationDTO(List<String> chosenRotors, String rotorsStartingPosition, String chosenReflector){
        this.chosenRotorsWithOrder = chosenRotors;
        this.RotorsStartingPosition = rotorsStartingPosition;
        this.chosenReflector = Reflectors.ReflectorEnum.valueOf(chosenReflector);
        this.haveConfigFromUser = true;
    }

    //constructor for answer of the engine to ui
    public UserConfigurationDTO(final EnigmaMachine machineInput){
        setPairOfNotchAndRotorId(machineInput.getPairOfNotchAndRotorId());
        this.numberOfMessageEncrypted = machineInput.getTheNumberOfStringsEncrypted();
        setChosenRotorsWithOrder(machineInput);
        setRotorsStartingPosition(machineInput);
        this.isPlugged = machineInput.isPluged();
        this.chosenReflector = machineInput.getReflectorsObject().getChosenReflectorByRomeNumerals();
        if(isPlugged)
        this.plugBoardConnections = machineInput.getPlugBoard();
        this.haveConfigFromUser = true;
    }

    private void setChosenRotorsWithOrder(final EnigmaMachine machineInput){
        this.chosenRotorsWithOrder = machineInput.getRotorsObject().getChosenRotorsString();

    }
    public void setChosenRotorsWithOrder(List<String> chosenRotors){
        this.chosenRotorsWithOrder = chosenRotors;
    }

    public int getNumberOfMessageEncrypted() {
        return numberOfMessageEncrypted;
    }
    public List<String> getChosenRotorsWithOrder() {
        return chosenRotorsWithOrder;
    }
    public String getChosenRotorsWithOrderWithSeprator(){
        //chosenRotorsWithOrder.
        return chosenRotorsWithOrder.stream().map(r->r.toString()).reduce("", String::concat);
    }
    private void setRotorsStartingPosition(final EnigmaMachine machineInput){
        List<RotatingRotor> rotors = machineInput.getRotorsObject().getChosenRotors();
        String rotorsStartingPositions = new String();
        // To display the rotors in a way that matches the way the rotors appear we need to reverse the order of the starting positions
        for (int i = rotors.size() -1; i >=0 ; i--) {
            char position = rotors.get(i).getCurrentPositionCharacter();
            rotorsStartingPositions = rotorsStartingPositions + position;
        }
        this.RotorsStartingPosition = rotorsStartingPositions;
    }
    public String getRotorsStartingPosition() {
        return RotorsStartingPosition;
    }
    public boolean isPlugged() {
        return isPlugged;
    }

    public Reflectors.ReflectorEnum getChosenReflector() {
        return chosenReflector;
    }
    public PlugBoard getPlugBoard() {
        return plugBoardConnections;
    }
    public static boolean isHaveConfigFromUser() {
        return haveConfigFromUser;
    }

    public void setPlugBoardConnections(String plugBoardConnections) {
        this.plugBoardConnections = new PlugBoard(plugBoardConnections);
        this.isPlugged = true;
    }
    public String getPlugBoardConnectionsWithFormat(){
        return plugBoardConnections.toString();
    }
    public void setPairOfNotchAndRotorId(List<NotchAndLetterAtPeekPane> pairs){
        this.NotchAndLetterPair = pairs;
    }
    public List<NotchAndLetterAtPeekPane> getNotchAndLetterPair() {
        return NotchAndLetterPair;
    }

    public StringBuilder getCodeConfigurationString(){
        StringBuilder stringBuilder = new StringBuilder();
        List<String> rotorsWithOrder = getChosenRotorsWithOrder();
        createCodeConfigurationGetChosenRotors(rotorsWithOrder, stringBuilder);

        List<NotchAndLetterAtPeekPane> currentPositionsAndNotch = getNotchAndLetterPair();
        getCurrentPositionsWithDistanceFromNotch(currentPositionsAndNotch, stringBuilder);

        stringBuilder.append("<"+getChosenReflector()+">");
        if(isPlugged()&&getPlugBoardConnectionsWithFormat().length()>0)
            stringBuilder.append("<" + getPlugBoardConnectionsWithFormat()+ ">");
        return stringBuilder;
    }

    private void getCurrentPositionsWithDistanceFromNotch(List<NotchAndLetterAtPeekPane> currentPositionsAndNotch, StringBuilder stringBuilderInput) {
        boolean isFirst = true;
        stringBuilderInput.append("<");
        for (NotchAndLetterAtPeekPane pair: currentPositionsAndNotch)
            stringBuilderInput.append(pair.toString());
        stringBuilderInput.append( ">");
    }

    private void createCodeConfigurationGetChosenRotors(List<String> rotorsWithOrder, StringBuilder stringBuilderInput) {
        boolean isFirst = true;
        stringBuilderInput.append("<");
        for (String rotorID: rotorsWithOrder) {
            if(!isFirst)
                stringBuilderInput.append(",");
            stringBuilderInput.append(rotorID);
            isFirst = false;
        }
        stringBuilderInput.append(">");
    }
    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

}
