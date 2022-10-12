package client.javafx.plugboard;

import javafx.scene.control.ToggleButton;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlugBoardUI {



    public static int amountOfCharacterSelected;
    // TODO: 9/1/2022 move this to property
    private static LinkedHashMap<ToggleButton,ToggleButton> plugBoardPairs;
    private static ToggleButton CurrentToggleButtonPressedWaitForPair;

    // TODO: 9/1/2022 make it return in order that you made in ui
    public static String getConnections(){
        List<ToggleButton> listToggleButtonKeys =  plugBoardPairs.keySet().stream().collect(Collectors.toList());
        String stringOfConnections = new String();
        Set<ToggleButton> alreadyAdded = new HashSet<>();
        for (int i = 0; i < listToggleButtonKeys.size(); i++) {
            ToggleButton currToggle = listToggleButtonKeys.get(i);
            ToggleButton pairOfPluggedChar = plugBoardPairs.get(currToggle);
            if(!alreadyAdded.contains(currToggle))
            stringOfConnections+=currToggle.textProperty().getValue();
            if(!alreadyAdded.contains(pairOfPluggedChar))
            stringOfConnections+=pairOfPluggedChar.textProperty().getValue();
            alreadyAdded.add(currToggle);
            alreadyAdded.add(pairOfPluggedChar);
            }
        return stringOfConnections;
    }



    public PlugBoardUI(){
        plugBoardPairs = new LinkedHashMap<>();
        amountOfCharacterSelected = 0;
    }
    public static void addToPair(ToggleButton alphabetChar){
        plugBoardPairs.put(CurrentToggleButtonPressedWaitForPair,alphabetChar);
        plugBoardPairs.put(alphabetChar, CurrentToggleButtonPressedWaitForPair);


    }
    public static void removePair(ToggleButton alphabetChar){
        ToggleButton valuePair = plugBoardPairs.get(alphabetChar);
        alphabetChar.setStyle(null);
        alphabetChar.setSelected(false);
        valuePair.setStyle(null);
        valuePair.setSelected(false);
        plugBoardPairs.remove(valuePair);
        plugBoardPairs.remove(alphabetChar);
    }
    public static ToggleButton getCurrentToggleButtonPressedWaitForPair() {
        return CurrentToggleButtonPressedWaitForPair;
    }
    public static boolean havePair(ToggleButton alphabetChar)
    {
        return plugBoardPairs.containsKey(alphabetChar);
    }
    public static void setCurrentToggleButtonPressedWaitForPair(ToggleButton currentToggleButtonPressedWaitForPair) {
        CurrentToggleButtonPressedWaitForPair = currentToggleButtonPressedWaitForPair;
    }
}
