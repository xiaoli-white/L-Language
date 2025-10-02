package ldk.l.lg.parser;

import ldk.l.lg.ir.IRConstantPool;
import ldk.l.lg.ir.IRModule;

public class Converter extends LGIRBaseVisitor<Object>{
    private final IRModule module;
    public Converter(IRModule module)
    {
        this.module = module;
    }

    @Override
    public Object visitConstant_pool_entry(LGIRParser.Constant_pool_entryContext ctx) {
//        module.constantPool.put(new IRConstantPool.Entry(ctx.type().getText(), ctx.constant().getText()));
        return super.visitConstant_pool_entry(ctx);
    }
}
