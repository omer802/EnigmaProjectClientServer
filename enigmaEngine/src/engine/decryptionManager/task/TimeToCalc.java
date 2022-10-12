package engine.decryptionManager.task;

import java.util.concurrent.atomic.AtomicLong;

public class TimeToCalc {
    private volatile long totalTimeTaskManager;


    private double averageMissionTime;
    private long startingTimeTaskManager;
    private int missionSize;



    private volatile AtomicLong  totalMissionAmount;

    public TimeToCalc(long startingTimeTaskManager) {
        this.totalMissionAmount = new AtomicLong(1);
        this.totalTimeTaskManager = 0;
        this.averageMissionTime = 0;
        this.startingTimeTaskManager = startingTimeTaskManager;
    }

    public long getTotalTimeTaskManager() {
        return totalTimeTaskManager;
    }

    public void setTotalTimeTaskManager(long endTime) {
        System.out.println("set total time now*********************");
        this.totalTimeTaskManager  = (endTime - startingTimeTaskManager);
    }

    public double getAverageMissionTime() {
        return averageMissionTime;
    }

    public void addTimeToAverageMissionTime(long MissionTime) {
        this.averageMissionTime = (averageMissionTime * totalMissionAmount.get())/ totalMissionAmount.incrementAndGet();
        this.averageMissionTime = ((double) averageMissionTime + MissionTime/(totalMissionAmount.get()));
    }

    public long getStartingTimeTaskManager() {
        return startingTimeTaskManager;
    }

    public void setStartingTimeTaskManager(long startingTimeTaskManager) {
        this.startingTimeTaskManager = startingTimeTaskManager;
    }
    public AtomicLong getTotalMissionAmount() {
        return totalMissionAmount;
    }

    public void updateTotalTasksTime() {
        long now = System.currentTimeMillis();
        this.totalTimeTaskManager  = now - startingTimeTaskManager;
    }
}
