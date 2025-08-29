package ldk.l.lvm.module;

import ldk.l.lvm.vm.ByteCode;
import ldk.l.lvm.vm.VirtualMachine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record Module(byte[] text, byte[] rodata, byte[] data, long bssSectionLength, long entryPoint) {
    public byte[] raw(boolean littleEndian) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(21 + text.length + 8 + rodata.length + 8 + data.length + 16);
        byteBuffer.order(littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        byteBuffer.put((byte) 'l');
        byteBuffer.put((byte) 'v');
        byteBuffer.put((byte) 'm');
        byteBuffer.put((byte) 'e');
        byteBuffer.put(littleEndian ? ByteCode.LITTLE_ENDIAN : ByteCode.BIG_ENDIAN);
        byteBuffer.putLong(VirtualMachine.LVM_VERSION);
        byteBuffer.putLong(text.length);
        byteBuffer.put(text);
        byteBuffer.putLong(rodata.length);
        byteBuffer.put(rodata);
        byteBuffer.putLong(data.length);
        byteBuffer.put(data);
        byteBuffer.putLong(bssSectionLength);
        byteBuffer.putLong(entryPoint);
        return byteBuffer.array();
    }

    public static Module fromRaw(byte[] raw) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(raw);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        if (byteBuffer.get() != 'l' || byteBuffer.get() != 'v' || byteBuffer.get() != 'm' || byteBuffer.get() != 'e') {
            throw new RuntimeException("Invalid module format");
        }
        if (byteBuffer.get() != VirtualMachine.ENDIAN) {
            throw new RuntimeException("Invalid endian");
        }
        if (byteBuffer.getLong() > VirtualMachine.LVM_VERSION) {
            throw new RuntimeException("Unsupported module version");
        }
        long textLength = byteBuffer.getLong();
        byte[] text = new byte[(int) textLength];
        byteBuffer.get(text);
        long rodataLength = byteBuffer.getLong();
        byte[] rodata = new byte[(int) rodataLength];
        byteBuffer.get(rodata);
        long dataLength = byteBuffer.getLong();
        byte[] data = new byte[(int) dataLength];
        byteBuffer.get(data);
        long bssSectionLength = byteBuffer.getLong();
        long entryPoint = byteBuffer.getLong();
        return new Module(text, rodata, data, bssSectionLength, entryPoint);
    }
}
