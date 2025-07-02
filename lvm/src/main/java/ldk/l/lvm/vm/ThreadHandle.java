package ldk.l.lvm.vm;

public final class ThreadHandle {
    public final ExecutionUnit executionUnit;
    public final Thread thread;
    public ThreadHandle(ExecutionUnit executionUnit) {
        this.executionUnit = executionUnit;
        this.thread = new Thread(executionUnit);
    }
}
