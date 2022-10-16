package client.javafx.teamsAgentsData;



public class AgentDetail {
    private Integer threadAmount;
    private Double missionSize;
    private String agentName;

    public AgentDetail(String teamName, int agentAmount, double missionSize) {
        this.agentName = teamName;
        this.threadAmount = agentAmount;
        this.missionSize = missionSize;
    }
    public String getAgentName() {
        return agentName;
    }

    public int getThreadAmount() {
        return threadAmount;
    }

    public double getMissionSize() {
        return missionSize;
    }

}
