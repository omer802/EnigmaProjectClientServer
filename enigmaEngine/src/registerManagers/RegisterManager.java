package registerManagers;

import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import engine.api.ApiEnigma;
import registerManagers.battleField.Battlefield;
import registerManagers.UBoatManager.UBoat;
import registerManagers.agentManager.Agent;
import registerManagers.alliesManager.Allies;
import registerManagers.genericManager.GenericManager;
import registerManagers.usersManager.ClientUser;

import java.util.List;
import java.util.stream.Collectors;

public class RegisterManager {

    public static enum ClientType {
        UBOAT, ALLIES, AGENT
    }
    private GenericManager<ClientUser> userManager;
    private GenericManager<Battlefield> battlefieldManager;
    private GenericManager<UBoat> UBoatManager;
    private GenericManager<Allies> alliesManager;
    private GenericManager<Agent> agentManager;


    public RegisterManager() {
        userManager = new GenericManager<>();
        battlefieldManager = new GenericManager<>();
        UBoatManager = new GenericManager<>();
        alliesManager = new GenericManager<>();
        agentManager = new GenericManager<>();
    }
    public void addUserByType(ClientUser clientUser){
        userManager.addClient(clientUser);
        String clientType = clientUser.getClientType().name();
        switch (clientType){
            case "UBOAT":
                addUBoat(new UBoat(clientUser.getUsername()));
                break;
            case "ALLIES":
                addAllies(new Allies(clientUser.getUsername()));
                break;
            case "AGENT":
                //
                break;
        }
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
            if(clientUser.getUsername().equals(username)){
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
        List<UBoat> uBoatList = UBoatManager.getClients();
        for (UBoat uBoat: uBoatList) {
            if(uBoat.getUBoatName().equals(UBoatName))
                return uBoat;
        }
        return null;
    }

    public GenericManager<ClientUser> getUserManager(){
        return userManager;
    }

    public void addUBoat(UBoat uboat){
        UBoatManager.addClient(uboat);
    }
    public void addAllies(Allies allies){
        alliesManager.addClient(allies);
    }

    public void addBattleField(Battlefield battlefield){
        battlefieldManager.addClient(battlefield);
    }
}
