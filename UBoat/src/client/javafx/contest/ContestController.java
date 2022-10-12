package client.javafx.contest;

import Trie.Trie;
import client.javafx.UBoatMainController.MainController;
import client.util.Constants;
import client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static client.util.Constants.GSON_INSTANCE;

public class ContestController {

    private  SimpleStringProperty encryptionTextFiled;
    private SimpleBooleanProperty isBruteForceProcess;
    @FXML
    private BorderPane bruteForceBorderPane;

    @FXML
    private ScrollPane scrollPaneEncryptDecrypt;

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

    private MainController mainController;
    private SimpleBooleanProperty isConfig;
    private SimpleStringProperty encryptionResultProperty;
    private SimpleStringProperty codeConfiguration;
    private String decryptedMessage;
    private Trie trieAutoComplete;

    private void initialize() {
        bruteForceBorderPane.disableProperty().bind(isConfig.not());
        EncryptDecryptResultLabel.textProperty().bind(Bindings.format("%s", encryptionResultProperty));
        codeConfigurationLabel.textProperty().bind(Bindings.format("%s", codeConfiguration));

    }
    public ContestController(){
        this.encryptionResultProperty = new SimpleStringProperty("");
        this.isBruteForceProcess = new SimpleBooleanProperty();
        this.codeConfiguration = new SimpleStringProperty();
        this.encryptionTextFiled = new SimpleStringProperty("");
        this.isConfig = new SimpleBooleanProperty();
    }

    public void setMainPageController(MainController mainPageController) {
        this.mainController = mainPageController;
        this.isConfig.bind(mainPageController.isConfigProperty());
    }



    @FXML
    void AddWordToEncryptTextField(MouseEvent event) {

    }

    @FXML
    void EncryptFullMessage(ActionEvent event) {
        String toEncrypt = encryptedMessage.getText();
       // String cleanedToEncrypt = api.cleanStringFromExcludeChars(toEncrypt);
       /* if(api.isDictionaryContainString(cleanedToEncrypt))
        {
            String encryptionResult = encryptFullMessageNoneAction(cleanedToEncrypt);
            encryptionResultProperty.set(encryptionResult);
            decryptedMessage = encryptionResult;
        }
        else{
            mainPageController.alertShowException(new RuntimeException("The word you entered is not in the dictionary"));
        }*/
    }
    private String encryptFullMessageNoneAction(String toEncrypt) {
       /* toEncrypt = toEncrypt.toUpperCase();
        //boolean isStringValid = api.validateStringToEncrypt(toEncrypt);
        if (!isStringValid) {
            mainController.alertShowException(new RuntimeException("Some of the letters you entered are not from the alphabet"));
            //System.out.println("Some of the letters you entered are not from the alphabet");
            return null;
        }
        else {
            if (toEncrypt.length() > 0) {
                //String encryptedString = api.dataEncryption(toEncrypt);
                return encryptedString;
                return null
            }
        }*/
        return null;
    }

    public void setBruteForceComponent(){
        initProcessComponent();
        fetchTrie();
        fillDictionary();
    }
    private void initProcessComponent()
    {
        encryptedMessage.setText("");
        encryptionResultProperty.set("");
    }
    public void fetchTrie(){
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .build()
                .toString();

        /*HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                    mainController.alertShowException(e);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            mainController.alertShowException(new RuntimeException(responseBody)));
                } else {
                    Platform.runLater(() -> {
                        try {
                            String trieJsonString = response.body().string();
                            trieAutoComplete = GSON_INSTANCE.fromJson(trieJsonString, Trie.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });*/

    }

    public void fillDictionary(){
        SearchField.setText("");
        DictionaryListViewField.getItems().clear();
        List<String> allWordsInTrie = trieAutoComplete.suggest("");
        for (String word: allWordsInTrie) {
            DictionaryListViewField.getItems().add(word);
        }
    }
    @FXML
    void clearTextAndProcessNewMessage(ActionEvent event) {

    }

    @FXML
    void getWordsByPrefix(KeyEvent event) {

    }

    @FXML
    void resetCodeButton(ActionEvent event) {

    }

}
