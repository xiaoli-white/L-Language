package ldk.l.litec.util.error;

import ldk.l.litec.util.Position;

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