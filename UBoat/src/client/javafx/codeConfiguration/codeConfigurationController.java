package client.javafx.codeConfiguration;
import DTOS.ConfigrationsPropertyAdapter.UserConfigurationDTOAdapter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class codeConfigurationController {
    @FXML
    private Label chosenReflectorLabel;

    @FXML
    private Label plugBoardConnectionsLabel;

    @FXML
    private Label positionsAndNotchLabel;

    @FXML
    private Label chosenRotorsLabel;

    @FXML
    private Label fullConfigurationLabel;

    public boolean isIsConfig() {
        return isConfig.get();
    }

    public SimpleBooleanProperty isConfigProperty() {
        return isConfig;
    }

    private SimpleBooleanProperty isConfig;

    private SimpleStringProperty fullConfiguration;
    private SimpleStringProperty chosenRotors;

    public SimpleStringProperty fullConfigurationProperty() {
        return fullConfiguration;
    }
    public SimpleStringProperty chosenRotorsProperty() {
        return chosenRotors;
    }

    public SimpleStringProperty notchAndLetterAtPeekPaneStartingPositionProperty() {
        return NotchAndLetterAtPeekPaneStartingPosition;
    }

    public SimpleStringProperty chosenReflectorProperty() {
        return chosenReflector;
    }

    public SimpleStringProperty plugBoardToShowProperty() {
        return plugBoardToShow;
    }

    private SimpleStringProperty NotchAndLetterAtPeekPaneStartingPosition;
    private SimpleStringProperty chosenReflector;
    private SimpleStringProperty plugBoardToShow;


    public codeConfigurationController() {
        chosenRotors = new SimpleStringProperty("");
        NotchAndLetterAtPeekPaneStartingPosition = new SimpleStringProperty("");
        chosenReflector = new SimpleStringProperty("");
        plugBoardToShow = new SimpleStringProperty("");
        fullConfiguration = new SimpleStringProperty("");
        isConfig =new SimpleBooleanProperty();
    }

    @FXML
    private void initialize(){
        chosenRotorsLabel.textProperty().bind(Bindings.format("%s", chosenRotors));
        positionsAndNotchLabel.textProperty().bind(Bindings.format("%s", NotchAndLetterAtPeekPaneStartingPosition));
        chosenReflectorLabel.textProperty().bind(Bindings.format("%s",chosenReflector));
        plugBoardConnectionsLabel.textProperty().bind(Bindings.format("%s",plugBoardToShow));
        fullConfigurationLabel.textProperty().bind(Bindings.format("%s",fullConfiguration));

    }

    // TODO: 9/3/2022 change to property action on file selected
    public void makeEmptyLayout() {
        chosenRotors.set("");
        NotchAndLetterAtPeekPaneStartingPosition.set("");
        chosenReflector.set("");
        plugBoardToShow.set("");
        fullConfiguration.set("");
    }
    public UserConfigurationDTOAdapter getConfigurationProperties(){
        return new UserConfigurationDTOAdapter(chosenRotors, NotchAndLetterAtPeekPaneStartingPosition,
                chosenReflector, plugBoardToShow, fullConfiguration);
    }
    public void setConfig(boolean config) {

    }
    public boolean isConfig() {
        return isConfig.getValue();
    }


    // TODO: 9/5/2022 change to more algant way

    public void BindCodeConfiguration(codeConfigurationController controller){
        this.chosenRotors.bind(controller.chosenRotorsProperty());
        this.NotchAndLetterAtPeekPaneStartingPosition.bind(controller.notchAndLetterAtPeekPaneStartingPositionProperty());
        this.chosenReflector.bind(controller.chosenReflectorProperty());
        this.plugBoardToShow.bind(controller.plugBoardToShowProperty());
        this.fullConfiguration.bind(controller.fullConfigurationProperty());
    }
}



