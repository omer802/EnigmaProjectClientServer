package registerManagers.clients;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.decryptionManager.DecryptionManagerDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import engine.decryptionManager.DM;
import engine.decryptionManager.task.MissionTask;
import registerManagers.mediators.Mediator;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class Allie implements Client,User {
    public  enum AllieStatus{
        IDLE, READY, IN_CONTEST
    }
    private boolean signed;
    private AllieStatus allieStatus;
    private String name;
    private int agentAmount;
    private long missionSize;
    private ContestInformationDTO contestInformationDTO;
    private String toDecode;
    Mediator mediator;
    private boolean isReady;
    private double missionAmount;

    private DM dm;

    // TODO: 10/21/2022 remove casting
    public void startBruteForce(){
        missionAmount = dm.calculateAmountOfTasks( missionSize, UBoat.DifficultyLevel.valueOf(contestInformationDTO.getLevel()));
        contestInformationDTO.setMessageToDecode(toDecode);
        DecryptionManagerDTO decryptionManagerDTO = new DecryptionManagerDTO(contestInformationDTO,(double)missionSize,missionAmount);
        dm.DecipherMessage(decryptionManagerDTO);
    }

    public Allie(String userName) {
        this.allieStatus = AllieStatus.IDLE;
        this.name = userName;
        this.signed = false;
        this.missionSize = 1;
        this.agentAmount = 0;
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
        return (agentAmount>0)&&(allieStatus.equals(AllieStatus.IDLE));
    }
    public void makeAllieReady(){
        if(canBeReady())
            allieStatus = AllieStatus.READY;
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
        allieStatus = AllieStatus.IN_CONTEST;
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

    public void signOutFromContest() {
        contestInformationDTO = null;
        removeDM();
        this.signed = false;
    }
    public void signInToContest(ContestInformationDTO contestInformationDTO, DM dm){
        setDM(dm);
        this.contestInformationDTO = contestInformationDTO;
        this.signed = true;
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

}
