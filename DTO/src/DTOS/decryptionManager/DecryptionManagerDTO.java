package DTOS.decryptionManager;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import registerManagers.clients.UBoat;

public class DecryptionManagerDTO {
    private String messageToDecipher;
    private UBoat.DifficultyLevel level;
    private Double missionSize;

    private double totalMissionAmount;

    public DecryptionManagerDTO(String messageToDecipher, UBoat.DifficultyLevel difficulty, Double missionSize, Double missionAmount){
       this.messageToDecipher = messageToDecipher;
       this.level = difficulty;
       this.missionSize = missionSize;
       this.totalMissionAmount = missionAmount;
    }
    public DecryptionManagerDTO(ContestInformationDTO contestInformationDTO, Double missionSize, Double totalMissionAmount){
        this.messageToDecipher = contestInformationDTO.getMessageToDecode();
        this.level = UBoat.DifficultyLevel.valueOf(contestInformationDTO.getLevel());
        this.missionSize = missionSize;
        this.totalMissionAmount = totalMissionAmount;
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


    public double getTotalMissionAmount() {
        return totalMissionAmount;
    }
}
