package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.error.ErrorStream;

public class AutomaticInjector extends LCAstVisitor  {
    private final ErrorStream errorStream;
    public AutomaticInjector(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }
}
