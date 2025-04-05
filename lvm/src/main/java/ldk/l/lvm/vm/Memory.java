package ldk.l.lvm.vm;

import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public final class Memory {
    public static final ValueLayout.OfByte LAYOUT_BYTE = ValueLayout.JAVA_BYTE.withOrder(ByteOrder.LITTLE_ENDIAN);
    public static final ValueLayout.OfShort LAYOUT_SHORT = ValueLayout.JAVA_SHORT_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);
    public static final ValueLayout.OfInt LAYOUT_INT = ValueLayout.JAVA_INT_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);
    public static final ValueLayout.OfLong LAYOUT_LONG = ValueLayout.JAVA_LONG_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);
    public static final ValueLayout.OfFloat LAYOUT_FLOAT = ValueLayout.JAVA_FLOAT_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);
    public static final ValueLayout.OfDouble LAYOUT_DOUBLE = ValueLayout.JAVA_DOUBLE_UNALIGNED.withOrder(ByteOrder.LITTLE_ENDIAN);

    public static final long MAX_MEMORY_ADDRESS = 0x0000ffffffffffffL;
    public static final int PAGE_TABLE_SIZE = 512;
    public static final int PAGE_SIZE = 4096;
    public static final long PAGE_OFFSET_MASK = 0xfff;

    public MemoryPage[][][][] memoryPageTable = null;
    public MemoryPage.FreeMemory freeMemoryList = null;
    public final ReentrantLock lock = new ReentrantLock();

    public Memory() {
    }

    public void init(byte[] text, byte[] rodata, byte[] data, long bssSectionLength) {
        memoryPageTable = new MemoryPage[Memory.PAGE_TABLE_SIZE][][][];
        long address = 0;

        setMemoryPageIfAbsent(address, MemoryPage.MP_READ | MemoryPage.MP_EXEC | MemoryPage.MP_WRITE);
        MemoryPage currentPage = getMemoryPage(address);
        address += PAGE_SIZE;

        long offset = 0;
        for (byte b : text) {
            currentPage.setByte(offset, b);
            offset++;
            if (offset == PAGE_SIZE) {
                currentPage.flags &= ~MemoryPage.MP_WRITE;
                setMemoryPageIfAbsent(address, MemoryPage.MP_READ | MemoryPage.MP_EXEC | MemoryPage.MP_WRITE);
                currentPage = getMemoryPage(address);
                address += PAGE_SIZE;
                offset = 0;
            }
        }
        if (offset == 0) {
            currentPage.flags &= ~MemoryPage.MP_EXEC;
        }
        for (byte b : rodata) {
            currentPage.setByte(offset, b);
            offset++;
            if (offset == PAGE_SIZE) {
                currentPage.flags &= ~MemoryPage.MP_WRITE;
                setMemoryPageIfAbsent(address, MemoryPage.MP_READ | MemoryPage.MP_WRITE);
                currentPage = getMemoryPage(address);
                address += PAGE_SIZE;
                offset = 0;
            }
        }
        currentPage.flags |= MemoryPage.MP_WRITE;
        for (byte b : data) {
            currentPage.setByte(offset, b);
            offset++;
            if (offset == PAGE_SIZE) {
                setMemoryPageIfAbsent(address, MemoryPage.MP_READ | MemoryPage.MP_WRITE);
                currentPage = getMemoryPage(address);
                address += PAGE_SIZE;
                offset = 0;
            }
        }
        long mapped = 0;
        while (mapped < bssSectionLength) {
            if (bssSectionLength - mapped < PAGE_SIZE - offset) {
                break;
            }
            mapped += PAGE_SIZE;
            setMemoryPageIfAbsent(address, MemoryPage.MP_READ | MemoryPage.MP_WRITE);
            address += PAGE_SIZE;
            offset = 0;
        }
        MemoryPage.FreeMemory head = new MemoryPage.FreeMemory(0, 0);
        head.next = new MemoryPage.FreeMemory(address - PAGE_SIZE + offset, MAX_MEMORY_ADDRESS);
        freeMemoryList = head;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public synchronized long allocateMemory(long size) {
        long length = size + 8;
        MemoryPage.FreeMemory freeMemory = freeMemoryList;
        while (freeMemory != null) {
            if (freeMemory.end - freeMemory.start >= length) {
                long start = freeMemory.start;
                freeMemory.start += length;
                long address = start;
                while (length > 0) {
                    setMemoryPageIfAbsent(address & ~PAGE_OFFSET_MASK, MemoryPage.MP_READ | MemoryPage.MP_WRITE);
                    long tmp = PAGE_SIZE - (address & PAGE_OFFSET_MASK);
                    length -= tmp;
                    address += tmp;
                }
                setLong(start, size);
                return start + 8;
            }
            freeMemory = freeMemory.next;
        }
        throw new RuntimeException("Out of memory");
    }

    public synchronized long reallocateMemory(long address, long size) {
        long oldSize = getLong(address - 8);
        byte[] bytes = new byte[(int) oldSize];
        for (int i = 0; i < oldSize; i++) bytes[i] = getByte(address + i);
        freeMemory(address);
        long newAddress = allocateMemory(size);
        for (int i = 0; i < Math.min(oldSize, size); i++) setByte(newAddress + i, bytes[i]);
        return newAddress;
    }

    public synchronized void freeMemory(long address) {
        address -= 8;
        long size = getLong(address) + 8;
        MemoryPage.FreeMemory freeMemory = freeMemoryList;
        while (freeMemory.next != null) {
            if (freeMemory.end == address) {
                freeMemory.end += size;
                break;
            } else if (freeMemory.start == address + size) {
                freeMemory.start -= size;
                break;
            } else if (freeMemory.end < address && freeMemory.next.start > address + size) {
                MemoryPage.FreeMemory next = freeMemory.next;
                freeMemory.next = new MemoryPage.FreeMemory(address, address + size);
                freeMemory.next.next = next;
                break;
            }
            freeMemory = freeMemory.next;
        }
        if (freeMemory.end < address && freeMemory.next == null) {
            freeMemory.next = new MemoryPage.FreeMemory(address, address + size);
        }
        while (size > 0) {
            releaseMemoryPage(address & ~PAGE_OFFSET_MASK);
            long tmp = PAGE_SIZE - address & PAGE_OFFSET_MASK;
            size -= tmp;
            address += tmp;
        }
    }

    private MemoryPage getMemoryPage(long address) {
        int pgdOffset = (int) ((address >> 39) & 0x1ff);
        MemoryPage[][][] pud = memoryPageTable[pgdOffset];
        if (pud == null) return null;
        int pudOffset = (int) ((address >> 30) & 0x1ff);
        MemoryPage[][] pmd = pud[pudOffset];
        if (pmd == null) return null;
        int pmdOffset = (int) ((address >> 21) & 0x1ff);
        MemoryPage[] pte = pmd[pmdOffset];
        if (pte == null) return null;
        int pteOffset = (int) ((address >> 12) & 0x1ff);
        return pte[pteOffset];
    }

    private void releaseMemoryPage(long address) {
        if ((address & PAGE_OFFSET_MASK) != 0) {
            throw new RuntimeException("Invalid memory address: 0x" + Long.toHexString(address));
        }
        MemoryPage memoryPage = getMemoryPage(address);
        memoryPage.release();
        if (memoryPage.refCount == 0) resetMemoryPageIfAbsent(address);
    }

    private synchronized boolean setMemoryPageIfAbsent(long address, int flags) {
        if ((address & PAGE_OFFSET_MASK) != 0) {
            throw new RuntimeException("Invalid memory address: 0x" + Long.toHexString(address));
        }
        int pgdOffset = (int) ((address >> 39) & 0x1ff);
        MemoryPage[][][] pud = memoryPageTable[pgdOffset];
        if (pud == null) {
            pud = new MemoryPage[PAGE_TABLE_SIZE][][];
            memoryPageTable[pgdOffset] = pud;
        }
        int pudOffset = (int) ((address >> 30) & 0x1ff);
        MemoryPage[][] pmd = pud[pudOffset];
        if (pmd == null) {
            pmd = new MemoryPage[PAGE_TABLE_SIZE][];
            pud[pudOffset] = pmd;
        }
        int pmdOffset = (int) ((address >> 21) & 0x1ff);
        MemoryPage[] pte = pmd[pmdOffset];
        if (pte == null) {
            pte = new MemoryPage[PAGE_TABLE_SIZE];
            pmd[pmdOffset] = pte;
        }
        int pteOffset = (int) ((address >> 12) & 0x1ff);
        MemoryPage page = pte[pteOffset];
        boolean ret = page != null;
        if (page == null) {
            page = new MemoryPage(flags);
            pte[pteOffset] = page;
        }
        page.retain();
        return ret;
    }

    private synchronized void resetMemoryPageIfAbsent(long address) {
        int pgdOffset = (int) ((address >> 39) & 0x1ff);
        MemoryPage[][][] pud = memoryPageTable[pgdOffset];
        if (pud == null) return;
        int pudOffset = (int) ((address >> 30) & 0x1ff);
        MemoryPage[][] pmd = pud[pudOffset];
        if (pmd == null) return;
        int pmdOffset = (int) ((address >> 21) & 0x1ff);
        MemoryPage[] pte = pmd[pmdOffset];
        if (pte == null) return;
        int pteOffset = (int) ((address >> 12) & 0x1ff);
        pte[pteOffset] = null;
    }

    public byte getByte(long address) {
        return getMemoryPage(address).getByte(address & PAGE_OFFSET_MASK);
    }

    public short getShort(long address) {
        if (((address & PAGE_OFFSET_MASK) + 2) < PAGE_SIZE) {
            return getMemoryPage(address).getShort(address & PAGE_OFFSET_MASK);
        } else {
            return (short) (getMemoryPage(address).getByte(address & PAGE_OFFSET_MASK) | (getMemoryPage(address + 1).getByte(0) << 8));
        }
    }

    public int getInt(long address) {
        if (((address & PAGE_OFFSET_MASK) + 4) < PAGE_SIZE) {
            return getMemoryPage(address).getInt(address & PAGE_OFFSET_MASK);
        } else {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                value |= (getMemoryPage(address).getByte(address & PAGE_OFFSET_MASK) & 0xff) << (i * 8);
                address++;
            }
            return value;
        }
    }

    public long getLong(long address) {
        if (((address & PAGE_OFFSET_MASK) + 8) < PAGE_SIZE) {
            return getMemoryPage(address).getLong(address & PAGE_OFFSET_MASK);
        } else {
            long value = 0;
            for (int i = 0; i < 8; i++) {
                value |= (getMemoryPage(address).getByte(address & PAGE_OFFSET_MASK) & 0xffL) << (i * 8);
                address++;
            }
            return value;
        }
    }

    public float getFloat(long address) {
        if (((address & PAGE_OFFSET_MASK) + 4) < PAGE_SIZE) {
            return getMemoryPage(address).getFloat(address & PAGE_OFFSET_MASK);
        } else {
            int value = 0;
            for (int i = 0; i < 4; i++) {
                value |= (getMemoryPage(address).getByte(address & PAGE_OFFSET_MASK) & 0xff) << (i * 8);
                address++;
            }
            return Float.intBitsToFloat(value);
        }
    }

    public double getDouble(long address) {
        if (((address & PAGE_OFFSET_MASK) + 8) < PAGE_SIZE) {
            return getMemoryPage(address).getDouble(address & PAGE_OFFSET_MASK);
        } else {
            long value = 0;
            for (int i = 0; i < 8; i++) {
                value |= (getMemoryPage(address).getByte(address & PAGE_OFFSET_MASK) & 0xffL) << (i * 8);
                address++;
            }
            return Double.longBitsToDouble(value);
        }
    }

    public void setByte(long address, byte value) {
        getMemoryPage(address).setByte(address & PAGE_OFFSET_MASK, value);
    }

    public void setShort(long address, short value) {
        if (((address & PAGE_OFFSET_MASK) + 2) < PAGE_SIZE) {
            getMemoryPage(address).setShort(address & PAGE_OFFSET_MASK, value);
        } else {
            getMemoryPage(address).setByte(address & PAGE_OFFSET_MASK, (byte) (value & 0xff));
            getMemoryPage(address + 1).setByte(0, (byte) (value >> 8));
        }
    }

    public void setInt(long address, int value) {
        if (((address & PAGE_OFFSET_MASK) + 4) < PAGE_SIZE) {
            getMemoryPage(address).setInt(address & PAGE_OFFSET_MASK, value);
        } else {
            for (int i = 0; i < 4; i++) {
                getMemoryPage(address).setByte(address & PAGE_OFFSET_MASK, (byte) (value >> (i * 8)));
                address++;
            }
        }
    }

    public void setLong(long address, long value) {
        if (((address & PAGE_OFFSET_MASK) + 8) < PAGE_SIZE) {
            getMemoryPage(address).setLong(address & PAGE_OFFSET_MASK, value);
        } else {
            for (int i = 0; i < 8; i++) {
                getMemoryPage(address).setByte(address & PAGE_OFFSET_MASK, (byte) (value >> (i * 8)));
                address++;
            }
        }
    }

    public void setFloat(long address, float value) {
        if (((address & PAGE_OFFSET_MASK) + 4) < PAGE_SIZE) {
            getMemoryPage(address).setFloat(address & PAGE_OFFSET_MASK, value);
        } else {
            int bits = Float.floatToRawIntBits(value);
            for (int i = 0; i < 4; i++) {
                getMemoryPage(address).setByte(address & PAGE_OFFSET_MASK, (byte) (bits >> (i * 8)));
                address++;
            }
        }
    }

    public void setDouble(long address, double value) {
        if (((address & PAGE_OFFSET_MASK) + 8) < PAGE_SIZE) {
            getMemoryPage(address).setDouble(address & PAGE_OFFSET_MASK, value);
        } else {
            long bits = Double.doubleToRawLongBits(value);
            for (int i = 0; i < 8; i++) {
                getMemoryPage(address).setByte(address & PAGE_OFFSET_MASK, (byte) (bits >> (i * 8)));
            }
        }
    }

}