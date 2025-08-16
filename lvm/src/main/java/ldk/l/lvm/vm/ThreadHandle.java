package ldk.l.lvm.vm;

public final class ThreadHandle {
    public final long threadID;
    public final ExecutionUnit executionUnit;
    public final Thread thread;
    public ThreadHandle(long threadID,ExecutionUnit executionUnit) {
        this.threadID = threadID;
        this.executionUnit = executionUnit;
        this.thread = new Thread(executionUnit);
    }
}
