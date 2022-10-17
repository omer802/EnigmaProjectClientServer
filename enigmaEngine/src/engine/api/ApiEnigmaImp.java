package engine.api;

import DTOS.ConfigrationsPropertyAdapter.FileConfigurationDTOAdapter;
import DTOS.ConfigrationsPropertyAdapter.UserConfigurationDTOAdapter;
import DTOS.Configuration.FileConfigurationDTO;
import DTOS.Configuration.UserConfigurationDTO;
import DTOS.StatisticsDTO.MachineStatisticsDTO;
import DTOS.Validators.xmlFileValidatorDTO;
import DTOS.decryptionManager.DecryptionManagerDTO;
import dictionary.Dictionary;
import registerManagers.clients.UBoat;
import registerManagers.battlefieldManager.Battlefield;
import dictionary.Trie;
import engine.decryptionManager.task.TimeToCalc;
import engine.enigma.Enigma;
import engine.enigma.Machine.EnigmaMachine;
import engine.enigma.Machine.NotchAndLetterAtPeekPane;
import engine.enigma.reflector.Reflectors;
import engine.LoadData.LoadData;
import engine.LoadData.LoadDataFromXml;
import engine.enigma.statistics.ConfigurationAndEncryption;
import engine.enigma.statistics.EncryptionData;
import registerManagers.battlefieldManager.BattlefieldManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import keyboard.Keyboard;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
// TODO: 9/8/2022 move api to ui and Enigma machine to warper class that contain DM

public class ApiEnigmaImp implements ApiEnigma {
    
    //private EnigmaMachine enigmaMachine;
    private Enigma enigma;

    FileConfigurationDTOAdapter fileConfigurationDTOAdapter;
    UserConfigurationDTOAdapter userConfigurationDTOAdapter;
    StringProperty statistics;
    private boolean haveConfigurationFromFile;

    private FileConfigurationDTO fileConfigurationDTO;

    public void DecipherMessage(DecryptionManagerDTO decryptionManagerDTO, Runnable onFinish)
    {
        enigma.getDecipher().setMachine(enigma.getMachine().clone());
    }

    public void setDTOConfigurationAdapter(FileConfigurationDTOAdapter fileConfigurationDTOAdapter){
        this.fileConfigurationDTOAdapter = fileConfigurationDTOAdapter;
    }
    public xmlFileValidatorDTO readDataJavaFx(InputStream fileInputStream, BattlefieldManager battlefieldManager){
        xmlFileValidatorDTO validator = readData(fileInputStream,battlefieldManager);
        if(validator.getListOfExceptions().size() == 0) {
        }
        return validator;


    }
    public xmlFileValidatorDTO readData(InputStream fileInputStream, BattlefieldManager battleFieldManager){
        xmlFileValidatorDTO validator = new xmlFileValidatorDTO();
        LoadData dataFromXml = new LoadDataFromXml();
        Enigma tempEnigma = dataFromXml.loadDataFromInput(fileInputStream, validator,battleFieldManager);
        if(validator.getListOfExceptions().size() == 0 && tempEnigma != null) {
            haveConfigurationFromFile = true;
            this.enigma = tempEnigma;

        }
        return validator;
    }

    public void removeEnigma(){
        haveConfigurationFromFile = false;
        enigma = null;
        fileConfigurationDTOAdapter = null;
    }


    public List<String> getPossibleReflectors(){

        return enigma.getMachine().getPossibleReflectors();
    }
    public List<String> isReflectorValid(String chosenReflector){
        List<String> ReflectorErrors = new ArrayList<>();
        if(!chosenReflector.matches("[0-9]+"))
           ReflectorErrors.add("Error: It is not possible to insert an input that is not a number. " +
                   "you need to select ");
        else {
            int chosenReflectorInteger = Integer.parseInt(chosenReflector);
            if (!getPossibleReflectors().contains(Reflectors.IntegerToReflectorString(chosenReflectorInteger)))
                ReflectorErrors.add("Error: There is no reflector in the system with the entered id");
        }
        return ReflectorErrors;
    }
    public FileConfigurationDTO getDataReceivedFromFile(){
        fileConfigurationDTO  = new FileConfigurationDTO(enigma.getMachine());
        return fileConfigurationDTO;
    }
    public UserConfigurationDTO getCurrentConfiguration(){
        UserConfigurationDTO Machinespecification =  new UserConfigurationDTO(enigma.getMachine());
        return Machinespecification;
    }

