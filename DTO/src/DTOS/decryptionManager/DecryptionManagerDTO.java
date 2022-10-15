package DTOS.decryptionManager;

import registerManagers.UBoatManager.UBoat;

public class DecryptionManagerDTO {
    private String messageToDecipher;
    private UBoat.DifficultyLevel level;
    private Double missionSize;

    private int amountOfAgentsForProcess;
    private double missionAmount;

    public DecryptionManagerDTO(String messageToDecipher, UBoat.DifficultyLevel difficulty, Double missionSize, int amountOfAgentsForProcess, Double missionAmount){
       this.messageToDecipher = messageToDecipher;
       this.level = difficulty;
       this.missionSize = missionSize;

       this.amountOfAgentsForProcess = amountOfAgentsForProcess;
       this.missionAmount = missionAmount;
    }
    public String getMessageToDecipher() {
        return messageToDecipher;
    }

    public UBoat.DifficultyLevel getLevel() {
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
