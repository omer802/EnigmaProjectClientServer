package registerManagers.UBoatManager;

import DTOS.Configuration.UserConfigurationDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import engine.api.ApiEnigma;
import engine.api.ApiEnigmaImp;
import registerManagers.battleField.Battlefield;

import java.util.Objects;

public class UBoat {
    public static enum DifficultyLevel{
        EASY, MEDIUM, HARD, IMPOSSIBLE
    }
    public static enum GameStatus{
        WAITING, ACTIVE_GAME
    }
    public static enum UBoatStatus{
        WAITING_FOR_CONFIG, READY, ACTIVE
    }
    private ApiEnigma api;

    Battlefield battlefield;
    private String UBoatName;
    private boolean isActive;
    private boolean isReady;
    UBoatStatus uBoatStatus;
    GameStatus gameStatus;
    private int amountOfTeamNeededForContest;
    private int alliesSignedAmount;



    private String encryptedString;

    private String originalStringNotEncrypted;


    public UBoat(String UBoatName) {
        this.UBoatName = UBoatName;
        this.api = new ApiEnigmaImp();
        this.uBoatStatus = UBoatStatus.WAITING_FOR_CONFIG;
        this.gameStatus = GameStatus.WAITING;
        this.isActive = false;
        this.isReady = false;

    }
    public String getUBoatName() {
        return UBoatName;
    }

    public boolean isActive() {
        return isActive;
    }

    public DifficultyLevel getLevel() {
        return battlefield.getLevel();
    }

    public int getAmountOfTeamNeededForContest() {
        return amountOfTeamNeededForContest;
    }

    public int getAlliesSignedAmount() {
        return alliesSignedAmount;
    }

    public ApiEnigma getApi() {
        return api;
    }
    //public ContestInformationDTO(String battlefield, String UBoatName, String status, String level, int requiredAllies, int signedAllies) {
    public synchronized ContestInformationDTO getContestInformationDTO() {
        ContestInformationDTO contestInformationDTO = new ContestInformationDTO(getBattlefield(), UBoatName, isActive,alliesSignedAmount);
        return contestInformationDTO;

    }

    public void makeUBoatActive(){
        isActive = true;
    }

    public synchronized void  makeUBoatReady(){
        isReady = true;
    }

    public String getEncryptedString() {
        return encryptedString;
    }

    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }
    public void setOriginalStringNotEncrypted(String originalStringNotEncrypted) {
        this.originalStringNotEncrypted = new String(originalStringNotEncrypted);
    }

    public String getOriginalStringNotEncrypted() {
        return originalStringNotEncrypted;
    }

    public UserConfigurationDTO encryptString(String toEncrypt) {
        setOriginalStringNotEncrypted(toEncrypt);
        UserConfigurationDTO userConfigurationDTO = api.dataEncryption(toEncrypt);
        setEncryptedString(userConfigurationDTO.getEncryptedMessage());
        return userConfigurationDTO;
    }

    public Battlefield getBattlefield() {
        return api.getBattleField();
    }

    public void setBattlefield(Battlefield battlefield) {

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UBoat uBoat = (UBoat) o;
        return Objects.equals(UBoatName, uBoat.UBoatName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UBoatName);
    }
}
