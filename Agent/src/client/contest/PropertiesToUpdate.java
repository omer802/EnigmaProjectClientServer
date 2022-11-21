package client.contest;

import DTOS.agentInformationDTO.AgentProgressDTO;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;

public class PropertiesToUpdate {
    private final SimpleIntegerProperty missionInQueue;
    private final SimpleLongProperty missionFromServer;
    private final SimpleLongProperty completedMissions;

    private final SimpleIntegerProperty candidates;
    private final SimpleStringProperty agentName;


    public PropertiesToUpdate() {
        this.missionInQueue    = new SimpleIntegerProperty(0);
        this.missionFromServer = new SimpleLongProperty(0);
        this.completedMissions = new SimpleLongProperty(0);
        this.candidates        = new SimpleIntegerProperty(0);
        this.agentName         = new SimpleStringProperty();
    }
    public void addOneToCompletedMissions(){
        Platform.runLater(()-> completedMissions.setValue(completedMissions.getValue()+1));
    }
    public void addOneToCandidateAmount(){
        Platform.runLater(()->candidates.setValue(candidates.getValue()+1));
    }

     public void setMissionInQueue(int missionInQueue) {
        Platform.runLater(()->this.missionInQueue.set(missionInQueue));
    }

    public long getMissionFromServer() {
        return missionFromServer.get();
    }


     public void setMissionFromServer(long missionFromServer) {
        Platform.runLater(()->this.missionFromServer.set(missionFromServer));
    }
     public void addAmountToMissionFromServer(long missionsFromServer){
        Platform.runLater(()-> this.missionFromServer.setValue(this.missionFromServer.getValue()+ missionsFromServer));
    }
     public void setCompletedMissions(long completedMissions) {
        Platform.runLater(()->this.completedMissions.set(completedMissions));
    }

     public int getCandidates() {
        return candidates.get();
    }


     public void setCandidates(int candidates) {
        Platform.runLater(()->this.candidates.set(candidates));
    }

    public void bindUIComponent(Label amountOfMissionsInQueueLabel, Label missionFromServerAmountLabel,
                                Label completedMissionAmountLabel, Label candidateAmountLabel, SimpleStringProperty userName) {
        amountOfMissionsInQueueLabel.textProperty().bind(Bindings.format("%,d", missionInQueue));
        missionFromServerAmountLabel.textProperty().bind(Bindings.format("%,d", missionFromServer));
        completedMissionAmountLabel.textProperty().bind(Bindings.format("%,d",completedMissions));
        candidateAmountLabel.textProperty().bind(Bindings.format("%,d",candidates));
        this.agentName.bind(userName);
    }
    public AgentProgressDTO createAgentProgressDTO(){
        return new AgentProgressDTO(agentName.getValue(),
                            getMissionFromServer(),
                            missionInQueue.getValue(),
                                getCandidates(),completedMissions.getValue());
    }
}
