package registerManagers.Managers;

import registerManagers.clients.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenericManager<T extends User> {
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
    public synchronized T getClientByName(String clientName){
        List<T> clientList = getClients();
        for (T client: clientList) {
            if(client.getUserName().equals(clientName))
                return client;
        }
        return null;
    }
}
