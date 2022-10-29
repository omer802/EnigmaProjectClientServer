package client.javafx.contest;

import DTOS.Configuration.FileConfigurationDTO;
import DTOS.Configuration.UserConfigurationDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import client.javafx.Candidate.candidateController;
import DTOS.AllieInformationDTO.AlliesDetailDTO;
import client.javafx.activeTeamsDetails.activeTeamsDetailsController;
import dictionary.Dictionary;
import dictionary.Trie;
import client.javafx.UBoatMainController.MainController;
import client.constants.ConstantsUBoat;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import keyboard.Keyboard;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import registerManagers.clients.UBoat;
import util.CommonConstants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

import static util.CommonConstants.GSON_INSTANCE;
import static util.CommonConstants.REFRESH_RATE;

public class ContestController {

    private  SimpleStringProperty encryptionTextFiled;
    @FXML
    private BorderPane bruteForceBorderPane;

    @FXML
    private ScrollPane scrollPaneEncryptDecrypt;
    @FXML
    private TableView<CandidateDTO> candidate;
    @FXML
    private candidateController candidateController;


    @FXML
    private TableView<AlliesDetailDTO> activeTeamDetails;
    @FXML
    private activeTeamsDetailsController activeTeamDetailsController;

    @FXML
    private Label codeConfigurationLabel;

    @FXML
    private Button resetCodeButton;

    @FXML
    private TextField encryptedMessage;

    @FXML
    private Button ProcessButton;

    @FXML
    private Button clearButton;

    @FXML
    private Label EncryptDecryptResultLabel;

    @FXML
    private TextField SearchField;

    @FXML
    private ListView<String> DictionaryListViewField;

    @FXML
    private Button readyButton;
    @FXML
    private Label labelIndication;
    private SimpleStringProperty contestStatus;

    @FXML
    private BorderPane encryptionBorderPane;
    @FXML
    private AnchorPane contestButtonsAnchorPane;
    @FXML
    private Button logoutButton;

    private MainController mainController;
    public  SimpleBooleanProperty isConfig;
    private SimpleStringProperty encryptionResultProperty;
    private SimpleStringProperty codeConfiguration;
    private String encryptionResult;
    private Dictionary dictionary;
    private Keyboard keyboard;

    private Consumer<Exception> httpRequestErrorLoggerConsumer;

    private SimpleBooleanProperty inActiveContest;
    private SimpleBooleanProperty isReady;
    ContestStatusRefresher contestStatusRefresher;
    private Timer contestStatusRefresherTimer;
    private UBoat.GameStatus gameStatus;

    @FXML
    private void initialize() {
        this.inActiveContest = new SimpleBooleanProperty(false);
        this.isReady = new SimpleBooleanProperty(false);
        bruteForceBorderPane.disableProperty().bind(isConfig.not());
        readyButton.disableProperty().bind(isReady);
        ProcessButton.disableProperty().bind(isReady);
        logoutButton.visibleProperty().bind(inActiveContest);
        //encryptionBorderPane.disableProperty().bind(isConfig.not());
        //candidate.disableProperty().bind(isConfig.not());
        EncryptDecryptResultLabel.textProperty().bind(Bindings.format("%s", encryptionResultProperty));
        codeConfigurationLabel.textProperty().bind(Bindings.format("%s", codeConfiguration));
        labelIndication.textProperty().bind(Bindings.format("%s",contestStatus));
        gameStatus = UBoat.GameStatus.OFF;

    }

    public ContestController(){
        this.encryptionResultProperty = new SimpleStringProperty("");
        this.codeConfiguration = new SimpleStringProperty("");
        this.encryptionTextFiled = new SimpleStringProperty("");
        this.contestStatus = new SimpleStringProperty("To start contest press ready");
        this.isConfig = new SimpleBooleanProperty();
    }

    public void setMainPageController(MainController mainController) {
        this.mainController = mainController;
        this.isConfig.bind(mainController.isConfigProperty());
    }



    @FXML
    void AddWordToEncryptTextField(MouseEvent event) {
        if (event.getClickCount() == 2) {
            //Use ListView's getSelected Item
            String currentItemSelected = DictionaryListViewField.getSelectionModel()
                    .getSelectedItem();
            encryptedMessage.appendText(currentItemSelected+" ");
        }
    }

