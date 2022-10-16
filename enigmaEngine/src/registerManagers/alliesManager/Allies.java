package registerManagers.alliesManager;

import registerManagers.UBoatManager.UBoat;

public class Allies {
    private String userName;
    private UBoat uBoat;

    public Allies(String userName) {
        this.userName = userName;
    }

    public UBoat getUBoat() {
        return uBoat;
    }

    public void setUBoat(UBoat uBoat) {
        this.uBoat = uBoat;
    }
}
