package registerManagers.Managers;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import DTOS.enigmaComponentContainers.ConfigurationForAgentBruteForceDTO;
import dictionary.Dictionary;
import engine.api.ApiEnigma;
import engine.decryptionManager.task.MissionTask;
import engine.enigma.Machine.EnigmaMachine;
import registerManagers.battlefieldManager.Battlefield;
import registerManagers.clients.Allie;
import registerManagers.clients.UBoat;
import registerManagers.clients.Agent;
import registerManagers.mediators.Mediator;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class RegisterManager {

    // TODO: 10/20/2022 create dm and make it work with this  


    public static enum ClientType {
        UBOAT, ALLIE, AGENT
    }

    synchronized public Mediator getMediator() {
        return mediator;
    }

    private Mediator mediator;
    private GenericManager<ClientUser> userManager;
    private GenericManager<Battlefield> battlefieldManager;
    private GenericManager<UBoat> UBoatManager;
    private GenericManager<Allie> alliesManager;
    private GenericManager<Agent> agentManager;

    private static final Object alliesAndUBoatLock = new Object();


    public RegisterManager() {
        mediator = new Mediator();
        userManager = new GenericManager<>();
        battlefieldManager = new GenericManager<>();
        UBoatManager = new GenericManager<>();
        alliesManager = new GenericManager<>();
        agentManager = new GenericManager<>();
    }
    public ConfigurationForAgentBruteForceDTO fetchDictionaryAndMachineByAgentName(String agentUserName) {
        mediator.startContest();
        Agent agent = agentManager.getClientByName(agentUserName);
        Allie allie = getAllieByAgentName(agentUserName);
        EnigmaMachine enigmaMachine = allie.getDm().getMachine().clone();
        if(enigmaMachine == null){
            System.out.println("Machine is not config at agent");
            throw new RuntimeException("Machine is not config at agent");
        }
        Dictionary dictionary = agent.getDictionary();
        if(dictionary == null){
            System.out.println("Dictionary is not config at agent");
            throw new RuntimeException("Dictionary is not config at agent");
        }
        startContest();
        return new ConfigurationForAgentBruteForceDTO(enigmaMachine,dictionary);

    }
    public void startContest(){
        List<Allie> allieList = alliesManager.getClients();
        allieList.forEach(Allie::startBruteForce);
    }
    public BlockingQueue<AgentTaskConfigurationDTO> getBlockingQueueByAgentName(String agentUserName){
        Allie allie = getAllieByAgentName(agentUserName);
        return allie.getBlockingQueue();
    }
    synchronized public ContestInformationDTO getContestFromAllieByAgentName(String agentName) {
        Allie allie = getAllieByAgentName(agentName);
        if(!allie.isSigned())
            throw new RuntimeException("Error: allie is not signed to any contest");
        ContestInformationDTO  contestInformationDTO = mediator.getContestInformationByAllie(allie);
        if(contestInformationDTO == null)
            throw new RuntimeException("Error: allie contest is null");
        return contestInformationDTO;
    }
    public Allie getAllieByAgentName(String agentName){
        Agent agent = agentManager.getClientByName(agentName);
        return getAllieByName(agent.getChosenAlliesName());
    }
    public List<AlliesDetailDTO> getSignedAllies(String userName) {
        UBoat uBoat = getUBoatByName(userName);
        return mediator.getUBoatSignedAllies(uBoat);
    }

    public List<AlliesDetailDTO> getParticipantAlliesInContest(String allieName) {
        Allie allie = getAllieByName(allieName);
        return mediator.ParticipantAlliesInContest(allie);
    }

    public List<AlliesDetailDTO> getAllAllies() {
       return alliesManager.getClients()
               .stream()
               .map(Allie::getAllieDetailDTO).
               collect(Collectors.toList());
    }

    public void addUserByType(ClientUser clientUser){
        userManager.addClient(clientUser);
        String clientType = clientUser.getClientType().name();
        switch (clientType){
            case "UBOAT":
                addUBoat(new UBoat(clientUser.getUserName()));
                break;
            case "ALLIE":
                addAllies(new Allie(clientUser.getUserName()));
                break;
        }
    }
    synchronized public void addAgentAncdCheckContestStatus(AgentInfoDTO agentDTO) {
        Agent agent = new Agent(agentDTO);
        agent.setMediator(mediator);
        agentManager.addClient(agent);
        Allie allie = getAllieByName(agent.getChosenAlliesName());
        mediator.addAgentToAllie(agent,allie);

    }
    public boolean isAgentNeedToBeActive(AgentInfoDTO agentDTO){
        Allie allie = getAllieByName(agentDTO.getAllieName());
        return allie.areInContest();
    }
    public List<ContestInformationDTO> getContestInformation(){
        List<UBoat> uBoatList = UBoatManager.getClients();
        List<ContestInformationDTO> contestInformationDTOS;
        synchronized (UBoatManager) {
            contestInformationDTOS = uBoatList.stream().
                    filter(u -> u.isActive())
                    .map(UBoat::getContestInformationDTO)
                    .collect(Collectors.toList());
        }
        return contestInformationDTOS;
    }
    public void makeClientReady(String usernameFromSession) {
        ClientType clientType = getTypeByName(usernameFromSession);
        switch (clientType){
            case UBOAT:
                UBoat UBoat = getUBoatByName(usernameFromSession);
                UBoat.makeUBoatReady();


        }

    }
    public ClientType getTypeByName(String username) {
         List<ClientUser> clientUsers = userManager.getClients();

        for (ClientUser clientUser: clientUsers) {
            if(clientUser.getUserName().equals(username)){
                return clientUser.getClientType();
            }
        }
        return null;
    }
    public synchronized ApiEnigma getApiFromUBoat(String UBoatName){

        UBoat uBoat = getUBoatByName(UBoatName);
        if(uBoat != null)
            return uBoat.getApi();
        else
          return null;
    }

    // TODO: 10/15/2022 make it work with interface and client interface
    public synchronized UBoat getUBoatByName(String UBoatName){
       return UBoatManager.getClientByName(UBoatName);

    }
    public Allie getAllieByName(String allieName) {
        return alliesManager.getClientByName(allieName);
    }

    public GenericManager<ClientUser> getUserManager(){
        return userManager;
    }

    public void addUBoat(UBoat uboat){
        uboat.setMediator(mediator);
        UBoatManager.addClient(uboat);
    }
    public void addAllies(Allie allie){
        allie.setMediator(mediator);
        alliesManager.addClient(allie);
    }

    public void addBattleField(Battlefield battlefield){
        battlefieldManager.addClient(battlefield);
    }
    public void setChosenAllieContest(ContestInformationDTO chosenContestDTO, String AllieName) {
        Allie allie = alliesManager.getClientByName(AllieName);
        String uBoatName = chosenContestDTO.getUBoatName();
        UBoat uBoat = getUBoatByName(uBoatName);
        try {
            mediator.addContestToAllie(allie, uBoat, chosenContestDTO);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }
}
