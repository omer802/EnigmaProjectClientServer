package registerManagers.clients;

import DTOS.Configuration.UserConfigurationDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import engine.api.ApiEnigma;
import engine.api.ApiEnigmaImp;
import registerManagers.battlefieldManager.Battlefield;
import registerManagers.mediators.Mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static registerManagers.clients.UBoat.GameStatus.*;
import static registerManagers.clients.UBoat.UBoatStatus.ACTIVE;
import static registerManagers.clients.UBoat.UBoatStatus.READY;

public class UBoat implements Client,User {

    private Mediator mediator;

    public boolean isAlliesCapacityFull() {
        return alliesSignedAmount == api.getBattleField().getAmountOfAlliesNeededForContest();
    }

    public void finishContest() {
        gameStatus  = FINISH_CONTEST_WAITING;
    }




    public static enum DifficultyLevel{
        EASY, MEDIUM, HARD, IMPOSSIBLE
    }
    public  enum GameStatus{
        OFF,WAITING,WAITING_AND_READY, ACTIVE_GAME, HAVE_ENOUGH_FOR_CONTEST,FINISH_CONTEST_WAITING,FINISH
    }
    public enum UBoatStatus{
        WAITING_FOR_CONFIG, ACTIVE, READY
    }

    private ApiEnigma api;

    Battlefield battlefield;
    private String name;
    public GameStatus getContestStatus() {
        return gameStatus;
    }
    UBoatStatus uBoatStatus;
    GameStatus gameStatus;
    private int alliesSignedAmount;

    private String encryptedString;

    private String originalStringNotEncrypted;


    private List<CandidateDTO> candidateList;

    public CandidateDTO getWinner() {
        return winner;
    }

    public void setWinner(CandidateDTO winner) {
        this.winner = winner;
    }

    private CandidateDTO winner;


    public UBoat(String UBoatName) {
        this.name = UBoatName;
        this.api = new ApiEnigmaImp();
        this.uBoatStatus = UBoatStatus.WAITING_FOR_CONFIG;
        this.gameStatus = OFF;
        this.candidateList = new ArrayList<>();
    }
    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void startContest() {
        if (canStartContest()){
            gameStatus = ACTIVE_GAME;
        }
    }
    public boolean isActiveContest(){
        return gameStatus.equals(ACTIVE_GAME);
    }
    public boolean canStartContest(){
        return api.getBattleField().getAmountOfAlliesNeededForContest() == alliesSignedAmount && uBoatStatus.equals(READY);
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
    public void addOneToAlliesCounter(){
        alliesSignedAmount++;
    }
    public void subOneFromAlliesCounter(){
        alliesSignedAmount--;
    }

    synchronized public void makeUBoatReady() {
        uBoatStatus = READY;
        /*if (battlefield.getAmountOfAlliesNeededForContest() == alliesSignedAmount)
            gameStatus = HAVE_ENOUGH_FOR_CONTEST;
        else*/
            gameStatus = WAITING_AND_READY;
    }
    public boolean isReady(){
        return uBoatStatus.equals(READY);
    }

    public String getEncryptedString() {
        return encryptedString;
    }

    public void setEncryptedString(String encryptedString) {
        this.encryptedString = encryptedString;
    }
    public void setOriginalStringNotEncrypted(String originalStringNotEncrypted) {
        this.originalStringNotEncrypted = originalStringNotEncrypted;
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
    public void addCandidate(Allie allie, CandidateDTO candidatesDTO) {
        //List<CandidateDTO> copyCandidateList = new ArrayList<>(candidatesDTO);
        //candidatesDTO.forEach(candidate -> candidate.setHowFind(allie.getUserName()));
        candidatesDTO.setHowFind(allie.getUserName());
        this.candidateList.add(candidatesDTO);
    }

    public void setBattlefield(Battlefield battlefield) {

    }
    public List<CandidateDTO> getCandidateList() {
        return candidateList;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UBoat uBoat = (UBoat) o;
        return Objects.equals(name, uBoat.name);
    }

    public int getAlliesSignedAmount() {
        return alliesSignedAmount;
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean isDecodingCorrect(CandidateDTO candidatesDTO){
        System.out.println(originalStringNotEncrypted);
       // candidatesDTO.stream().map(CandidateDTO::getCandidateString).forEach(System.out::println);
        return candidatesDTO.getCandidateString().equals(originalStringNotEncrypted);
    }

}