    public void selectInitialCodeConfiguration(UserConfigurationDTO configuration){
        enigma.getMachine().selectInitialCodeConfiguration(configuration);
        //updateStatisticsProperty();

    }

    public UserConfigurationDTO dataEncryption(String data){
        String encodeInformation = enigma.getMachine().encodeString(data);
        UserConfigurationDTO config = getCurrentConfiguration();
        config.setEncryptedMessage(encodeInformation);
        //UpdateCode(config);
        //updateStatisticsProperty();
        //System.out.println("-----------------");
        //System.out.println(statistics.getValue());
        return config;

    }
    public void updateStatistics(String input, String output, long processingTime){
        enigma.getMachine().addEncryptionToStatistics(input,output,processingTime);
        updateStatisticsProperty();
    }
    public Character encryptChar(char ch){
        Character toReturnChar = enigma.getMachine().encodeChar(ch);
        UserConfigurationDTO config = getCurrentConfiguration();
        UpdateCode(config);
        updateStatisticsProperty();
        return toReturnChar;
    }

    public void resetPositions(){
        enigma.getMachine().getRotorsObject().returnRotorsToStartingPositions();
        UserConfigurationDTO config = getCurrentConfiguration();
        UpdateCode(config);
    }

    // TODO: 9/3/2022 update code
    public UserConfigurationDTO automaticallyInitialCodeConfiguration(){
        enigma.getMachine().automaticInitialCodeConfiguration();
       // updateStatisticsProperty();
         return getCurrentConfiguration();
    }

    public List<String> getPossibleRotors(){
       return enigma.getMachine().getPossibleRotors();
    }
    public int getAmountOfRotors(){
        return enigma.getMachine().getRotorsAmountInUse();
    }
    public List<String> isLegalRotors(String chosenRotorsInputStr)  {
        List<String> RotorsErrors = new ArrayList<>();
        List<String> chosenRotorsList = cleanStringAndReturnList(chosenRotorsInputStr);
        List<String> possibleRotors = getPossibleRotors();
        if(chosenRotorsList.size()!= getAmountOfRotors())
            RotorsErrors.add("Error: The amount of rotors does not correspond to the amount of rotors" +
                    " that exist in the system." + " You have to insert "+ getAmountOfRotors()+" rotors from: "+ getPossibleRotors());

        if(!possibleRotors.containsAll(chosenRotorsList))
            RotorsErrors.add("Error: The rotors you are trying to insert is out of range. You have to insert " + getAmountOfRotors()+" rotors from: "+ getPossibleRotors());
        // TODO: 9/1/2022 check if working with console
        if(isIdenticalRotors(chosenRotorsList))
            RotorsErrors.add("Error: You have entered more than one identical rotor. You have to insert " + getAmountOfRotors()+" rotors from: "+ getPossibleRotors());
        return RotorsErrors;
    }
    public boolean isIdenticalRotors(List<String> chosenRotorsList){
        return chosenRotorsList.stream().distinct().count() != chosenRotorsList.size();

    }
    public List<String> cleanStringAndReturnList(String chosenRotorsInputStr){
        //Clearing spaces  and tabs from the user if entered
        String chosenRotors = chosenRotorsInputStr.replaceAll(" ", "");
        chosenRotors = chosenRotors.replaceAll("\t", "");
        List<String> chosenRotorsInput =  Arrays.asList(chosenRotors.split(","));
        return chosenRotorsInput;
    }
    public boolean isConfigFromUser() {
        return haveConfigurationFromFile;
    }
    public boolean isRotorsPositionsInRange(String positions){
        return Keyboard.isStringInRange(positions);
    }

