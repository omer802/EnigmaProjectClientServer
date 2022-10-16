package registerManagers.agentManager;

import java.util.*;

public class AgentManager {
    private final List<Agent> agentList ;
    public AgentManager() {

        agentList = new ArrayList<>();
    }

    public synchronized void addAgent(Agent agent) {
        agentList.add(agent);
    }

    public synchronized void removeAgent(Agent agent) {
        agentList.remove(agent);
    }

    public synchronized List<Agent> getUsers() {
        return Collections.unmodifiableList(agentList);
    }

    public boolean isAgentExists(Agent agent) {
        return agentList.contains(agent);
    }
}
