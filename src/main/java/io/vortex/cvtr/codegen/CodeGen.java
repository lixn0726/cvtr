package io.vortex.cvtr.codegen;

import io.vortex.cvtr.SpecialCharacters;
import io.vortex.cvtr.model.MethodProcessContext;
import io.vortex.cvtr.model.ParametersOfMethod;
import io.vortex.cvtr.model.Variable;

import java.util.List;

public class CodeGen {

    public void generateCode(MethodProcessContext ctx) {
        ctx.addCodeText(codeTextForInstantiating(ctx))
                .addCodeText(SpecialCharacters.lineSeparator())
                .addCodeText(codeTextForFieldSet(ctx, ctx.getMethodSignature().getReturnOfMethod().getRootVariable()))
                .addCodeText(SpecialCharacters.lineSeparator())
                .addCodeText(codeTextForReturning(ctx));
    }


    private String codeTextForInstantiating(MethodProcessContext ctx) {
        List<Variable> plainObjects = ctx.getMethodSignature().getReturnOfMethod().plainObjects();
        StringBuilder codePiece = new StringBuilder();
        ParametersOfMethod orderedParameters = ctx.getMethodSignature().getOrderedParameters();
        for (Variable variable : plainObjects) {
            if (orderedParameters.canMatchVariable(variable).success()) {
                continue;
            }
            String clazzName = variable.getPsiClass().getName();
            String variableName = variable.getName();
            codePiece.append(SpecialCharacters.twoTabSpace())
                    .append(clazzName)
                    .append(SpecialCharacters.space())
                    .append(variableName)
                    .append(" = new ")
                    .append(clazzName)
                    .append("();\n");
        }
        return codePiece.toString();
    }

    private String codeTextForFieldSet(MethodProcessContext ctx, Variable variable) {
        StringBuilder res = new StringBuilder();
        if (variable.isPlainObject()) {
            if (ctx.getMethodSignature().getOrderedParameters().canMatchVariable(variable).success()) {
                // 即使是数据类 也要判断是否有现成的可以直接用
                res.append(variable.methodTextOfSettingThis(ctx, variable.getFirstRefInRoute()));
            }
            for (Variable insideVariable : variable.getInsideVariables()) {
                res.append(codeTextForFieldSet(ctx, insideVariable));
            }
        } else {
            res.append(variable.methodTextOfSettingThis(ctx, variable.getFirstRefInRoute()));
        }
        return res.toString();
    }

    private String codeTextForReturning(MethodProcessContext ctx) {
        return SpecialCharacters.twoTabSpace() +
                "return " +
                ctx.getMethodSignature().getReturnOfMethod().getRootVariable().getName() +
                ";";
    }

}
