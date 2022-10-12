package engine.battleField;


import java.util.Objects;

public class Battlefield {

    private String level;
    private String battleName;
    private int allies;


    public Battlefield(String battleName, String level, int allies){
        this.level = level;
        this.battleName = battleName;
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
}
