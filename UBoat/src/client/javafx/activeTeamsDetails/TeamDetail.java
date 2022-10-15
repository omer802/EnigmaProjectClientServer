package client.javafx.activeTeamsDetails;



public class TeamDetail {



    private Integer agentAmount;
    private Double missionSize;
    private String teamName;

    public TeamDetail(String teamName, int agentAmount, double missionSize) {
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

    public double getMissionSize() {
        return missionSize;
    }

}
