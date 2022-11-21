package registerManagers.clients;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.AllieInformationDTO.DMProgressDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import DTOS.decryptionManager.DecryptionManagerDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import engine.decryptionManager.DM;
import registerManagers.mediators.Mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Allie implements Client,User {


    public void setWinner(CandidateDTO candidateDTO) {
        this.winnerDTO = candidateDTO;
    }

    public enum AllieStatus{
        IDLE, READY, IN_CONTEST,FINISHED_CONTEST
    }
    private boolean signed;

    public AllieStatus getAllieStatus() {
        return allieStatus;
    }

    private AllieStatus allieStatus;
    private String name;
    private int agentAmount;

    synchronized public boolean isSignoutFromUBoatAction() {
        return signoutFromUBoatAction;
    }

    private boolean signoutFromUBoatAction;

    private long missionSize;
    private ContestInformationDTO contestInformationDTO;

    public String getStringToDecode() {
        return toDecode;
    }

    private String toDecode;
    Mediator mediator;
    private boolean isReady;
    private double missionAmount;
    private CandidateDTO winnerDTO;
    public CandidateDTO getWinner(){
        return winnerDTO;
    }

    public AtomicLong getPulledMissionsAmount() {
        return pulledMissionsAmount;
    }

    private AtomicLong pulledMissionsAmount;
    private AtomicLong atomicMissionAmount;

    private DM dm;

    public List<CandidateDTO> getCandidateList() {
        return candidateList;
    }

    private List<CandidateDTO> candidateList;






    // TODO: 10/21/2022 remove casting
    synchronized public void startBruteForce(){
        missionAmount = dm.calculateAmountOfTasks( missionSize, UBoat.DifficultyLevel.valueOf(contestInformationDTO.getLevel()));
        pulledMissionsAmount = new AtomicLong(Math.round(missionAmount));
        contestInformationDTO.setMessageToDecode(toDecode);
        DecryptionManagerDTO decryptionManagerDTO = new DecryptionManagerDTO(contestInformationDTO,(double)missionSize,missionAmount);
        dm.DecipherMessage(decryptionManagerDTO);
        allieStatus = AllieStatus.IN_CONTEST;
    }

    public Allie(String userName) {
        this.allieStatus = AllieStatus.IDLE;
        this.name = userName;
        this.signed = false;
        this.missionSize = 1;
        this.agentAmount = 0;
        candidateList = new ArrayList<>();
        signoutFromUBoatAction = false;
    }
    synchronized public void uBoatLogoutActionMode() {
        signoutFromUBoatAction = true;
    }
    public void cancelUBoatLogoutAction(){
        signoutFromUBoatAction = false;
    }
    public BlockingQueue<AgentTaskConfigurationDTO> getBlockingQueue(){
        return dm.getBlockingQueue();
    }
    public void setEncryptedString(String toDecode) {
        this.toDecode = toDecode;
    }
    public DM getDm() {
        return dm;
    }
    public boolean canBeReady(){
        return (agentAmount>0)&&(!allieStatus.equals(AllieStatus.FINISHED_CONTEST));
    }
    public void makeAllieReady() {
        if (canBeReady()) {
            candidateList = new ArrayList<>();
            allieStatus = AllieStatus.READY;
        }
    }
    public void setMissionSize(long missionSize) {
        this.missionSize = missionSize;
    }
    public boolean isReady(){
        return allieStatus.equals(AllieStatus.READY);
    }
    public boolean areInContest(){
        return allieStatus.equals(AllieStatus.IN_CONTEST);
    }

    public void addOneToAgentAmount(){
        agentAmount++;
    }
    public boolean isSigned() {
        return signed;
    }



    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void startContest() {

    }


    @Override
    public String getUserName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allie allie = (Allie) o;
        return Objects.equals(name, allie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


    public void signInToContest(ContestInformationDTO contestInformationDTO, DM dm){
        setDM(dm);
        this.contestInformationDTO = contestInformationDTO;
        this.signed = true;
        this.signoutFromUBoatAction = false;
    }
    public void setDM(DM dm) {
        this.dm = dm;
    }
    public void removeDM(){
        this.dm = null;
    }
    public AlliesDetailDTO getAllieDetailDTO(){
        return new AlliesDetailDTO(name,agentAmount,missionSize);
    }
    public ContestInformationDTO getContestInformationDTO() {
        return contestInformationDTO;
    }
    public long getMissionSize() {
        return missionSize;
    }
    synchronized public void addCandidate(Agent agent, CandidateDTO candidateDTO) {
     //   List<CandidateDTO> copyCandidateDTOS = new ArrayList<>();
      //  for (CandidateDTO candidate: candidatesDTO) {
            try {
                CandidateDTO candidateDTOClone = candidateDTO.clone();
                //copyCandidateDTOS.add(candidateDTOClone);
                candidateDTOClone.setHowFind(agent.getUserName());
                this.candidateList.add(candidateDTOClone);

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
     //   }
      //  copyCandidateDTOS.forEach(candidate -> candidate.setHowFind(agent.getUserName()));
       // this.candidateList.addAll(copyCandidateDTOS);

    }
    public DMProgressDTO getDMProgress(){
        return dm.getDMProgressDTO();

    }
    public void terminateContest() {
        allieStatus = AllieStatus.FINISHED_CONTEST;
        dm.stopTaskCreator();
        //signOutFromContest();
    }
    public void signOutFromContest() {
        contestInformationDTO = null;
        this.signed = false;
        allieStatus = AllieStatus.IDLE;
    }
    public void restDM(){
        dm.restDMProgress();
    }


}
