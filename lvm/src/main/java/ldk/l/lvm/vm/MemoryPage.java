package ldk.l.lvm.vm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public final class MemoryPage {
    public static final int MP_READ = 1;
    public static final int MP_WRITE = 1 << 1;
    public static final int MP_EXEC = 1 << 2;
    public static final int MP_PRESENT = 1 << 3;

    public final long start;
    public Arena arena;
    public MemorySegment data;
    public int flags;
    public long referenceCount;

    public MemoryPage(long start, int flags) {
        this.start = start;
        this.flags = flags;
    }

    public synchronized void initialize() {
        if ((this.flags & MP_PRESENT) != 0) return;
        this.arena = Arena.ofAuto();
        this.data = this.arena.allocate(Memory.PAGE_SIZE);
        this.flags |= MP_PRESENT;
    }

    public synchronized void retain() {
        referenceCount++;
    }

    public synchronized void release() {
        referenceCount--;
        if (referenceCount == 0) {
            destroy();
        }
    }

    private void destroy() {
        this.arena = null;
        this.data = null;
        this.flags &= ~MP_PRESENT;
    }

    public byte getByte(ThreadHandle threadHandle, long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkReadable(threadHandle, offset))
            return this.data.get(Memory.LAYOUT_BYTE, offset);
        else
            return 0;
    }

    public short getShort(ThreadHandle threadHandle, long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkReadable(threadHandle, offset))
            return this.data.get(Memory.LAYOUT_SHORT, offset);
        else
            return 0;
    }

    public int getInt(ThreadHandle threadHandle, long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkReadable(threadHandle, offset))
            return this.data.get(Memory.LAYOUT_INT, offset);
        else
            return 0;
    }

    public long getLong(ThreadHandle threadHandle, long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkReadable(threadHandle, offset))
            return this.data.get(Memory.LAYOUT_LONG, offset);
        else
            return 0;
    }

    public float getFloat(ThreadHandle threadHandle, long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkReadable(threadHandle, offset))
            return this.data.get(Memory.LAYOUT_FLOAT, offset);
        else
            return 0;
    }

    public double getDouble(ThreadHandle threadHandle, long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkReadable(threadHandle, offset))
            return this.data.get(Memory.LAYOUT_DOUBLE, offset);
        else
            return 0;
    }

    public void setByte(ThreadHandle threadHandle, long offset, byte b) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkWritable(threadHandle, offset))
            this.data.set(Memory.LAYOUT_BYTE, offset, b);
    }

    public void setShort(ThreadHandle threadHandle, long offset, short s) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkWritable(threadHandle, offset))
            this.data.set(Memory.LAYOUT_SHORT, offset, s);
    }

    public void setInt(ThreadHandle threadHandle, long offset, int i) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkWritable(threadHandle, offset))
            this.data.set(Memory.LAYOUT_INT, offset, i);
    }

    public void setLong(ThreadHandle threadHandle, long offset, long i) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkWritable(threadHandle, offset))
            this.data.set(Memory.LAYOUT_LONG, offset, i);
    }

    public void setFloat(ThreadHandle threadHandle, long offset, float f) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkWritable(threadHandle, offset))
            this.data.set(Memory.LAYOUT_FLOAT, offset, f);
    }

    public void setDouble(ThreadHandle threadHandle, long offset, double d) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if (checkWritable(threadHandle, offset))
            this.data.set(Memory.LAYOUT_DOUBLE, offset, d);
    }

    public boolean checkReadable(ThreadHandle threadHandle, long offset) {
        boolean readable = (this.flags & MP_READ) != 0;
        if (!readable) {
            if (threadHandle != null) {
                ExecutionUnit executionUnit = threadHandle.executionUnit;
                executionUnit.setRegister(ByteCode.FLAGS_REGISTER, executionUnit.getRegister(ByteCode.FLAGS_REGISTER) | ByteCode.PAGE_NOT_READABLE);
                executionUnit.interrupt(null, ByteCode.INTERRUPT_PAGE_ERROR);
            } else {
                throw new RuntimeException("Page is not readable(address: " + (start + offset) + ")");
            }
        }
        return readable;
    }

    public boolean checkWritable(ThreadHandle threadHandle, long offset) {
        boolean writable = (this.flags & MP_WRITE) != 0;
        if (!writable) {
            if (threadHandle != null) {
                ExecutionUnit executionUnit = threadHandle.executionUnit;
                executionUnit.setRegister(ByteCode.FLAGS_REGISTER, executionUnit.getRegister(ByteCode.FLAGS_REGISTER) | ByteCode.PAGE_NOT_WRITABLE);
                executionUnit.interrupt(null, ByteCode.INTERRUPT_PAGE_ERROR);
            } else {
                throw new RuntimeException("Page is not writable(address: " + (start + offset) + ")");
            }
        }
        return writable;
    }

    public boolean checkExecutable(ThreadHandle threadHandle, long offset) {
        boolean executable = (this.flags & MP_EXEC) != 0;
        if (!executable) {
            if (threadHandle != null) {
                ExecutionUnit executionUnit = threadHandle.executionUnit;
                executionUnit.setRegister(ByteCode.FLAGS_REGISTER, executionUnit.getRegister(ByteCode.FLAGS_REGISTER) | ByteCode.PAGE_NOT_EXECUTABLE);
                executionUnit.interrupt(null, ByteCode.INTERRUPT_PAGE_ERROR);
            } else {
                throw new RuntimeException("Page is not executable(address: " + (start + offset) + ")");
            }
        }
        return executable;
    }

    public static final class FreeMemory {
        public long start;
        public long end;
        public FreeMemory next;

        public FreeMemory(long start, long end) {
            this.start = start;
            this.end = end;
            this.next = null;
        }
    }
}