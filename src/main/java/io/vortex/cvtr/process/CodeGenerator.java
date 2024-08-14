package io.vortex.cvtr.process;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.vortex.cvtr.JavaClassSupport;
import io.vortex.cvtr.SpecialCharacters;
import io.vortex.cvtr.StringSupport;
import io.vortex.cvtr.process.data.MethodProcessContext;
import io.vortex.cvtr.process.data.ParametersOfMethod;
import io.vortex.cvtr.process.data.ReturnOfMethod;

@Deprecated
public class CodeGenerator {

    public String generateCode(MethodProcessContext context) {
        return generateCode(context.getMethodSignature().getReturnOfMethod(),
                context.getMethodSignature().getOrderedParameters());
    }

    // getAllFields() --> 获取所有的属性，包括父类的
    // getFields()    --> 获取当前类的所有属性
    // getContainingClass --> 不知道为什么返回的是null，看方法名也看不出来是什么意思
    public String generateCode(ReturnOfMethod rom, ParametersOfMethod pom) {
        StringBuilder code = new StringBuilder();

        PsiClass returnTypePsiClass = rom.getReturnTypePsiClass();

        String localVariable = StringSupport.lowerFirst(rom.getReturnTypePsiClass().getName());

        code.append(returnTypePsiClass.getName())
                .append(" ")
                .append(localVariable)
                .append(" = new ")
                .append(StringSupport.upperFirst(localVariable))
                .append("();\n");

        PsiField[] allFields = returnTypePsiClass.getAllFields();


        for (PsiField field : allFields) {
            PsiType curFieldType = field.getType();
            PsiClass curFieldClass = PsiTypesUtil.getPsiClass(curFieldType);
            String curFieldName = field.getName();
            if (curFieldClass == null) {

                code.append(SpecialCharacters.multiTabSpace(2))
                        .append(localVariable)
                        .append(".set")
                        .append(StringSupport.upperFirst(field.getName()))
                        .append("();\n");
                continue;
            }

            boolean isPlainObject = JavaClassSupport.Api.isPlainObject(curFieldClass);

            System.out.println(curFieldName + " -> " + curFieldClass + " isPlainObject -> " + isPlainObject);

            if (JavaClassSupport.Api.isPlainObject(curFieldClass)) {
                code.append(curFieldClass.getName())
                        .append(" ")
                        .append(curFieldName)
                        .append(" = new ")
                        .append(curFieldClass.getName())
                        .append("();\n")
                        .append(SpecialCharacterInputUnit.getMultiTabSpace(2))
                        .append(localVariable)
                        .append(".set")
                        .append(StringSupport.upperFirst(curFieldName))
                        .append("(")
                        .append(curFieldName)
                        .append(");\n")
                        .append(SpecialCharacterInputUnit.getMultiTabSpace(2));
                recursiveHandle(pom, code, curFieldType, field.getName());
            } else {
                code.append(localVariable)
                        .append(".set")
                        .append(StringSupport.upperFirst(curFieldName))
                        .append("(")
                        .append(findMatchParam(pom, curFieldType, curFieldName))
                        .append(");\n");
            }
        }

        code.append(SpecialCharacterInputUnit.getMultiTabSpace(2))
                .append("return ")
                .append(localVariable)
                .append(";");
        System.out.println("Generated code example below: ");
        System.out.println(code.toString());

        return code.toString();

    }

    private void recursiveHandle(ParametersOfMethod params, StringBuilder code, PsiType type, String sourceFieldName) {
        PsiClass source = PsiTypesUtil.getPsiClass(type);
        if (source == null) {
            return;
        }
        for (PsiField field : source.getAllFields()) {
            PsiType curFieldType = field.getType();
            PsiClass curFieldClass = PsiTypesUtil.getPsiClass(field.getType());
            String curFieldName = field.getName();

            if (curFieldClass == null) {
                System.err.println("Cannot find class for " + field.getName() + " cur class: " + curFieldClass);
                continue;
            }
            String localVariableName = StringSupport.lowerFirst(curFieldClass.getName());
            if (JavaClassSupport.Api.isPlainObject(curFieldClass)) {

                code.append(SpecialCharacterInputUnit.getMultiTabSpace(2))
                        .append(curFieldClass.getName())
                        .append(" ")
                        .append(localVariableName)
                        .append(" = new ")
                        .append(curFieldClass.getName())
                        .append("();\n")

                        .append(SpecialCharacterInputUnit.getMultiTabSpace(2))
                        .append(sourceFieldName)
                        .append(".set")
                        .append(StringSupport.upperFirst(curFieldName))
                        .append("(")
                        .append(localVariableName)
                        .append(");\n");
                recursiveHandle(params, code, curFieldType, localVariableName);
            } else {
                code.append(sourceFieldName)
                        .append(".set")
                        .append(StringSupport.upperFirst(curFieldName))
                        .append("(")
                        .append(findMatchParam(params, curFieldType, curFieldName))
                        .append(");\n");
            }
        }
    }

    private String findMatchParam(ParametersOfMethod params, PsiType matchType, String matchName) {
        return params.canMatchVariable(matchType, matchName).getMatchParameterGetterMethodText();
    }

//    private void handle

    private static class SpecialCharacterInputUnit {

        private static final String TAB = "\t";

        private static final String TAB_SPACE = "    ";

        private static final String LINE_SEPARATOR = System.lineSeparator();

        public static String getMultiTabSpace(int count) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < count; i++) {
                result.append(TAB_SPACE);
            }
            return result.toString();
        }

    }

}
