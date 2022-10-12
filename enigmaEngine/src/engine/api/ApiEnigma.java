package engine.api;

import DTOS.ConfigrationsPropertyAdapter.FileConfigurationDTOAdapter;
import DTOS.ConfigrationsPropertyAdapter.UserConfigurationDTOAdapter;
import DTOS.Configuration.FileConfigurationDTO;
import DTOS.Configuration.UserConfigurationDTO;
import DTOS.StatisticsDTO.MachineStatisticsDTO;
import DTOS.Validators.xmlFileValidatorDTO;
import DTOS.decryptionManager.DecryptionManagerDTO;
import engine.battleField.Battlefield;
import engine.decryptionManager.DM;
import Trie.Trie;
import engine.decryptionManager.task.TimeToCalc;
import engine.registerManagers.BattlefieldManager;
import javafx.beans.property.StringProperty;

import java.io.InputStream;
import java.util.List;

public interface ApiEnigma {
    //here we will put all the implement for the machine
    xmlFileValidatorDTO readData(InputStream fileInputStream, BattlefieldManager battleFieldManager);
    void removeEnigma();

    void selectInitialCodeConfiguration(UserConfigurationDTO configuration);

    String dataEncryption(String data);

    void resetPositions();

    FileConfigurationDTO getDataReceivedFromFile();

    UserConfigurationDTO getCurrentConfiguration();

    List<String> getPossibleReflectors();

    List<String> getPossibleRotors();

    boolean isConfigFromUser();

    int getAmountOfRotors();

    boolean isIdenticalRotors(List<String> chosenRotorsList);

    List<String> isLegalRotors(String chosenRotors);

    boolean isRotorsPositionsInRange(String positions);

    List<String> isReflectorValid(String chosenReflector);

    List<String> cleanStringAndReturnList(String chosenRotorsInputStr);

    boolean alreadyEncryptedMessages();

    UserConfigurationDTO getOriginalConfiguration();

    UserConfigurationDTO automaticallyInitialCodeConfiguration();

    MachineStatisticsDTO getStatistics();

    boolean haveConfigFromUser();

    boolean haveConfigFromFile();

    // void saveMachineStateToFile(String filePath) throws IOException;

    //void loadMachineStateFromFIle(InputStream fileInputStream) throws IOException, ClassNotFoundException;

    boolean validateStringToEncrypt(String stringToEncrypt);

    void setDTOConfigurationAdapter(FileConfigurationDTOAdapter fileConfigurationDTOAdapter);

    public xmlFileValidatorDTO readDataJavaFx(InputStream fileInputStream, BattlefieldManager battlefieldManager);

        StringBuilder getStringDataReceiveFromUser(UserConfigurationDTO machineConfigUser);

    void setCurrentConfigurationProperties(UserConfigurationDTOAdapter DTOPropertiesToConfig);

    void setUserConfigurationDTO(UserConfigurationDTO originalConfigurationDTO);

    void UpdateCode(UserConfigurationDTO originalConfigurationDTO);

    StringBuilder PrintStatistic();

    Character encryptChar(char ch);

    void updateStatistics(String input, String output, long processingTime);

    void setStatisticsProperty(StringProperty statisticsProperty);

    void DecipherMessage(DecryptionManagerDTO decryptionManagerDTO, Runnable onFinish);

    boolean isDictionaryContainString(String toEncrypt);

    String cleanStringFromExcludeChars(String words);

    Trie getTrieFromDictionary();

    int getAmountOfAgents();

    double calculateAmountOfTasks(Integer missionSize, DM.DifficultyLevel level);
    void cancelCurrentTask();

    void pauseCurrentTask();

    void resumeCurrentTask();
    TimeToCalc getTimeToCalc();

    Battlefield getBattleField();
}


