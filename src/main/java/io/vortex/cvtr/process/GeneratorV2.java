package io.vortex.cvtr.process;

import io.vortex.cvtr.SpecialCharacters;
import io.vortex.cvtr.process.data.MethodProcessContext;
import io.vortex.cvtr.process.data.ParametersOfMethod;
import io.vortex.cvtr.process.data.ReturnOfMethod;
import io.vortex.cvtr.process.data.Variable;

import java.util.List;

public class GeneratorV2 {

    public void generateCode(MethodProcessContext ctx) {

        StringBuilder codePiece = new StringBuilder();

        ReturnOfMethod rom = ctx.getMethodSignature().getReturnOfMethod();
        ParametersOfMethod pom = ctx.getMethodSignature().getOrderedParameters();

        // 1. 这里把所有的类都创建完了
        // todo:lithiumnzinc 2024/8/14 09:53 > 有一个问题是如果能在 params 里面找到对应的 param 那么就可以直接不创建了
        codePiece.append(codeForInstantiatingNecessaryVariables(ctx));
        codePiece.append(codeForSettingSingleField(ctx, rom.getRootVariable()));
        codePiece.append(SpecialCharacters.twoTabSpace())
                .append("return ")
                .append(rom.getRootVariable().getName())
                .append(";\n");

        ctx.appendCodeLine(codePiece.toString());

    }


    private String codeForInstantiatingNecessaryVariables(MethodProcessContext ctx) {
        List<Variable> plainObjects = ctx.getMethodSignature().getReturnOfMethod().plainObjects();
        StringBuilder codePiece = new StringBuilder();
        ParametersOfMethod orderedParameters = ctx.getMethodSignature().getOrderedParameters();
        for (Variable variable : plainObjects) {
            if (orderedParameters.canMatchVariable(variable).success()) {
                // 找得到一样的就不用 new
                System.err.println("Can match type " + variable.getName());
                continue;
            }
            System.err.println("Generating instantiating code for field: " + variable.getName());
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

    private String codeForSettingSingleField(MethodProcessContext ctx, Variable variable) {
        StringBuilder res = new StringBuilder();
        if (variable.isPlainObject()) {
            for (Variable insideVariable : variable.getInsideVariables()) {
                res.append(codeForSettingSingleField(ctx, insideVariable));
            }
        } else {
            // todo:lithiumnzinc 2024/8/14 10:17 > 这里的 variable.getRefRoute() 要改掉 改成第一个 ref
            res.append(variable.methodTextOfSettingThis(ctx, variable.getFirstRefInRoute()));
        }

        return res.toString();
    }

//    private void showGetterRecursive(Variable variable) {
//        if (variable.isPlainObject()) {
//            System.out.println(variable.methodTextOfGettingThis());
//            for (Variable inner : variable.getInsideVariables()) {
//                showGetterRecursive(inner);
//            }
//        } else {
//            System.out.println(variable.methodTextOfGettingThis());
//        }
//    }
//
//    private void showSetterRecursive(MethodProcessContext ctx, Variable variable, String targetToSet) {
//        System.out.println(variable.methodTextOfSettingThis(ctx, targetToSet));
//        if (variable.isPlainObject()) {
//            for (Variable inner : variable.getInsideVariables()) {
//                showSetterRecursive(ctx, inner, targetToSet);
//            }
//        }
//    }


}
