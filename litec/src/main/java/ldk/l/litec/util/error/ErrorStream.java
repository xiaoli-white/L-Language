package ldk.l.litec.util.error;

import ldk.l.litec.util.Position;
import ldk.l.util.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ErrorStream {
    public String fileName;
    public String[] fileLines;
    public int errorNum = 0;
    public int warningNum = 0;
    public List<Error> errors = new ArrayList<>();
    public HashMap<Language, HashMap<Integer, String>> lang2map = new HashMap<>() {{
        put(Language.zh_cn, InfoMaps.zh_cn);
        put(Language.en_us, InfoMaps.en_us);
    }};
    private final HashMap<Integer, String> key2info;

    public ErrorStream(Language language, String fileName, String[] fileLines) {
        this.key2info = lang2map.get(language);
        this.fileName = fileName;
        this.fileLines = fileLines;
    }

    public void printError(boolean isError, Position position, int infoNum, String... others) {
        printStackTrace();

        System.err.println(this.fileName + ":" + (position.beginLine() == position.endLine() ? position.beginLine() : (position.endLine() > position.beginLine() ? position.beginLine() + "-" + position.endLine() : position.endLine() + "-" + position.beginLine())) + ": " + (isError ? "error" : "warning") + ": " + this.key2info.get(infoNum));
        if (position.beginLine() == position.endLine()) {
            System.err.println(this.fileLines[position.beginLine()]);
            for (int i = 0; i < position.beginCol(); i++) {
                System.err.print(' ');
            }
            if (position.endCol() == -1) {
                for (int i = 0; i < this.fileLines[position.beginLine()].length() - position.beginCol(); i++) {
                    System.err.print('^');
                }
            } else if (position.endCol() == position.beginCol()) {
                System.err.print('^');
            } else {
                for (int i = 0; i < position.endCol() - position.beginCol() + 1; i++) {
                    System.err.print('^');
                }
            }
            System.err.print('\n');
        } else if (position.endLine() > position.beginLine()) {
            for (int lineIndex = position.beginLine(); lineIndex <= position.endLine(); lineIndex++) {
                System.err.println(this.fileLines[lineIndex]);
                if (lineIndex == position.beginLine() || lineIndex == position.endLine()) {
                    for (int i = 0; i < position.beginCol(); i++) {
                        System.err.print(' ');
                    }
                    if (position.endCol() == -1) {
                        for (int i = 0; i < this.fileLines[lineIndex].length() - position.beginCol(); i++) {
                            System.err.print('^');
                        }
                    } else if (position.endCol() == position.beginCol()) {
                        System.err.print('^');
                    } else {
                        for (int i = 0; i < position.endCol() - position.beginCol() + 1; i++) {
                            System.err.print('^');
                        }
                    }
                } else {
                    for (int i = 0; i < this.fileLines[lineIndex].length(); i++) {
                        System.err.print('^');
                    }
                }
                System.err.print('\n');
            }
        } else {
            for (int lineIndex = position.endLine(); lineIndex <= position.beginLine(); lineIndex++) {
                System.err.println(this.fileLines[lineIndex]);
                if (lineIndex == position.beginLine() || lineIndex == position.endLine()) {
                    for (int i = 0; i < position.beginCol(); i++) {
                        System.err.print(' ');
                    }
                    if (position.endCol() == -1) {
                        for (int i = 0; i < this.fileLines[lineIndex].length() - position.beginCol(); i++) {
                            System.err.print('^');
                        }
                    } else if (position.endCol() == position.beginCol()) {
                        System.err.print('^');
                    } else {
                        for (int i = 0; i < position.endCol() - position.beginCol() + 1; i++) {
                            System.err.print('^');
                        }
                    }
                } else {
                    for (int i = 0; i < this.fileLines[lineIndex].length(); i++) {
                        System.err.print('^');
                    }
                }
                System.err.print('\n');
            }
        }
        if (isError) {
            this.errorNum++;
        } else {
            this.warningNum++;
        }
        this.errors.add(new Error(isError, this.key2info.get(infoNum), position));
    }

    public static void printStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        System.err.println("StackTrace:");
        for (int i = 2; i < stackTraceElements.length; i++) {
            System.err.println(stackTraceElements[i].toString());
        }
    }

    public void printErrorMessage(boolean isError, String message) {
        System.err.println((isError ? "error" : "warning") + ": " + message);
        if (isError) {
            this.errorNum++;
        } else {
            this.warningNum++;
        }
        this.errors.add(new Error(isError, message, null));
    }

    public boolean checkErrorNum(String prefix) {
        if (this.errorNum != 0) {
            this.dumpErrorsAndWarnings(prefix);
            return false;
        } else {
            return true;
        }
    }

    public void dumpErrorsAndWarnings(String prefix) {
        if (this.warningNum != 0)
            System.out.println(prefix + this.warningNum + " warning(s).");
        if (this.errorNum != 0)
            System.out.println(prefix + this.errorNum + " error(s).");
    }
}