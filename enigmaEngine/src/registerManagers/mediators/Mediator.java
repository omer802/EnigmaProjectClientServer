package registerManagers.mediators;

import DTOS.AllieInformationDTO.AlliesDetailDTO;
import DTOS.UBoatsInformationDTO.ContestInformationDTO;
import DTOS.agentInformationDTO.AgentInfoDTO;
import DTOS.agentInformationDTO.AgentProgressDTO;
import DTOS.agentInformationDTO.CandidateDTO;
import engine.enigma.Machine.EnigmaMachine;
import registerManagers.clients.Agent;
import registerManagers.clients.Allie;
import registerManagers.clients.UBoat;

import java.util.*;
import java.util.stream.Collectors;

// TODO: 10/17/2022 think if makeing mediator implement interface
public class Mediator {
    private Map<UBoat, Set<Allie>> mapUBoatToAllies;
    private Map<Allie, UBoat> mapAlliesToUBoat;
    private Map<Allie, List<Agent>> mapAllieToAgents;
    private Map<Agent, Allie> mapAgentToAllie;

    public Mediator() {
        this.mapUBoatToAllies = new HashMap<>();
        this.mapAlliesToUBoat = new HashMap<>();
        this.mapAllieToAgents = new HashMap<>();
        this.mapAgentToAllie = new HashMap<>();
    }

    public synchronized void addContestToAllie(Allie allie, UBoat uBoat, ContestInformationDTO chosenContestDTO) throws CloneNotSupportedException {
        //add connection between chosen UBoat to allies list

        if (allie.isSigned()) {
            removeAllieFromContest(allie);
        }
        if (uBoat.isAlliesCapacityFull()) {
            throw new RuntimeException("the contest is already full");
        }
        Set<Allie> alliesList = mapUBoatToAllies.computeIfAbsent(uBoat, key -> new HashSet<>());
        alliesList.add(allie);
        allie.signInToContest(chosenContestDTO, uBoat.getApi().getDM().clone());
        uBoat.addOneToAlliesCounter();
        // add connection between allie to chosen UBoat
        mapAlliesToUBoat.put(allie, uBoat);

        //check
    }

    synchronized public void startContestUpdateConfigurations(UBoat uBoat) {
        EnigmaMachine enigmaMachine = uBoat.getApi().getMachine();
        Set<Allie> allieSet = mapUBoatToAllies.get(uBoat);
        String toDecode = uBoat.getEncryptedString();
        for (Allie allie : allieSet) {
            allie.setEncryptedString(toDecode);
            allie.getDm().setMachine(enigmaMachine.clone());
            updateConfigurationInAgent(allie);
        }
    }



    private void updateConfigurationInAgent(Allie allie) {
       List<Agent> agentList = mapAllieToAgents.get(allie);
       for(Agent agent : agentList){
           agent.setMachineAndDictionary(allie.getDm().getMachine().clone(), allie.getDm().getDictionary());

       }

    }

    private void removeAllieFromContest(Allie allie) {
        allie.signOutFromContest();
        UBoat uBoat = mapAlliesToUBoat.get(allie);
        uBoat.subOneFromAlliesCounter();
        Set<Allie> alliesList = mapUBoatToAllies.get(uBoat);
        alliesList.remove(allie);
        mapAlliesToUBoat.remove(allie);
    }


    public synchronized List<AlliesDetailDTO> getUBoatSignedAllies(UBoat uBoat) {
        if (uBoat.getAlliesSignedAmount() == 0) {
            // return empty list
            return new ArrayList<>();
        } else {
            Set<Allie> allieSet = mapUBoatToAllies.get(uBoat);
            return getAlliesListDTOFromSet(allieSet);
        }
    }
    public synchronized List<AlliesDetailDTO> getAlliesListDTOFromSet(Set<Allie> allieSet){
        List<AlliesDetailDTO> alliesDetailDTOList = allieSet.stream()
                .map(Allie::getAllieDetailDTO)
                .collect(Collectors.toList());
        return alliesDetailDTOList;
    }

