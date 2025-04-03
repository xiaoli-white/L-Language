package ldk.l.lc.util.error;

import ldk.l.lc.util.Position;

public class Error {
    public boolean isError;
    public String information;
    public Position position;
    public Error(boolean isError, String information, Position position) {
        this.isError=isError;
        this.information=information;
        this.position=position;
    }
}