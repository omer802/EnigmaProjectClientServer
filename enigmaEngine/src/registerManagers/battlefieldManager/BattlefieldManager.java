package registerManagers.battlefieldManager;

import registerManagers.battleField.Battlefield;

import java.util.*;

public class BattlefieldManager {


    private final Set<Battlefield> battlefieldSet;

    public BattlefieldManager() {
        battlefieldSet = new HashSet<>();
    }

    public synchronized void addBattlefield(Battlefield battlefield) {
        battlefieldSet.add(battlefield);
    }

    public synchronized void removeBattlefield(Battlefield battlefield) {
        battlefieldSet.remove(battlefield);
    }

    public synchronized Set<Battlefield> getBattlefields() {
        return Collections.unmodifiableSet(battlefieldSet);
    }

    public boolean isBattlefieldExists(Battlefield battlefield) {
        return battlefieldSet.contains(battlefield);
    }


}
