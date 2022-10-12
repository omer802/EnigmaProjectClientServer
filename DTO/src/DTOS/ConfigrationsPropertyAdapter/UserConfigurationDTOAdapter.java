package DTOS.ConfigrationsPropertyAdapter;

import javafx.beans.property.SimpleStringProperty;

public class UserConfigurationDTOAdapter {

    private SimpleStringProperty chosenRotors;
    private SimpleStringProperty NotchAndLetterAtPeekPaneStartingPosition;
    private SimpleStringProperty chosenReflector;
    private SimpleStringProperty plugBoardToShow;
    private SimpleStringProperty fullConfiguration;
    public UserConfigurationDTOAdapter(SimpleStringProperty chosenRotors, SimpleStringProperty NotchAndLetterAtPeekPaneStartingPosition,
    SimpleStringProperty chosenReflector, SimpleStringProperty plugBoardToShow,SimpleStringProperty fullConfiguration){
        this.chosenRotors = chosenRotors;
        this.NotchAndLetterAtPeekPaneStartingPosition = NotchAndLetterAtPeekPaneStartingPosition;
        this.chosenReflector = chosenReflector;
        this.plugBoardToShow = plugBoardToShow;
        this.fullConfiguration = fullConfiguration;
    }
    public void setFullConfiguration(String fullConfiguration) {
        this.fullConfiguration.set(fullConfiguration);
    }

    public void setChosenRotors(String chosenRotors) {
        this.chosenRotors.set(chosenRotors);
    }

    public void setNotchAndLetterAtPeekPaneStartingPosition(String notchAndLetterAtPeekPaneStartingPosition) {
        this.NotchAndLetterAtPeekPaneStartingPosition.set(notchAndLetterAtPeekPaneStartingPosition);
    }

    public void setChosenReflector(String chosenReflector) {
        this.chosenReflector.set(chosenReflector);
    }

    public void setPlugBoardToShow(String plugBoardToShow) {
        this.plugBoardToShow.set(plugBoardToShow);
    }

}
