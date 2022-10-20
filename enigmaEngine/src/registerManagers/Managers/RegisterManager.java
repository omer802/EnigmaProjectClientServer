package registerManagers.Managers;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import engine.api.ApiEnigma;
import registerManagers.battlefieldManager.Battlefield;
import registerManagers.clients.Allie;
import registerManagers.clients.UBoat;
import registerManagers.clients.Agent;
import registerManagers.mediators.Mediator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RegisterManager {




    public static enum ClientType {
        UBOAT, ALLIE, AGENT
    }

    public synchronized Mediator getMediator() {
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
        Random random = new Random();
        boolean what = random.nextBoolean();
        return what;
        //return allie.areInContest();
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
        synchronized (mediator) {
            String uBoatName = chosenContestDTO.getUBoatName();
            UBoat uBoat = getUBoatByName(uBoatName);
            mediator.addContestToAllie(allie, uBoat);
        }
    }
}
