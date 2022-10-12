package client.javafx.UBoatMainController;

import DTOS.Configuration.UserConfigurationDTO;

import java.util.List;

public class PrintableCodeConfiguration {

   /* public static StringBuilder getCodeConfigurationString(UserConfigurationDTO userConfigurationDTO){
        StringBuilder stringBuilder = new StringBuilder();
        List<String> rotorsWithOrder = getChosenRotorsWithOrder(userConfigurationDTO);
        createCodeConfigurationGetChosenRotors(rotorsWithOrder, stringBuilder);

        List<NotchAndLetterAtPeekPane> currentPositionsAndNotch = getNotchAndLetterPair();
        getCurrentPositionsWithDistanceFromNotch(currentPositionsAndNotch, stringBuilder);

        stringBuilder.append("<"+getChosenReflector()+">");
        if(isPlugged()&&getPlugBoardConnectionsWithFormat().length()>0)
            stringBuilder.append("<" + getPlugBoardConnectionsWithFormat()+ ">");
        return stringBuilder;
    }
    public static List<String> getChosenRotorsWithOrder(UserConfigurationDTO userConfigurationDTO) {
        return userConfigurationDTO.getChosenRotorsWithOrder();
    }
    private static void createCodeConfigurationGetChosenRotors(List<String> rotorsWithOrder, StringBuilder stringBuilderInput) {
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

    public static void getCurrentPositionsWithDistanceFromNotch(){

    }*/
}
