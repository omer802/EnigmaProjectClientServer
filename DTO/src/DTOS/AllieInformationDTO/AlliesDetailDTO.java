package DTOS.AllieInformationDTO;


import java.util.Objects;

public class AlliesDetailDTO {



    private int agentAmount;
    private long missionSize;
    private String teamName;

    public AlliesDetailDTO(String teamName, int agentAmount, long missionSize) {
        this.teamName = teamName;
        this.agentAmount = agentAmount;
        this.missionSize = missionSize;
    }
    public String getTeamName() {
        return teamName;
    }

    public int getAgentAmount() {
        return agentAmount;
    }

    public long getMissionSize() {
        return missionSize;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlliesDetailDTO that = (AlliesDetailDTO) o;
        return Objects.equals(agentAmount, that.agentAmount) && Objects.equals(missionSize, that.missionSize) && Objects.equals(teamName, that.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentAmount, missionSize, teamName);
    }

}
