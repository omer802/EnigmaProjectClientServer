package miniEngine.CustomThreadPool;

import engine.decryptionManager.task.AgentCandidatesList;
import miniEngine.MissionTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                    long keepAliveTime, TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,threadFactory);
    }



    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
        }
        //System.out.println("Perform afterExecute() logic");
        MissionTask task = (MissionTask)r;
        /*try {
            AgentCandidatesList candidatesList = task.getCandidatesList();
            if(!candidatesList.isEmpty()) {
            *//*    System.out.println(Thread.currentThread().getName());
                System.out.println("add to ququqe");*//*
                for (int i = 0; i <candidatesList.getCandidates().size() ; i++) {
                  //  System.out.println(candidatesList.getCandidates().get(i));
                }
                task.getCandidateBlockingQueue().put(candidatesList);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
