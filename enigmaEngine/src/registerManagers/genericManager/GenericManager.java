package registerManagers.genericManager;

import registerManagers.alliesManager.Allies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericManager<T> {
    private final List<T> clientList;

    public GenericManager() {

        clientList = new ArrayList<>();
    }

    public synchronized void addClient(T client) {
        clientList.add(client);
    }

    public synchronized void removeClient(T client) {
        clientList.remove(client);
    }

    public synchronized List<T> getClients() {
        return Collections.unmodifiableList(clientList);
    }


    public boolean isClientExists(T client) {
        return clientList.contains(client);
    }
}
