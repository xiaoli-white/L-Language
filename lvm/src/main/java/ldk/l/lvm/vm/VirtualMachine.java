package ldk.l.lvm.vm;

import ldk.l.lvm.module.Module;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class VirtualMachine {
    public static final long LVM_VERSION = 0;

    public final long stackSize;
    public final Memory memory;
    public final Map<Long, ThreadHandle> threadID2Handle = new HashMap<>();
    public final Map<Long, FileHandle> fd2FileHandle = new HashMap<>();
    public long entryPoint = 0;
    private boolean running = false;
    private long lastThreadID;
    private long lastFd;

    public VirtualMachine(long stackSize) {
        this.stackSize = stackSize;
        this.memory = new Memory();
    }

    public int init(Module module) {
        this.memory.init(module.text(), module.rodata(), module.data(), module.bssSectionLength());
        this.entryPoint = module.entryPoint();

        fd2FileHandle.put(0L, new FileHandle("<stdin>", FileHandle.FH_READ, 0, System.in, null));
        fd2FileHandle.put(1L, new FileHandle("<stdout>", FileHandle.FH_WRITE, 0, null, System.out));
        fd2FileHandle.put(2L, new FileHandle("<stderr>", FileHandle.FH_WRITE, 0, null, System.err));
        lastFd = 2;

        return 0;
    }

    public int run() {
        createThread(this.entryPoint);
        running = true;
        while (running && !threadID2Handle.isEmpty()) {
            try {
                threadID2Handle.values().stream().toList().getFirst().thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    public long createThread(long entryPoint) {
        long threadID = getThreadID();
        ExecutionUnit executionUnit = createExecutionUnit(threadID, entryPoint);
        ThreadHandle threadHandle = new ThreadHandle(executionUnit);
        threadID2Handle.put(threadID, threadHandle);
        threadHandle.thread.start();
        return threadID;
    }

    private ExecutionUnit createExecutionUnit(long threadID, long entryPoint) {
        ExecutionUnit executionUnit = new ExecutionUnit(this);
        long stackStart = memory.allocateMemory(this.stackSize);
        executionUnit.init(threadID, stackStart + this.stackSize - 1, entryPoint);
        return executionUnit;
    }

    public int open(String path, int flags, int mode) throws FileNotFoundException {
        long fd = getFd();
        fd2FileHandle.put(fd, new FileHandle(path, flags, mode));
        return -1;
    }

    public synchronized int close(long fd) {
        FileHandle fileHandle = fd2FileHandle.remove(fd);
        fileHandle.close();
        lastFd = 2;
        return 0;
    }

    public int read(long fd, byte[] buffer, int count) throws IOException {
        FileHandle fileHandle = fd2FileHandle.get(fd);
        if (fileHandle == null) {
            throw new IOException("Invalid file descriptor: " + fd);
        }
        return fileHandle.read(buffer, count);
    }

    public int write(long fd, byte[] buffer) throws IOException {
        FileHandle fileHandle = fd2FileHandle.get(fd);
        if (fileHandle == null) {
            throw new IOException("Invalid file descriptor: " + fd);
        }
        fileHandle.write(buffer);
        return buffer.length;
    }

    public void exit(long status) {
        running = false;
        // TODO
    }

    private synchronized long getThreadID() {
        long threadID = lastThreadID + 1;
        while (fd2FileHandle.containsKey(threadID)) threadID++;
        lastThreadID = threadID;
        return threadID;
    }

    private synchronized long getFd() {
        long fd = lastFd + 1;
        while (fd2FileHandle.containsKey(fd)) fd++;
        lastFd = fd;
        return fd;
    }
}