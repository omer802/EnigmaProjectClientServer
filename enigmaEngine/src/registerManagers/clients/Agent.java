package registerManagers.clients;

import DTOS.agentInformationDTO.AgentInfoDTO;
import registerManagers.mediators.Mediator;

public class Agent implements Client,User {
    private Mediator mediator;
    private String agentName;
    private String chosenAlliesName;

    public String getChosenAlliesName() {
        return chosenAlliesName;
    }

    private int missionAmount;
    private int threadAmount;

    public Agent(AgentInfoDTO agentInfoDTO) {
        this.agentName = agentInfoDTO.getUserName();
        this.chosenAlliesName = agentInfoDTO.getAllieName();
        this.missionAmount = agentInfoDTO.getMissionAmount();
        this.threadAmount = agentInfoDTO.getThreadAmount();
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
        return agentName;
    }
}
