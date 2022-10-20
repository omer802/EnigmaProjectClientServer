package registerManagers.clients;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import engine.decryptionManager.DM;
import registerManagers.mediators.Mediator;

import java.util.Objects;

public class Allie implements Client,User {
    public  enum AllieStatus{
        IDLE, READY, IN_CONTEST
    }
    private boolean signed;
    private AllieStatus allieStatus;
    private String name;
    private int agentAmount;
    private long missionSize;
    private UBoat uBoat;
    Mediator mediator;
    private boolean isReady;
    private DM dm;

    public void setDm(DM dm) {
        this.dm = dm;
    }

    public Allie(String userName) {
        this.allieStatus = AllieStatus.IDLE;
        this.name = userName;
        this.signed = false;
        this.missionSize = 1;
        this.agentAmount = 0;
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

    public UBoat getUBoat() {
        return uBoat;
    }

    public void setUBoat(UBoat uBoat) {
        this.uBoat = uBoat;
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
        this.signed = false;
    }
    public void signInToContest(){
        this.signed = true;
    }
    public AlliesDetailDTO getAllieDetailDTO(){
        return new AlliesDetailDTO(name,agentAmount,missionSize);
    }

}
