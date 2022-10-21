package registerManagers.clients;

import registerManagers.mediators.Mediator;

public interface Client {
    void setMediator(Mediator mediator);
    void startContest();

}
