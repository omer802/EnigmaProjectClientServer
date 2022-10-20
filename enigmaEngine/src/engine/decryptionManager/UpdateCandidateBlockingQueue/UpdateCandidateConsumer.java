package engine.decryptionManager.UpdateCandidateBlockingQueue;

import engine.decryptionManager.task.AgentCandidatesList;

import java.util.concurrent.BlockingDeque;

public class UpdateCandidateConsumer implements Runnable {



    BlockingDeque<AgentCandidatesList> blockingDeque;

    public static void setRunning(boolean running) {
        UpdateCandidateConsumer.Running = running;
    }

    static boolean Running;
    public UpdateCandidateConsumer(BlockingDeque<AgentCandidatesList> blockingDeque){
        this.blockingDeque = blockingDeque;
        this.Running = true;

    }

    @Override
    public void run() {
        try {
            final String threadName = Thread.currentThread().getName();
            AgentCandidatesList agentCandidateList;
            while (Running) {
                agentCandidateList = blockingDeque.take();
                if(agentCandidateList.isPoisonPill()){
                    break;
                }
                /*synchronized (uiAdapter) {
                    // TODO: 10/7/2022 update candidate diffrently
                    //uiAdapter.AddCandidateStringForDecoding(agentCandidateList);
                }*/
            }

        } catch (InterruptedException e) {
            System.out.println("Was interrupted !");
        }
    }
    public void finish() {
        AgentCandidatesList agentCandidatesList = new AgentCandidatesList();
        agentCandidatesList.setPoisonPill();
        try {
            blockingDeque.put(agentCandidatesList);
        } catch (InterruptedException e) {
            System.out.println("problem at poison pill");
        }
        Running = false;

    }
}
