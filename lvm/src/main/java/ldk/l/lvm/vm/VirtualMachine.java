package ldk.l.lvm.vm;

import ldk.l.lvm.module.Module;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VirtualMachine {
    public static final long LVM_VERSION = 0;

    public final long stackSize;
    public final Memory memory;
    public final List<ExecutionUnit> executionUnits = new LinkedList<>();
    public final Map<Integer, InputStream> fd2InputStream = new HashMap<>();
    public final Map<Integer, PrintStream> fd2PrintStream = new HashMap<>();
    public long entryPoint = 0;

    public VirtualMachine(long stackSize) {
        this.stackSize = stackSize;
        this.memory = new Memory();
    }

    public int init(Module module) {
        this.memory.init(module.text(), module.rodata(), module.data(), module.bssSectionLength());
        this.entryPoint = module.entryPoint();

        return 0;
    }

    public int run() {
        ExecutionUnit executionUnit = initThread(this.entryPoint);
        Thread thread = new Thread(executionUnit, "ExecutionThread");
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        return 0;
    }

    public ExecutionUnit initThread(long entryPoint) {
        ExecutionUnit executionUnit = new ExecutionUnit(this);
        long stackStart = memory.allocateMemory(this.stackSize);
        executionUnit.init(stackStart + stackSize-1, entryPoint);
        return executionUnit;
    }

    public int open(String path, int flags, int mode) throws FileNotFoundException {
        // TODO support flags and mode
        int fd = getFd();
        fd2InputStream.put(fd, new FileInputStream(path));
        fd2PrintStream.put(fd, new PrintStream(new FileOutputStream(path)));
        return -1;
    }

    public int close(int fd) {
        if (fd == 0 || fd == 1 || fd == 2) {
            throw new RuntimeException("Invalid file descriptor");
        } else {
            try {
                fd2InputStream.remove(fd).close();
                fd2PrintStream.remove(fd).close();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    public int read(int fd, byte[] buffer, int count) throws IOException {
        InputStream fis;
        if (fd == 0) {
            fis = System.in;
        } else if (fd2InputStream.containsKey(fd)) {
            fis = fd2InputStream.get(fd);
        } else {
            throw new IOException("Invalid file descriptor: " + fd);
        }
        return fis.read(buffer, 0, count);
    }

    public int write(int fd, byte[] buffer) throws IOException {
        PrintStream fos;
        if (fd == 1) {
            fos = System.out;
        } else if (fd == 2) {
            fos = System.err;
        } else if (fd2PrintStream.containsKey(fd)) {
            fos = fd2PrintStream.get(fd);
        } else {
            throw new IOException("Invalid file descriptor: " + fd);
        }
        fos.write(buffer);
        return buffer.length;
    }

    public void exit(long status) {
        // TODO exit
    }

    private synchronized int getFd() {
        int fd = 3;
        while (fd2InputStream.containsKey(fd)) fd++;
        return fd;
    }
}