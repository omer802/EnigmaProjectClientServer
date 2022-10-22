package DTOS.UBoatsInformationDTO;

import registerManagers.battlefieldManager.Battlefield;

import java.util.Objects;

public class ContestInformationDTO {
    private String battlefieldName;
    private String UBoatName;
    private String level;
    private String status;
    private int requiredAllies;
    private int signedAllies;
    private String messageToDecode;

    public void setSignedAllies(int signedAllies) {
        this.signedAllies = signedAllies;
    }




    public ContestInformationDTO(Battlefield battlefield, String UBoatName, boolean status, int signedAllies) {
        this.battlefieldName = battlefield.getBattleName();
        this.UBoatName = UBoatName;
        this.status = status == true ? "Active" : "Waiting";
        this.level = battlefield.getLevel().name();
        this.requiredAllies = battlefield.getAmountOfAlliesNeededForContest();
        this.signedAllies = signedAllies;
    }
    public String getBattlefieldName() {
        return battlefieldName;
    }

    public String getUBoatName() {
        return UBoatName;
    }

    public String getLevel() {
        return level;
    }

    public String getStatus() {
        return status;
    }

    public int getRequiredAllies() {
        return requiredAllies;
    }

    public int getSignedAllies() {
        return signedAllies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContestInformationDTO that = (ContestInformationDTO) o;
        return requiredAllies == that.requiredAllies && signedAllies == that.signedAllies && Objects.equals(UBoatName, that.UBoatName) && Objects.equals(level, that.level) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UBoatName, level, status, requiredAllies, signedAllies);
    }
    public String getMessageToDecode() {
        return messageToDecode;
    }

    public void setMessageToDecode(String messageToDecode) {
        this.messageToDecode = messageToDecode;
    }
}
