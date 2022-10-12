package client.javafx.UBoatMainController;

import DTOS.ConfigrationsPropertyAdapter.FileConfigurationDTOAdapter;
import DTOS.Configuration.FileConfigurationDTO;
import DTOS.Configuration.UserConfigurationDTO;
import client.javafx.Login.UBoatLoginController;
import client.javafx.contest.ContestController;
import client.javafx.plugboard.PlugBoardUI;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static client.util.Constants.GSON_INSTANCE;

public class MainController {


    private final int MAX_AMOUNT_ERROR_TO_SHOW = 3;
    private SimpleIntegerProperty rotorsAmount;


    private GridPane loginComponent;
    private UBoatLoginController logicController;

    @FXML
    private Button loadFileButton;

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
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField URLFileText;

    @FXML
    private HBox HboxRtorosID;
    @FXML
    private HBox HBoxlistOfRotorsButtons;

    @FXML
    private HBox HBoxListOfPositions;

    @FXML
    private HBox HBoxReflectorChoice;
    @FXML
    private CheckBox CheckBoxIsPluged;
    @FXML
    private FlowPane FlowPaneAlphabet;
    @FXML
    private Label InUseRotorsAmountLabel;

    @FXML
    private Label ReflectorsAmountLabel;

    @FXML
    private Label encryptedMessagesAmountLabel;

    @FXML
    private Label rotorsAmountLabel;
    @FXML
    private AnchorPane CodeAtMachineDetails;

    @FXML
    private Button setCodeButton;

    @FXML
    private Button randomCodeButton;
    @FXML
    private FlowPane machineDetailsFlowPane;
    @FXML
    private ScrollPane Contest;
    @FXML
    private ContestController ContestController;

    private List<ObjectProperty<String>> chosenRotorsList;
    private List<ObjectProperty<Character>> chosenPositions;
    private ObjectProperty<String> ReflectorOptions;
    private String CurrentColor;

    private Stage primaryStage;

    private SimpleBooleanProperty isConfig;

    private SimpleBooleanProperty isFileSelected;
    private String userName;
    private String alphabet;
    UserConfigurationDTO userOriginalConfiguration;
    private SimpleIntegerProperty rotorsInUseAmount;
    private SimpleIntegerProperty reflectorsAmount;
    private SimpleIntegerProperty encryptedMessagesAmount;

    private SimpleStringProperty chosenRotors;
    private SimpleStringProperty NotchAndLetterAtPeekPaneStartingPosition;
    private SimpleStringProperty chosenReflector;
    private SimpleStringProperty plugBoardToShow;
    private SimpleStringProperty errorMessageProperty = new SimpleStringProperty();
    private FileConfigurationDTOAdapter fileConfigurationDTOAdapter;

    public MainController(){
        this.chosenRotorsList = new ArrayList<>();
        this.chosenPositions = new ArrayList<>();
        this.rotorsAmount = new SimpleIntegerProperty(0);
        this.rotorsInUseAmount = new SimpleIntegerProperty(0);
        this.reflectorsAmount = new SimpleIntegerProperty(0);
        this.encryptedMessagesAmount = new SimpleIntegerProperty(0);
        this.isFileSelected = new SimpleBooleanProperty(false);
        this.isConfig = new SimpleBooleanProperty(false);
        this.chosenRotors = new SimpleStringProperty();
        this.NotchAndLetterAtPeekPaneStartingPosition = new SimpleStringProperty();
        this.chosenReflector = new SimpleStringProperty();
        this.plugBoardToShow = new SimpleStringProperty();



    }
    @FXML
    public void initialize() {

        errorMessageLabel.textProperty().bind(errorMessageProperty);

        CheckBoxIsPluged.disableProperty().bind(isFileSelected.not());

        rotorsAmountLabel.textProperty().bind(Bindings.format("%,d", rotorsAmount));
        InUseRotorsAmountLabel.textProperty().bind(Bindings.format("%,d", rotorsInUseAmount));
        ReflectorsAmountLabel.textProperty().bind(Bindings.format("%,d",reflectorsAmount));
        encryptedMessagesAmountLabel.textProperty().bind(Bindings.format("%,d",encryptedMessagesAmount));

        rotorsAmountLabel.visibleProperty().bind(isFileSelected);
        InUseRotorsAmountLabel.visibleProperty().bind(isFileSelected);
        ReflectorsAmountLabel.visibleProperty().bind(isFileSelected);
        encryptedMessagesAmountLabel.visibleProperty().bind(isFileSelected);
        fileConfigurationDTOAdapter = new FileConfigurationDTOAdapter(rotorsAmount,rotorsInUseAmount ,reflectorsAmount,
                encryptedMessagesAmount);

        CodeAtMachineDetails.disableProperty().bind(isConfig.not());
        //encryptDecrypt.disableProperty().bind(isConfig.not());

        setCodeButton.disableProperty().bind(isFileSelected.not());
        randomCodeButton.disableProperty().bind(isFileSelected.not());
        machineDetailsFlowPane.disableProperty().bind(isFileSelected.not());
        CheckBoxIsPluged.disableProperty().bind(isFileSelected.not());


    }

