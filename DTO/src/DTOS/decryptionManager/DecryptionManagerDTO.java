package DTOS.decryptionManager;

import engine.decryptionManager.DM;

public class DecryptionManagerDTO {
    private String messageToDecipher;
    private DM.DifficultyLevel level;
    private Double missionSize;

    private int amountOfAgentsForProcess;
    private double missionAmount;

    public DecryptionManagerDTO(String messageToDecipher, DM.DifficultyLevel difficulty, Double missionSize, int amountOfAgentsForProcess, Double missionAmount){
       this.messageToDecipher = messageToDecipher;
       this.level = difficulty;
       this.missionSize = missionSize;

       this.amountOfAgentsForProcess = amountOfAgentsForProcess;
       this.missionAmount = missionAmount;
    }
    public String getMessageToDecipher() {
        return messageToDecipher;
    }

    public DM.DifficultyLevel getLevel() {
        return level;
    }

    public Double getMissionSize() {
        return missionSize;
    }

    public int getAmountOfAgentsForProcess() {
        return amountOfAgentsForProcess;
    }

    public double getMissionAmount() {
        return missionAmount;
    }
}
