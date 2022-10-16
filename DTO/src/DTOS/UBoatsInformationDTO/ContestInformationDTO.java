package DTOS.UBoatsInformationDTO;

import registerManagers.battleField.Battlefield;

import java.util.Objects;

public class ContestInformationDTO {
    private String battlefieldName;
    private String UBoatName;
    private String level;
    private String status;
    private int requiredAllies;
    private int signedAllies;

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
        return Objects.equals(battlefieldName, that.battlefieldName) && Objects.equals(UBoatName, that.UBoatName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battlefieldName, UBoatName);
    }
}