    @FXML
    void loadXmlFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select machine file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null)
            return;

        try {
            sendFileToServerAndLoadMachineConfig(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileToServerAndLoadMachineConfig(File selectedFile) throws IOException {
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart(userName, selectedFile.getName(), RequestBody.create(selectedFile,
                                MediaType.parse("text/plain"))).build();
        Request request = new Request.Builder()
                .url(Constants.UPLOAD_XML_FILE)
                .post(body)
                .build();


        Call call = HttpClientUtil.getHttpClient().newCall(request);

        Response response = call.execute();
        if (response.code() != 200) {
            String responseBody = response.body().string();
            Platform.runLater(() ->
                    errorMessageProperty.set("Something went wrong: " + responseBody));
        }
        else{
            Platform.runLater(() ->{
                    errorMessageProperty.set("File loaded successfully");
                try {
                    String fileConfigurationDTOString = response.body().string();
                    FileConfigurationDTO fileConfigurationDTO = GSON_INSTANCE.fromJson(fileConfigurationDTOString, FileConfigurationDTO.class);
                    fetchStartingConfigurationFromServer(selectedFile.getAbsolutePath(), fileConfigurationDTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        }

    }

    private void fetchStartingConfigurationFromServer(String path,  FileConfigurationDTO fileConfigurationDTO) throws IOException {
        URLFileText.setText(path);
        alphabet = fileConfigurationDTO.getAlphabet();
        initOriginalConfiguration();
        createCodeMenu(fileConfigurationDTO);
        isFileSelected.set(true);
        isConfig.set(false);
        CheckBoxIsPluged.setSelected(false);
        // TODO: 10/11/2022 change this name to beeter name 
        fileConfigurationDTOAdapter.setDataFromFileDTO(fileConfigurationDTO);
        if(ContestController!=null)
        ContestController.setBruteForceComponent();


        //BruteForceController.setBruteForceComponent();

    }
    public void initOriginalConfiguration(){
        chosenReflectorLabel.setText("");
        plugBoardConnectionsLabel.setText("");
        positionsAndNotchLabel.setText("");
        chosenRotorsLabel.setText("");
        fullConfigurationLabel.setText("");
    }
    private void createCodeMenu(FileConfigurationDTO fileConfiguration) {
        makeEmptyLayouts();
        createLabels(fileConfiguration.getCountOfRotorsInUse());
        createRotorsButtons(fileConfiguration);
        createPositionsButtons(fileConfiguration);
        createReflectorChoiceBox(fileConfiguration);
    }
    private void createRotorsButtons(FileConfigurationDTO fileConfiguration) {
        Label labelNumber = new Label();
        labelNumber.setText("Set Rotors");
        labelNumber.setPrefWidth(85);
        labelNumber.setMinWidth(Region.USE_PREF_SIZE);
        HBoxlistOfRotorsButtons.getChildren().add(labelNumber);
        List<String> possibleRotors = fileConfiguration.getPossibleRotors();
        addDynamicRotorsChoiceBoxesToHBox(HBoxlistOfRotorsButtons, fileConfiguration.getCountOfRotorsInUse(), possibleRotors);
    }
    public void addDynamicRotorsChoiceBoxesToHBox(HBox addToHBox, int countOfRotors, List<String> addToList) {
        chosenRotorsList = new ArrayList<>();
        for (int i = 1; i <= countOfRotors; i++) {
            ChoiceBox<String> choiceBox = new ChoiceBox();
            choiceBox.setPrefHeight(25);
            choiceBox.setPrefWidth(61);
            choiceBox.setMinWidth(Region.USE_PREF_SIZE);
            choiceBox.getItems().addAll(addToList);
            choiceBox.getSelectionModel().select(i);
            ObjectProperty<String> chosenRotorProperty = choiceBox.valueProperty();
            chosenRotorsList.add(chosenRotorProperty);
            addToHBox.getChildren().add(choiceBox);
        }
    }


    private void createLabels(int countOfRotors) {
        Label firstLabelRotor = new Label();
        firstLabelRotor.setText("Rotor " + countOfRotors);
        firstLabelRotor.setPadding(new Insets(0, 0, 0, 105));
        firstLabelRotor.setMinWidth(Region.USE_PREF_SIZE);
        HboxRtorosID.getChildren().add(firstLabelRotor);
        for (int i = countOfRotors - 1; i > 0; i--) {
            Label rotorID = new Label();
            rotorID.setText("Rotor " + i);
            rotorID.setMinWidth(Region.USE_PREF_SIZE);
            rotorID.setPrefWidth(46);
            HboxRtorosID.getChildren().add(rotorID);
        }
    }
    public void createPositionsButtons(FileConfigurationDTO fileConfiguration) {
        Label labelRings = new Label();
        labelRings.setText("Set Position");
        labelRings.setPrefWidth(85);
        labelRings.setPadding(new Insets(0, 6, 0, 0));
        labelRings.setMinWidth(Region.USE_PREF_SIZE);
        HBoxListOfPositions.getChildren().add(labelRings);
        List<Character> alphabetChar = adapterStringToCharList(alphabet);
        addDynamicPositionsChoiceBoxesToHBox(HBoxListOfPositions, fileConfiguration.getCountOfRotorsInUse(), alphabetChar);
    }
    public void addDynamicPositionsChoiceBoxesToHBox(HBox addToHBox, int countOfRotors, List<Character> addToList) {
        chosenPositions = new ArrayList<>();
        for (int i = 1; i <= countOfRotors; i++) {
            ChoiceBox choiceBox = getChoiceBox(addToList);
            // TODO: 9/1/2022 chenge to choicebox
            ObjectProperty<Character> chosenRotorProperty = choiceBox.valueProperty();
            chosenPositions.add(chosenRotorProperty);
            addToHBox.getChildren().add(choiceBox);
        }
    }
    public ChoiceBox<Character> getChoiceBox(List<Character> addToList) {
        ChoiceBox<Character> choiceBox = new ChoiceBox();
        choiceBox.setPrefHeight(25);
        choiceBox.setPrefWidth(61);
        choiceBox.setMinWidth(Region.USE_PREF_SIZE);
        choiceBox.getItems().addAll(addToList);
        choiceBox.getSelectionModel().selectFirst();
        return choiceBox;
    }
    public static List<Character> adapterStringToCharList(String str) {
        List<Character> chars = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            chars.add(str.charAt(i));
        }
        return chars;
    }
    public void createReflectorChoiceBox(FileConfigurationDTO fileConfiguration) {
        Label ReflectorLabel = new Label("Set Reflector");
        HBoxReflectorChoice.getChildren().add(ReflectorLabel);
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setPrefHeight(25);
        choiceBox.setPrefWidth(74);
        choiceBox.getItems().addAll(fileConfiguration.getPossibleReflectors());
        choiceBox.setValue(fileConfiguration.getPossibleReflectors().get(0));
        this.ReflectorOptions = choiceBox.valueProperty();
        HBoxReflectorChoice.getChildren().add(choiceBox);
    }

    public void makeEmptyLayouts(){
        this.HboxRtorosID.getChildren().clear();
        this.HBoxlistOfRotorsButtons.getChildren().clear();
        this.HBoxListOfPositions.getChildren().clear();
        this.HBoxReflectorChoice.getChildren().clear();
        this.FlowPaneAlphabet.getChildren().clear();
        //codeConfigurationController.makeEmptyLayout();
    }
    @FXML
    void setCodeListener(ActionEvent event) {

        UserConfigurationDTO UserConfiguration = getUserConfigurationDTO();
        if(UserConfiguration == null)
            return;
        if(CheckBoxIsPluged.isSelected()) {// TODO: 9/1/2022 make it a property list
            //check if plugBoard initial succeed
            if(!setPlugBoard(UserConfiguration))
                return;
        }
        //api.selectInitialCodeConfiguration(Specification);
        sendCodeConfigurationToServer(UserConfiguration);


        //api.DecipherMessage("ICJ AOZKR", DM.DifficultyLevel.IMPOSSIBLE,10, uiAdapter);

        // TODO: 9/5/2022  think how to bind statitsics to encrypted decrypted

    }

    private void sendCodeConfigurationToServer(UserConfigurationDTO userConfiguration) {
        String jsonCodeConfigurationDTO = GSON_INSTANCE.toJson(userConfiguration);
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("jsonUserConfigurationDTO", jsonCodeConfigurationDTO)
                        .build();
        Request finalUrl = new Request.Builder()
                .url(Constants.SEND_CODE_CONFIGURATION)
                .post(body)
                .build();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        alertShowException(new RuntimeException("Something went wrong:\n " + e.getMessage()))
                );
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handleCodeConfigurationResponse(response);
            }
        });
        }


    public void updateConfigurationLabels(UserConfigurationDTO userConfiguration){
        if(userConfiguration == null){
            System.out.println(" the configuration is null !");
        }
        setOriginalConfiguration(userConfiguration);
        //setCurrentConfiguration();todo add this also after implement servlet
        isConfig.set(true);

    }
    public void setOriginalConfiguration(UserConfigurationDTO originalConfigurationDTO){
        String codeConfigurationWithSeprator = originalConfigurationDTO.getCodeConfigurationString().toString();
        String[] build = codeConfigurationWithSeprator.replace(">","")
                .split("<");
        //System.out.println(build);
        chosenRotorsLabel.setText(build[1]);

        positionsAndNotchLabel.setText(build[2]);

        chosenReflectorLabel.setText(build[3]);
        if(originalConfigurationDTO.isPlugged()){
            plugBoardConnectionsLabel.setText(originalConfigurationDTO.getPlugBoardConnectionsWithFormat());
        }
        else
            plugBoardConnectionsLabel.setText("");

        fullConfigurationLabel.setText(codeConfigurationWithSeprator.toString());
    }
    public boolean isIdenticalRotors(List<String> chosenRotorsList){
        return chosenRotorsList.stream().distinct().count() != chosenRotorsList.size();

    }
    public UserConfigurationDTO getUserConfigurationDTO(){
        List<String> chosenRotors = chosenRotorsList.stream().map(p -> p.getValue()).collect(Collectors.toList());
        Collections.reverse(chosenRotors);
        if (isIdenticalRotors(chosenRotors)) {
            alertShowException(new RuntimeException("Error: You have chose more than one identical rotor"));
            //System.out.println("error accoured");
            return null;
        }
        String chosenPositions = getChosenPosition();
        String chosenReflector = this.ReflectorOptions.getValue();
       return new UserConfigurationDTO(chosenRotors, chosenPositions, chosenReflector);
    }
    public void alertShowException(Exception e){
        List<Exception> exceptionList = new ArrayList<>();
        exceptionList.add(e);
        showListOfExceptions(exceptionList);
    }

    private void showListOfExceptions(List<Exception> exceptionList) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        String errorMessage = "";
        int amountOfMessageShowed = 0;
        for (Exception message : exceptionList) {
            errorMessage = errorMessage + message.getMessage() + "\n";
            amountOfMessageShowed++;
            if (amountOfMessageShowed > MAX_AMOUNT_ERROR_TO_SHOW)
                break;
        }
        alert.setContentText(errorMessage + "\n");
        alert.setTitle("Error!");
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
    public String getUserName() {
        return userName;
    }
    public String getChosenPosition() {
        String positionsToReturn = chosenPositions.stream().
                map(p -> p.getValue().
                        toString()).reduce("", String::concat);
        //System.out.println(positionsToReturn);
        return positionsToReturn;
    }

    @FXML
    void addOrRemovePlugBoard(ActionEvent event) {
        if (CheckBoxIsPluged.isSelected()) {
            PlugBoardUI plugBoard = new PlugBoardUI();
            for (char ch : alphabet.toCharArray()) {
                ToggleButton alphabetChar = new ToggleButton();
                alphabetChar.setText(String.valueOf(ch));
                alphabetChar.setMnemonicParsing(false);
                alphabetChar.setOnAction(e -> {
                    if(alphabetChar.isSelected())
                        setColors(alphabetChar);
                    else
                        removeColorPairs(alphabetChar);
                });

                FlowPaneAlphabet.getChildren().add(alphabetChar);
            }

        } else {
            FlowPaneAlphabet.getChildren().clear();
            PlugBoardUI.amountOfCharacterSelected = 0;

        }

    }
    public void removeColorPairs(ToggleButton alphabetChar) {
        if (!PlugBoardUI.havePair(alphabetChar)) {
            alphabetChar.setStyle(null);
            PlugBoardUI.amountOfCharacterSelected--;
        } else {
            PlugBoardUI.removePair(alphabetChar);
            PlugBoardUI.amountOfCharacterSelected -= 2;
        }
    }
    public void setColors(ToggleButton alphabetChar) {
        PlugBoardUI.amountOfCharacterSelected++;
        if (PlugBoardUI.amountOfCharacterSelected % 2 != 0) {
            this.CurrentColor = ChangeColor();
            alphabetChar.setStyle("-fx-background-color: " + CurrentColor);
            PlugBoardUI.setCurrentToggleButtonPressedWaitForPair(alphabetChar);
        } else {
            alphabetChar.setStyle("-fx-background-color:" + CurrentColor);
            PlugBoardUI.addToPair(alphabetChar);
        }
    }
    public String ChangeColor(){
        Color color =  Color.rgb(generateRandomIntForRgb(),
                generateRandomIntForRgb(),generateRandomIntForRgb());

        return color.toString().replace("0x", "#");

    }
    public int generateRandomIntForRgb(){
        return (int) (Math.random()*254+1);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;


    }
    public boolean setPlugBoard(UserConfigurationDTO Specification){
        String connections = PlugBoardUI.getConnections();
        if (PlugBoardUI.amountOfCharacterSelected % 2 != 0) {
            alertShowException(new RuntimeException("One of the plugs doesn't have a pair"));
            //System.out.println("One of the plugs doesn't have a pair\n");
            return false;
        }
        else{
            Specification.setPlugBoardConnections(connections);
            //System.out.println(connections);
            return true;
        }
    }
    @FXML
    void generateRandomCode(ActionEvent event) {
        String finalUrl = HttpUrl
                .parse(Constants.GENERATE_RANDOM_CODE_CONFIGURATION)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        alertShowException(new RuntimeException("Something went wrong:\n " + e.getMessage()))
                );
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                handleCodeConfigurationResponse(response);

            }
        });
    }
    private void handleCodeConfigurationResponse(Response response) throws IOException {
        if (response.code() != 200) {
            String responseBody = response.body().string();
            Platform.runLater(() ->
                    alertShowException(new RuntimeException("Something went wrong:\n " + responseBody))
            );
        } else {
            Platform.runLater(() -> {
                try {
                    String userConfigurationString = response.body().string();
                    UserConfigurationDTO userConfiguration = GSON_INSTANCE.fromJson(userConfigurationString, UserConfigurationDTO.class);
                    userOriginalConfiguration = userConfiguration;
                    updateConfigurationLabels(userConfiguration);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public SimpleBooleanProperty isConfigProperty() {
        return isConfig;
    }


}