package registerManagers.clients;

import registerManagers.mediators.Mediator;

public class Agent implements Client,User {
    Mediator mediator;
    String name;

    public Agent(String name) {
        this.name = name;
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
}