    public boolean alreadyEncryptedMessages(){
        return EnigmaMachine.getTheNumberOfStringsEncrypted()>0;
    }
    public UserConfigurationDTO getOriginalConfiguration(){
        return enigma.getMachine().getCurrentStartingConfiguration();
    }

    public MachineStatisticsDTO getStatistics(){
        return enigma.getMachine().getStatistics();
    }
    public boolean haveConfigFromUser(){
        return enigma.getMachine().isConfigFromUser();
    }
    public boolean haveConfigFromFile(){
        return enigma.getMachine().isConfigFromFile();
    }

    public void saveMachineStateToFile(String filePath) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(filePath))) {
            out.writeObject(this.enigma.getMachine());
            out.flush();
        }
    }
    public void loadMachineStateFromFIle(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(filePath))) {
            // we know that we read the enigmaMachine we saved
            this.enigma.setMachine( (EnigmaMachine) in.readObject());
        }
    }
    public boolean validateStringToEncrypt(String stringToEncrypt) {
        return (Keyboard.isStringInRange(stringToEncrypt));
    }


    public StringBuilder getStringDataReceiveFromUser(UserConfigurationDTO machineConfigUser){
        return machineConfigUser.getCodeConfigurationString();
        /*StringBuilder stringBuilder = new StringBuilder();
        List<String> rotorsWithOrder = machineConfigUser.getChosenRotorsWithOrder();
        getChosenRotorsWithOrder(rotorsWithOrder, stringBuilder);

        List<NotchAndLetterAtPeekPane> currentPositionsAndNotch = machineConfigUser.getNotchAndLetterPair();
        getCurrentPositionsWithDistanceFromNotch(currentPositionsAndNotch, stringBuilder);

        stringBuilder.append("<"+machineConfigUser.getChosenReflector()+">");
        if(machineConfigUser.isPlugged()&&machineConfigUser.getPlugBoardConnectionsWithFormat().length()>0)
            stringBuilder.append("<" + machineConfigUser.getPlugBoardConnectionsWithFormat()+ ">");
        return stringBuilder;*/
    }
    public void getChosenRotorsWithOrder( List<String> rotorsWithOrder, StringBuilder stringBuilderInput){
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
    public void getCurrentPositionsWithDistanceFromNotch(List<NotchAndLetterAtPeekPane> chosenRotors, StringBuilder stringBuilderInput){
        //reverse the order of rotors beacuse in the machine we work from right to left and the ui show from left to right
        //Collections.reverse(chosenRotors);
        boolean isFirst = true;
        stringBuilderInput.append("<");
        for (NotchAndLetterAtPeekPane pair: chosenRotors)
            stringBuilderInput.append(pair.toString());
        stringBuilderInput.append( ">");
    }
    public void setCurrentConfigurationProperties(UserConfigurationDTOAdapter DTOPropertiesToConfig){
        userConfigurationDTOAdapter = DTOPropertiesToConfig;
        UserConfigurationDTO originalConfigurationDTO = getOriginalConfiguration();
        setUserConfigurationDTO(originalConfigurationDTO);
    }
    public void UpdateCode(UserConfigurationDTO originalConfigurationDTO) {
        StringBuilder originalConfiguration = getStringDataReceiveFromUser(originalConfigurationDTO);
        String[] configurationArray = originalConfiguration.toString().replace(">", "")
                .split("<");
        if (userConfigurationDTOAdapter != null) {
            userConfigurationDTOAdapter.setNotchAndLetterAtPeekPaneStartingPosition(configurationArray[2]);
            userConfigurationDTOAdapter.setFullConfiguration(originalConfiguration.toString());
        }

    }
    public void setUserConfigurationDTO(UserConfigurationDTO originalConfigurationDTO){
        StringBuilder originalConfiguration = getStringDataReceiveFromUser(originalConfigurationDTO);
        String[] configurationArray = originalConfiguration.toString().replace(">","")
                .split("<");
        userConfigurationDTOAdapter.setChosenRotors(configurationArray[1]);
        userConfigurationDTOAdapter.setNotchAndLetterAtPeekPaneStartingPosition(configurationArray[2]);
        userConfigurationDTOAdapter.setChosenReflector(configurationArray[3]);
        // TODO: 9/3/2022 make it work with events
        if(enigma.getMachine().isPluged()){
            userConfigurationDTOAdapter.setPlugBoardToShow(originalConfigurationDTO.getPlugBoardConnectionsWithFormat());
        }
        else
            userConfigurationDTOAdapter.setPlugBoardToShow("");

        userConfigurationDTOAdapter.setFullConfiguration(originalConfiguration.toString());
      // dataEncryption("AAA");
    }

    public void setStatisticsProperty(StringProperty statisticsProperty){
        this.statistics = new SimpleStringProperty();
       statisticsProperty.bind(Bindings.format("%s",(statistics)));
    }
    private void updateStatisticsProperty(){
        this.statistics.setValue(PrintStatistic().toString());
    }

    public StringBuilder PrintStatistic() {
        String pattern = "###,###,###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        MachineStatisticsDTO statisticsToShow = getStatistics();
        StringBuilder sb = new StringBuilder();
        sb.append("History and statistics:\n");
        sb.append("the code configurations performed in the system:\n");
        for (ConfigurationAndEncryption configuration: statisticsToShow.getConfigurations())
            sb.append("\t"+getStringDataReceiveFromUser(configuration.getConfiguration())+"\n");
        sb.append("\n");
        if(statisticsToShow.haveEncoded()) {
            sb.append("Configurations used by the machine and the encrypted messages in each configuration:\n");
            for (ConfigurationAndEncryption configuration : statisticsToShow.getConfigurationsInUse()) {
                UserConfigurationDTO config = configuration.getConfiguration();
                sb.append("In configuration:" + getStringDataReceiveFromUser(config) + " The following messages have been encrypted: \n");

                for (EncryptionData data : configuration.getEncryptionDataList()) {
                    String Format = decimalFormat.format(data.getProcessingTime());
                    sb.append("\t#.<" + data.getInput().toUpperCase() + ">" + "--> " + "<" + data.getOutput().toUpperCase() + ">" + " Encryption time in nano seconds: " + Format + "\n");
                }
            }
        }
        else
            sb.append("The machine has not yet encrypted messages");
        return sb;
    }
    public boolean isDictionaryContainString(String toEncrypt){
        return enigma.getDecipher().isDictionaryContainString(toEncrypt);
    }
    public String cleanStringFromExcludeChars(String words){
        return enigma.getDecipher().cleanStringFromExcludeChars(words);
    }
    public Trie getTrieFromDictionary(){
        return enigma.getDecipher().getTrieFromDictionary();
    }
    public Dictionary getDictionary(){
        return enigma.getDecipher().getDictionary();

    }
    public int getAmountOfAgents(){
        return enigma.getDecipher().getAmountOfAgents();
    }
    public double calculateAmountOfTasks(Integer missionSize, UBoat.DifficultyLevel level){
        return enigma.getDecipher().calculateAmountOfTasks(missionSize,level);
    }
    public void cancelCurrentTask(){
        enigma.getDecipher().cancelCurrentTask();
    }
    public void pauseCurrentTask(){

    }
    public void resumeCurrentTask(){

    }

    @Override
    public TimeToCalc getTimeToCalc() {
       return enigma.getDecipher().getTimeToCalc();
    }

    @Override
    public Battlefield getBattleField() {
        return enigma.getBattleField();
    }


}
