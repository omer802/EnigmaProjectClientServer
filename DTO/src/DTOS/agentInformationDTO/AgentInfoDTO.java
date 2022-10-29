package DTOS.agentInformationDTO;

import java.util.Objects;

public class AgentInfoDTO {
    private String userName;
    private String AllieName;
    private int threadAmount;
    private int missionAmount;

    public AgentInfoDTO(String userName, String allieName, int threadAmount, int missionAmount) {
        this.userName = userName;
        AllieName = allieName;
        this.threadAmount = threadAmount;
        this.missionAmount = missionAmount;
    }

    public String getUserName() {
        return userName;
    }

    public String getAllieName() {
        return AllieName;
    }

    public int getThreadAmount() {
        return threadAmount;
    }

    public int getMissionAmount() {
        return missionAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentInfoDTO that = (AgentInfoDTO) o;
        return threadAmount == that.threadAmount && missionAmount == that.missionAmount && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, threadAmount, missionAmount);
    }
}
