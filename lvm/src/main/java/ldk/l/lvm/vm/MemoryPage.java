package ldk.l.lvm.vm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

public final class MemoryPage {
    public static final int MP_READ = 1;
    public static final int MP_WRITE = 1 << 1;
    public static final int MP_EXEC = 1 << 2;
    public static final int MP_PRESENT = 1 << 3;

    public Arena arena;
    public MemorySegment data;
    public int flags;
    public long refCount;

    public MemoryPage(int flags) {
        this.flags = flags;
    }

    public synchronized void initialize() {
        if ((this.flags & MP_PRESENT) != 0) return;
        this.arena = Arena.ofAuto();
        this.data = this.arena.allocate(Memory.PAGE_SIZE);
        this.flags |= MP_PRESENT;
    }

    public synchronized void retain() {
        refCount++;
    }

    public synchronized void release() {
        refCount--;
        if (refCount == 0) {
            destroy();
        }
    }

    private void destroy() {
        this.arena = null;
        this.data = null;
        this.flags &= ~MP_PRESENT;
    }

    public byte getByte(long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_READ) == 0) throw new RuntimeException("Page is not readable");
        return this.data.get(Memory.LAYOUT_BYTE, offset);
    }

    public short getShort(long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_READ) == 0) throw new RuntimeException("Page is not readable");
        return this.data.get(Memory.LAYOUT_SHORT, offset);
    }

    public int getInt(long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_READ) == 0) throw new RuntimeException("Page is not readable");
        return this.data.get(Memory.LAYOUT_INT, offset);
    }

    public long getLong(long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_READ) == 0) throw new RuntimeException("Page is not readable");
        return this.data.get(Memory.LAYOUT_LONG, offset);
    }

    public float getFloat(long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_READ) == 0) throw new RuntimeException("Page is not readable");
        return this.data.get(Memory.LAYOUT_FLOAT, offset);
    }

    public double getDouble(long offset) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_READ) == 0) throw new RuntimeException("Page is not readable");
        return this.data.get(Memory.LAYOUT_DOUBLE, offset);
    }

    public void setByte(long offset, byte b) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_WRITE) == 0) throw new RuntimeException("Page is not writable");
        this.data.set(Memory.LAYOUT_BYTE, offset, b);
    }

    public void setShort(long offset, short s) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_WRITE) == 0) throw new RuntimeException("Page is not writable");
        this.data.set(Memory.LAYOUT_SHORT, offset, s);
    }

    public void setInt(long offset, int i) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_WRITE) == 0) throw new RuntimeException("Page is not writable");
        this.data.set(Memory.LAYOUT_INT, offset, i);
    }

    public void setLong(long offset, long i) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_WRITE) == 0) throw new RuntimeException("Page is not writable");
        this.data.set(Memory.LAYOUT_LONG, offset, i);
    }

    public void setFloat(long offset, float f) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_WRITE) == 0) throw new RuntimeException("Page is not writable");
        this.data.set(Memory.LAYOUT_FLOAT, offset, f);
    }

    public void setDouble(long offset, double d) {
        if ((this.flags & MP_PRESENT) == 0) initialize();
        if ((this.flags & MP_WRITE) == 0) throw new RuntimeException("Page is not writable");
        this.data.set(Memory.LAYOUT_DOUBLE, offset, d);
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