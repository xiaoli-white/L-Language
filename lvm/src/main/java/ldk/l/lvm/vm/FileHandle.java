package ldk.l.lvm.vm;

import java.io.*;

public final class FileHandle {
    public static final int FH_READ = 1;
    public static final int FH_WRITE = 1 << 1;
    private static final int FH_PREOPEN = 1;
    private final String path;
    private final int flags;
    private final int mode;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public FileHandle(String path, int flags, int mode, InputStream inputStream, OutputStream outputStream) {
        this.path = path;
        this.flags = flags | FH_PREOPEN;
        this.mode = mode;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public FileHandle(String path, int flags, int mode) {
        this.path = path;
        this.flags = flags;
        this.mode = mode;
        try {
            if ((flags & FH_READ) != 0) this.inputStream = new FileInputStream(path);
            else this.inputStream = null;
            if ((flags & FH_WRITE) != 0) this.outputStream = new FileOutputStream(path);
            else this.outputStream = null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public int read(byte[] buffer, int count) {
        if (inputStream == null) throw new RuntimeException("File not open for reading");
        try {
            return inputStream.read(buffer, 0, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] buffer) {
        if (outputStream == null) throw new RuntimeException("File not open for writing");
        try {
            outputStream.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if ((flags & FH_PREOPEN) != 0) return;
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