    public synchronized List<AlliesDetailDTO> ParticipantAlliesInContest(Allie allie) {
        UBoat uBoat = mapAlliesToUBoat.get(allie);
        if(uBoat == null||uBoat.getAlliesSignedAmount()<2){
            return new ArrayList<>();
        }
        else{
            Set<Allie> allieSet = mapUBoatToAllies.get(uBoat);
            List<AlliesDetailDTO> alliesDetailDTOList = getAlliesListDTOFromSet(allieSet);
            alliesDetailDTOList.remove(allie.getAllieDetailDTO());
            return alliesDetailDTOList;
        }
    }

    synchronized public void addAgentToAllie(Agent agent, Allie allie) {

        List<Agent> agentList = mapAllieToAgents.computeIfAbsent(allie, key -> new ArrayList<>());
        agentList.add(agent);
        allie.addOneToAgentAmount();
        mapAgentToAllie.put(agent,allie);
    }
    synchronized public ContestInformationDTO getContestInformationByAllie(Allie allie){
        UBoat uBoat = mapAlliesToUBoat.get(allie);
        return uBoat.getContestInformationDTO();
    }

    synchronized public List<AgentInfoDTO> getAgentsSignedToAllieDTOList(Allie allie) {
        List<Agent> agentList = mapAllieToAgents.get(allie);
        if(agentList==null)
            return new ArrayList<>();
        else
            return agentList.stream().map(Agent::createAgentInfoDTO).collect(Collectors.toList());
    }

    synchronized public void addCandidatesFromAgent(Agent agent, List<CandidateDTO> candidatesListDTO) {
        Allie allie = mapAgentToAllie.get(agent);
        UBoat uBoat = mapAlliesToUBoat.get(allie);

            // add candidates to allie
            for (CandidateDTO candidateDTO : candidatesListDTO) {
                if(uBoat.isActiveContest()) {
                // add candidates to allie
                allie.addCandidate(agent, candidateDTO);
                //add candidates to UBoat
                uBoat.addCandidate(allie, candidateDTO);
                if (uBoat.isDecodingCorrect(candidateDTO)) {
                    uBoat.setWinner(candidateDTO);
                    uBoat.finishContest();
                    Set<Allie> allieSet = mapUBoatToAllies.get(uBoat);
                    allieSet.forEach(Allie::terminateContest);
                }
            }
        }
    }


    synchronized public void startContestIfAllAlliesAndUBoatsReady(UBoat uBoat) {
        Set<Allie> allieSet = mapUBoatToAllies.get(uBoat);
        if(allieSet.size() == allieSet.stream().filter(Allie::isReady).count()){
            startContestUpdateConfigurations(uBoat);
            allieSet.forEach(Allie::startBruteForce);
            uBoat.startContest();
        }
    }

    synchronized public void startContestIfAllAlliesAndUBoatsReady(Allie allie) {
        UBoat uBoat = mapAlliesToUBoat.get(allie);
        if (uBoat.isReady()&&uBoat.isAlliesCapacityFull()) {
            startContestIfAllAlliesAndUBoatsReady(uBoat);
        }
    }

    public List<AgentProgressDTO> getAgentsProgressAtAllie(Allie allie) {
        List<Agent> agentList = mapAllieToAgents.get(allie);
        List<AgentProgressDTO> agentProgressDTOS = agentList.stream().map(Agent::getProgressDTO).collect(Collectors.toList());
        return agentProgressDTOS;
    }

    public CandidateDTO getWinnerByAllie(Allie allie) {
        UBoat uBoat = mapAlliesToUBoat.get(allie);
        return uBoat.getWinner();
    }

    public CandidateDTO getWinnerByAgent(Agent agent) {
        Allie allie = mapAgentToAllie.get(agent);
        return getWinnerByAllie(allie);
    }
}
