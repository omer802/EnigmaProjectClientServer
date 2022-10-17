package registerManagers.battlefieldManager;

import java.util.*;

// TODO: 10/17/2022 move this manger to be under UBoat Manager
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
