package registerManagers.battleField;


import registerManagers.UBoatManager.UBoat;

import java.util.Objects;

public class Battlefield {


    private UBoat.DifficultyLevel level;

    private String battleName;
    private int allies;


    public Battlefield(String battleName, String level, int allies){
        this.battleName = battleName;
        this.level = UBoat.DifficultyLevel.valueOf(level.toUpperCase());
        this.allies = allies;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Battlefield that = (Battlefield) o;
        return battleName.equals(that.battleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleName);
    }
    public UBoat.DifficultyLevel getLevel() {
        return level;
    }

    public String getBattleName() {
        return battleName;
    }

    public int getAmountOfAlliesNeededForContest() {
        return allies;
    }
}