    @FXML
    void EncryptFullMessage(ActionEvent event) {
        String toEncrypt = encryptedMessage.getText();
        String cleanedToEncrypt = dictionary.cleanStringFromExcludeChars(toEncrypt);
        if(dictionary.isDictionaryContainString(cleanedToEncrypt)) {
            String encryptionResult = encryptFullMessageNoneAction(cleanedToEncrypt);

        }
        else{
            mainController.alertShowException(new RuntimeException("The word you entered is not in the dictionary"));
        }
    }


    private String encryptFullMessageNoneAction(String toEncrypt) {
        toEncrypt = toEncrypt.toUpperCase();
        if (!keyboard.isStringInRange(toEncrypt)) {
            mainController.alertShowException(new RuntimeException("Some of the letters you entered are not from the alphabet"));
            return null;
        }
        else {
            if (toEncrypt.length() > 0) {
               sendStringToEncryptToServer(toEncrypt);

            }
        }
        return null;
    }

    private void sendStringToEncryptToServer(String toEncrypt) {
        String finalUrl = HttpUrl
                .parse(ConstantsUBoat.ENCRYPT_MESSAGE)
                .newBuilder()
                .build()
                .toString();

        RequestBody body = new FormBody.Builder()
                .add("messageToEncrypt", toEncrypt)
                .build();
        HttpClientUtil.runAsyncPost(finalUrl,body, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    mainController.alertShowException(e);
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    handleErrorFromServer(response);
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonConfigurationDTOString = response.body().string();
                            //System.out.println(jsonConfigurationDTOString);
                            UserConfigurationDTO configurationDTO = GSON_INSTANCE.fromJson(jsonConfigurationDTOString, UserConfigurationDTO.class);
                            updateCurrentConfiguration(configurationDTO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

        });
    }

    public void setBruteForceComponent(FileConfigurationDTO fileConfigurationDTO){
        String alphabet = fileConfigurationDTO.getAlphabet();
        this.keyboard = new Keyboard(alphabet);
        initProcessComponent();
        fetchDictionary();
    }
    private void initProcessComponent() {
        encryptionResultProperty.set("");
        encryptionResultProperty.set("");
    }
    public void fetchDictionary(){
        String finalUrl = HttpUrl
                .parse(ConstantsUBoat.FETCH_DICTIONARY)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    mainController.alertShowException(e);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    handleErrorFromServer(response);
                } else {
                    Platform.runLater(() -> {
                        try {
                            String dictionaryJsonString = response.body().string();
                            dictionary = GSON_INSTANCE.fromJson(dictionaryJsonString, Dictionary.class);
                           // System.out.println(" fill dictionary");
                            fillDictionary();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });

    }

    public void fillDictionary(){
        SearchField.setText("");
        DictionaryListViewField.getItems().clear();
        List<String> allWordsInTrie = dictionary.getTrie().suggest("");
        for (String word: allWordsInTrie) {
            DictionaryListViewField.getItems().add(word);
        }
    }
    @FXML
    void clearTextAndProcessNewMessage(ActionEvent event) {
        encryptedMessage.setText("");
        encryptionResultProperty.set("");
    }
    @FXML
    void getWordsByPrefix(KeyEvent event) {
        String prefix = SearchField.getText();
        prefix = prefix.toUpperCase();
        Trie trie = dictionary.getTrie();
        List<String> suggestWords = trie.suggest(prefix);
        DictionaryListViewField.getItems().clear();
        for (String word: suggestWords) {
            DictionaryListViewField.getItems().add(word);
        }
    }
    @FXML
    private void resetCodeButton(ActionEvent event) {
        String finalUrl = HttpUrl
                .parse(ConstantsUBoat.RESET_CODE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    mainController.alertShowException(e);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    handleErrorFromServer(response);
                } else {
                    Platform.runLater(() -> {
                        try {
                            String jsonConfigurationDTOString = response.body().string();
                            UserConfigurationDTO configurationDTO = GSON_INSTANCE.fromJson(jsonConfigurationDTOString, UserConfigurationDTO.class);
                            mainController.updateConfigurationLabels(configurationDTO);
                            updateCurrentConfiguration(configurationDTO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

        });

    }
    private void handleErrorFromServer(@NotNull Response response) throws IOException {
        String responseBody = response.body().string();
        Platform.runLater(() ->
                mainController.alertShowException(new RuntimeException(responseBody)));
    }
    public void updateCurrentConfiguration(UserConfigurationDTO configurationDTO){
        if(configurationDTO.getNumberOfMessageEncrypted()>0)
        {
            if (configurationDTO.getEncryptedMessage() != null) {
                encryptionResult = configurationDTO.getEncryptedMessage();
                encryptionResultProperty.set(encryptionResult);
               // CandidateDTO candidate1 = new CandidateDTO(encryptionResult,"1",configurationDTO.getCodeConfigurationString().toString());
               // candidateController.addCandidate(candidate1);
               // AlliesDetailDTO teamDetail = new AlliesDetailDTO("omer team", 5,10);
               // activeTeamDetailsController.addTeamToTable(teamDetail);
            }
            else{
                encryptionResultProperty.set("");
            }
        }
        String codeConfigurationString= configurationDTO.getCodeConfigurationString().toString();
        codeConfiguration.set(codeConfigurationString);
    }
    @FXML
    void readyForContestAction(ActionEvent event){
        String finalUrl = HttpUrl
                .parse(CommonConstants.MAKE_CLIENT_READY)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    mainController.alertShowException(e);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    handleErrorFromServer(response);
                } else {
                    Platform.runLater(() -> {
                        isReady.set(true);
                        startContestStatusRefresher();
                    });
                    }
                }

        });
    }

