package registerManagers.clients;

import registerManagers.mediators.Mediator;

import java.util.Objects;

public class Allie implements Client,User {


    public boolean isSigned() {
        return signed;
    }

    private boolean signed;
    private String name;
    private UBoat uBoat;
    Mediator mediator;

    public Allie(String userName) {

        this.name = userName;
        this.signed = false;
    }

    public UBoat getUBoat() {
        return uBoat;
    }

    public void setUBoat(UBoat uBoat) {
        this.uBoat = uBoat;
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void startContest() {

    }


    @Override
    public String getUserName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allie allie = (Allie) o;
        return Objects.equals(name, allie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void signOutFromContest() {
        this.signed = false;
    }
    public void signInToContest(){
        this.signed = true;
    }

}
