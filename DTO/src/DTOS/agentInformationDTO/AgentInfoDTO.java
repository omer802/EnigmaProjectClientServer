package DTOS.agentInformationDTO;

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
}
