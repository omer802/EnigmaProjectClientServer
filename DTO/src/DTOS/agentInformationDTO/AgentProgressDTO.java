package DTOS.agentInformationDTO;

public class AgentProgressDTO {
    private  String agentName;
    private long AcceptedMissions;
    private int WaitingMissions;
    private int CandidatesAmount;

    public AgentProgressDTO(String agentName, long acceptedMissions, int waitingMissions, int candidatesAmount) {
        this.agentName = agentName;
        AcceptedMissions = acceptedMissions;
        WaitingMissions = waitingMissions;
        CandidatesAmount = candidatesAmount;
    }

    public String getAgentName() {
        return agentName;
    }

    public long getAcceptedMissions() {
        return AcceptedMissions;
    }

    public int getWaitingMissions() {
        return WaitingMissions;
    }

    public int getCandidatesAmount() {
        return CandidatesAmount;
    }
}
