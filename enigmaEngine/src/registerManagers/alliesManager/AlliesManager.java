package registerManagers.alliesManager;

import registerManagers.agentManager.Agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlliesManager {
    private final List<Allies> alliesList ;
    public AlliesManager() {

        alliesList = new ArrayList<>();
    }

    public synchronized void addAgent(Allies allies) {
        alliesList.add(allies);
    }

    public synchronized void removeAgent(Allies allies) {
        alliesList.remove(allies);
    }

    public synchronized List<Allies> getUsers() {
        return Collections.unmodifiableList(alliesList);
    }

    public boolean isAgentExists(Allies allies) {
        return alliesList.contains(allies);
    }
}
