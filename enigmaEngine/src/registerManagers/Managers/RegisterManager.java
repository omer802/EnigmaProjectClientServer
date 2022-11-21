package registerManagers.Managers;

import DTOS.AllieInformationDTO.AgentAndDMProgressDTO;
import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.AllieInformationDTO.DMProgressDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.agentInformationDTO.AgentProgressDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import DTOS.enigmaComponentContainers.AgentTaskConfigurationDTO;
import DTOS.enigmaComponentContainers.ConfigurationForAgentBruteForceDTO;
import dictionary.Dictionary;
import engine.api.ApiEnigma;
import engine.enigma.Machine.EnigmaMachine;
import registerManagers.battlefieldManager.Battlefield;
import registerManagers.clients.Allie;
import registerManagers.clients.UBoat;
import registerManagers.clients.Agent;
import registerManagers.mediators.Mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RegisterManager {

    public List<AgentInfoDTO> getSingedAgentToAllieByAllieName(String allieName) {
        Allie allie = getAllieByName(allieName);
        List<AgentInfoDTO> agentInfoDTOList = mediator.getAgentsSignedToAllieDTOList(allie);
        if(agentInfoDTOList == null)
            agentInfoDTOList = new ArrayList<>();
        return agentInfoDTOList;
    }

    public void addCandidateFromAgent(String agentUser, List<CandidateDTO> candidatesDTO) {
        Agent agent = agentManager.getClientByName(agentUser);
        mediator.addCandidatesFromAgent(agent,candidatesDTO);
    }

    public List<CandidateDTO> getAllieCandidates(String allieUsername) {
        Allie allie = alliesManager.getClientByName(allieUsername);
        List<CandidateDTO> candidateList = allie.getCandidateList();
        if(candidateList==null)
            candidateList =  new ArrayList<>();
       return candidateList;
    }

    public List<CandidateDTO> getUBoatCandidates(String UBoatUsername) {
        UBoat uBoat = UBoatManager.getClientByName(UBoatUsername);
        List<CandidateDTO> candidateList = uBoat.getCandidateList();
        if(candidateList==null)
            candidateList = new ArrayList<>();
        return candidateList;
    }

    synchronized public Allie.AllieStatus getContestStatusByAgent(String usernameFromSession) {
        Allie allie = getAllieByAgentName(usernameFromSession);
        return allie.getAllieStatus();
    }

    public void updateAgentProgress(String agentUsername, AgentProgressDTO agentProgressDTO) {
        Agent agent = agentManager.getClientByName(agentUsername);
        agent.updateProgress(agentProgressDTO);
    }
    public List<AgentProgressDTO> pullAgentsProgress(){
        return null;
    }

    public AgentAndDMProgressDTO getAgentsAndDMProgressFromAllie(String allieUsername) {
        Allie allie = alliesManager.getClientByName(allieUsername);
        List<AgentProgressDTO> agentProgressDTOS = mediator.getAgentsProgressAtAllie(allie);
        long totalFinishMissionsAmount = agentProgressDTOS.stream().mapToLong(AgentProgressDTO::getCompletedMissionsAmount).sum();
        DMProgressDTO dmProgressDTO = allie.getDMProgress();
        dmProgressDTO.setFinishedMissionsByAgents(totalFinishMissionsAmount);
        AgentAndDMProgressDTO agentAndDMProgressDTO = new AgentAndDMProgressDTO(dmProgressDTO,agentProgressDTOS);
        agentAndDMProgressDTO.setStringToHack(allie.getStringToDecode());
        return agentAndDMProgressDTO;

    }

    public void finishContestInAllieByAgentName(String agentUserName) {
        Allie allie = getAllieByAgentName(agentUserName);
        allie.terminateContest();
    }

    public UBoat.GameStatus getContestStatusByUBoat(String uBoatUsername) {
        UBoat uBoat = getUBoatByName(uBoatUsername);
        return uBoat.getContestStatus();
    }

    public Allie.AllieStatus getContestStatusByAllie(String  allieUserName) {
        Allie allie = getAllieByName(allieUserName);
        return allie.getAllieStatus();
    }
    synchronized public void signoutAllieFromContest(Allie allie){
        mediator.removeAllieFromContest(allie);
    }

    synchronized public void signoutAllieFromContestAndRestDM(String allieName) {
        Allie allie = getAllieByName(allieName);
        signoutAllieFromContest(allie);
        allie.restDM();
        mediator.notifyWaitingAgentsSignedToALlie(allie);
        //mediator.restAgentsSignedToAllie(allie);
    }

    public void restUBoatAfterContest(String uBoatName) {
        UBoat uBoat = getUBoatByName(uBoatName);
        uBoat.initUBoatForNewContest();
    }

    public Battlefield signoutUBoat(String uBoatName) {
        UBoat uBoat = getUBoatByName(uBoatName);
        Battlefield battlefield = uBoat.getBattlefield();
        uBoat.inLogoutMode();
        if(uBoat.getAlliesSignedAmount()==0){
            removeUBoatFromManagers(uBoat);
            return battlefield;
        }
        else{
            mediator.activateSignoutModeForEachAllieSignedToUBoat(uBoat);
            return null;
        }
    }

    public Battlefield signoutAllieFromContestAfterUBoatLogout(String allieName) {
        Allie allie = alliesManager.getClientByName(allieName);
        UBoat uBoat = mediator.getUBoatByAllie(allie);
        Battlefield battlefield = uBoat.getBattlefield();
        signoutAllieFromContest(allie);
        allie.cancelUBoatLogoutAction();
        if(uBoat.getAlliesSignedAmount() == 0){
            removeUBoatFromManagers(uBoat);
            return battlefield;
        }
        return null;


    }
    synchronized public void removeUBoatFromManagers(UBoat uBoat){
        Battlefield battlefield = uBoat.getBattlefield();
        battlefieldManager.removeClient(battlefield);
        ClientUser uBoatUser = userManager.getClientByName(uBoat.getUserName());
        userManager.removeClient(uBoatUser);
        UBoatManager.removeClient(uBoat);
    }

     public boolean checkIfUBoatLogOut(String allieName) {
        Allie allie = getAllieByName(allieName);
        return allie.isSignoutFromUBoatAction();
    }

    synchronized public UBoat getUBoatByAgentName(String agentUserName) {
        Allie allie = getAllieByAgentName(agentUserName);
        return mediator.getUBoatByAllie(allie);
    }

    public boolean isAgentWaiting(String usernameFromSession) {
        Agent agent = agentManager.getClientByName(usernameFromSession);
        return agent.isWaiting();
    }


    // TODO: 10/20/2022 create dm and make it work with this


    public enum ClientType {
        UBOAT, ALLIE, AGENT
    }

    synchronized public Mediator getMediator() {
        return mediator;
    }

    private final Mediator mediator;
    private final GenericManager<ClientUser> userManager;
    private final GenericManager<Battlefield> battlefieldManager;
    private final GenericManager<UBoat> UBoatManager;
    private final GenericManager<Allie> alliesManager;
    private final GenericManager<Agent> agentManager;

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
        //mediator.startContest();
        Agent agent = agentManager.getClientByName(agentUserName);
        Allie allie = getAllieByAgentName(agentUserName);
        EnigmaMachine enigmaMachine = allie.getDm().getMachine().clone();
        if(enigmaMachine == null){
            throw new RuntimeException("Machine is not config at agent");
        }
        Dictionary dictionary = agent.getDictionary();
        if(dictionary == null){
            throw new RuntimeException("Dictionary is not config at agent");
        }
        //startContest();
        return new ConfigurationForAgentBruteForceDTO(enigmaMachine,dictionary);

    }
  /*  public void startContest(){
        List<Allie> allieList = alliesManager.getClients();
        allieList.forEach(Allie::startBruteForce);
    }*/
    public BlockingQueue<AgentTaskConfigurationDTO> getBlockingQueueByAgentName(String agentUserName){
        Allie allie = getAllieByAgentName(agentUserName);
        return allie.getBlockingQueue();
    }

    synchronized public ContestInformationDTO getContestFromAllieByAgentName(String agentName) {
        Allie allie = getAllieByAgentName(agentName);
        if(!allie.isSigned())
            throw new RuntimeException("allie is not signed to any contest");
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
        return mediator.getUBoatSignedAlliesDTO(uBoat);
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
    synchronized public void addAgentAndCheckContestStatus(AgentInfoDTO agentDTO, ClientUser clientUser) {
        userManager.addClient(clientUser);
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
    public List<ContestInformationDTO> getContestInformation() {
        List<UBoat> uBoatList = UBoatManager.getClients();
        if (uBoatList.size() > 0) {
            List<ContestInformationDTO> contestInformationDTOS;
            synchronized (UBoatManager) {
                contestInformationDTOS = uBoatList.stream().
                        filter(u -> u.isActive() && (!u.isLogoutMode()))
                        .map(UBoat::getContestInformationDTO)
                        .collect(Collectors.toList());
            }
            return contestInformationDTOS;
        }
        else{
            return new ArrayList<>();
        }
    }
    synchronized public void makeClientReady(String usernameFromSession) {
        ClientType clientType = getTypeByName(usernameFromSession);
        switch (clientType) {
            case UBOAT:
                UBoat UBoat = getUBoatByName(usernameFromSession);
                UBoat.makeUBoatReady();
                if (UBoat.isAlliesCapacityFull())
                    mediator.startContestIfAllAlliesAndUBoatsReady(UBoat);
                break;
            case ALLIE:
                Allie allie = getAllieByName(usernameFromSession);
                if (allie.canBeReady()) {
                    allie.makeAllieReady();
                    if (allie.isSigned()) {
                        mediator.startContestIfAllAlliesAndUBoatsReady(allie);
                    }
                }
                break;
        }
    }
    public void startContestIfAllAlliesAndUBoatsReady(){
        /*if(needToStartContest()){
            mediator.startContest();
            startContest();
        }*/
    }

    /*public boolean needToStartContest() {
        List<UBoat> uBoatList = UBoatManager.getClients();
        for (UBoat uBoat: uBoatList) {
            if(!uBoat.canStartContest())
                return false;
        }
        List<Allie> allieList = alliesManager.getClients();
        for (Allie allie: allieList) {
            if(allie.isReady())
                return false;
        }
        return true;
    }*/

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
    synchronized public CandidateDTO getWinnerByClientType(String username, ClientType clientType) {
        CandidateDTO winner = null;
        switch (clientType){
            case UBOAT:
                UBoat uBoat = getUBoatByName(username);
                winner = uBoat.getWinner();
                break;
            case ALLIE:
                Allie allie = getAllieByName(username);
               // winner = mediator.getWinnerByAllie(allie);
                winner = allie.getWinner();
                break;
            case AGENT:
                Agent agent = agentManager.getClientByName(username);
                winner = mediator.getWinnerByAgent(agent);

        }
        return winner;


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

    public void updateMissionSizeByAllieName(String usernameFromSession, int missionSize) {
        Allie allie = getAllieByName(usernameFromSession);
        allie.setMissionSize(missionSize);

    }
    public AtomicLong getTotalTaskAmount(String agentName){
        Allie allie = getAllieByAgentName(agentName);
        return allie.getPulledMissionsAmount();
    }
}
