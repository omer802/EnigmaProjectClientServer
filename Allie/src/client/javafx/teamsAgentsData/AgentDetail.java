package client.javafx.teamsAgentsData;



public class AgentDetail {
    private Integer threadAmount;
    private Double missionAmount;
    private String agentName;

    public AgentDetail(String teamName, int agentAmount, double missionSize) {
        this.agentName = teamName;
        this.threadAmount = agentAmount;
        this.missionAmount = missionSize;
    }
    public String getAgentName() {
        return agentName;
    }

    public int getThreadAmount() {
        return threadAmount;
    }

    public double getMissionAmount() {
        return missionAmount;
    }

}
