package DTOS.agentInformationDTO;

import java.util.Objects;

public class AgentProgressDTO {
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    private  String agentName;
    private long AcceptedMissions = 0;
    private int WaitingMissions = 0;
    private int CandidatesAmount = 0;

    public long getCompletedMissionsAmount() {
        return completedMissionsAmount;
    }

    private long completedMissionsAmount = 0;;

    public AgentProgressDTO(String agentName, long acceptedMissions, int waitingMissions, int candidatesAmount, long completedMissionsAmount) {
        this.agentName = agentName;
        this.AcceptedMissions = acceptedMissions;
        this.WaitingMissions = waitingMissions;
        this.CandidatesAmount = candidatesAmount;
        this.completedMissionsAmount = completedMissionsAmount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentProgressDTO that = (AgentProgressDTO) o;
        return AcceptedMissions == that.AcceptedMissions && WaitingMissions == that.WaitingMissions && CandidatesAmount == that.CandidatesAmount && Objects.equals(agentName, that.agentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentName, AcceptedMissions, WaitingMissions, CandidatesAmount);
    }
}
