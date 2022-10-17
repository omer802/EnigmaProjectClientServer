package registerManagers.clients;

import DTOS.Configuration.UserConfigurationDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import engine.api.ApiEnigma;
import engine.api.ApiEnigmaImp;
import registerManagers.battlefieldManager.Battlefield;
import registerManagers.mediators.Mediator;

import java.util.Objects;

import static registerManagers.clients.UBoat.GameStatus.*;
import static registerManagers.clients.UBoat.UBoatStatus.ACTIVE;
import static registerManagers.clients.UBoat.UBoatStatus.READY;

public class UBoat implements Client,User {

    private Mediator mediator;
    public static enum DifficultyLevel{
        EASY, MEDIUM, HARD, IMPOSSIBLE
    }
    public  enum GameStatus{
        OFF,WAITING, ACTIVE_GAME, HAVE_ENOUGH_FOR_CONTEST
    }
    public  enum UBoatStatus{
        WAITING_FOR_CONFIG, ACTIVE, READY
    }
    private ApiEnigma api;

    Battlefield battlefield;
    private String name;
    private boolean isActive;
    private boolean isReady;
    UBoatStatus uBoatStatus;
    GameStatus gameStatus;
    private int alliesSignedAmount;



    private String encryptedString;

    private String originalStringNotEncrypted;


    public UBoat(String UBoatName) {
        this.name = UBoatName;
        this.api = new ApiEnigmaImp();
        this.uBoatStatus = UBoatStatus.WAITING_FOR_CONFIG;
        this.gameStatus = OFF;
        this.isActive = false;
        this.isReady = false;

    }
    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void startContest() {

    }
    public String getUserName() {
        return name;
    }

    public boolean isActive() {

        return uBoatStatus.equals(ACTIVE) || uBoatStatus.equals(READY);
    }

    public DifficultyLevel getLevel() {
        return battlefield.getLevel();
    }




    public ApiEnigma getApi() {
        return api;
    }
    //public ContestInformationDTO(String battlefield, String name, String status, String level, int requiredAllies, int signedAllies) {
    public synchronized ContestInformationDTO getContestInformationDTO() {
        if (gameStatus.equals(OFF))
            throw new RuntimeException("not allowed");

        boolean isActive = gameStatus.equals(ACTIVE_GAME);

        ContestInformationDTO contestInformationDTO = new ContestInformationDTO(getBattlefield(), name, isActive, alliesSignedAmount);
        return contestInformationDTO;

    }

    public void makeUBoatActive(){
        uBoatStatus = ACTIVE;
        gameStatus = WAITING;
    }

    public synchronized void  makeUBoatReady() {
        uBoatStatus = READY;
        if (battlefield.getAmountOfAlliesNeededForContest() == alliesSignedAmount)
            gameStatus = ACTIVE_GAME;
        else
            gameStatus = WAITING;
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
        return Objects.equals(name, uBoat.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