    private void startContestStatusRefresher() {
        contestStatusRefresher = new ContestStatusRefresher(isReady, httpRequestErrorLoggerConsumer,this::updateCurrentState);
        contestStatusRefresherTimer = new Timer();
        contestStatusRefresherTimer.schedule(contestStatusRefresher,REFRESH_RATE,REFRESH_RATE);
        // check for starting contest and if contest get the candidates
        candidateController.startCandidateRefresher();
    }
    public void updateCurrentState(UBoat.GameStatus gameStatus){
        this.gameStatus = gameStatus;
        switch (gameStatus){
            case WAITING_AND_READY:
                isReady.set(true);
                contestStatus.set("Ready, Waiting for allies...");
                break;
            case ACTIVE_GAME:
                inActiveContest.set(true);
                contestStatus.set("In active game...");
                break;
            case FINISH_CONTEST_WAITING:// TODO: 10/28/2022 change to active in server 
                /// fetching winner
                isReady.set(false);
                inActiveContest.set(false);
                contestStatus.set("finished...");
                getWinnerFromSever();
                contestStatus.set("To start contest press ready");
        }
    }
    private void getWinnerFromSever() {
        HttpClientUtil.runAsync(CommonConstants.GET_WINNER, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestErrorLoggerConsumer.accept(new RuntimeException("faild call :Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String winnerResponse = response.body().string();
                System.out.println(winnerResponse);
                if(winnerResponse==null){
                    return;
                }
                if (!response.isSuccessful()) {
                    httpRequestErrorLoggerConsumer.accept(new RuntimeException("Something went wrong:" + winnerResponse));
                } else {
                    Platform.runLater(() -> {
                        CandidateDTO candidatesDTO = GSON_INSTANCE.fromJson(winnerResponse, CandidateDTO.class);
                        StringBuilder sb = new StringBuilder();

                        Alert winnerAlert = new Alert(Alert.AlertType.INFORMATION);
                        winnerAlert.setTitle("UBoat Message: We Have A Winner!");
                        sb.append("The Winner is: "+ candidatesDTO.getHowFind()+"\n");
                        sb.append("Found in the following code: "+ candidatesDTO.getCode());
                        winnerAlert.setHeaderText(sb.toString());
                        winnerAlert.showAndWait();
                        cleanContestData();

                    });

                }
            }
        });
    }




    public void startListRefresher() {
        gameStatus = UBoat.GameStatus.WAITING;
        activeTeamDetailsController.startListRefresher();

    }

    public void setErrorHandlerMainController(Consumer<Exception> httpRequestLoggerConsumer) {
        this.httpRequestErrorLoggerConsumer = httpRequestLoggerConsumer;
        activeTeamDetailsController.setErrorHandlerMainController(httpRequestLoggerConsumer);
        candidateController.setErrorHandlerMainController(httpRequestLoggerConsumer,this.inActiveContest);

    }

    public void setActiveContestProperty(SimpleBooleanProperty inActiveContest) {
        inActiveContest.bind(this.inActiveContest);
    }
    

    private void cleanContestData() {
        candidateController.terminateCandidateRefresher();
        contestStatusRefresherTimer.cancel();
        candidateController.cleanCandidateTable();

    }
}
