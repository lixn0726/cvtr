package io.vortex.cvtr.model;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import io.vortex.cvtr.StringUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParametersOfMethod {

    private final Variable[] variables;

    private final int parameterCount;

    // todo:lithiumnzinc 2024/8/14 10:25 > 出此下策来完善逻辑
    private final Map<String, Variable> router = new HashMap<>();

    private ParametersOfMethod(Variable[] variables) {
        this.variables = variables;
        this.parameterCount = variables.length;
        for (Variable variable : variables) {
            fillInRouter(variable);
        }

        System.out.println("---------------------- Parameters Router Info ----------------------");
        System.out.println(this.router.size());
        this.router.forEach((key, value) -> System.out.println(key + " --- >>> " + value.getName()));
        System.out.println("---------------------- Parameters Router Info ----------------------");
    }

    private void fillInRouter(Variable rootVariable) {
        this.router.put(rootVariable.getRefRoute(), rootVariable);
        if (rootVariable.isPlainObject()) {
            for (Variable inside : rootVariable.getInsideVariables()) {
                fillInRouter(inside);
            }
        }
    }

    public static ParametersOfMethod extractFrom(PsiMethod method) {
        System.out.println("Extracting parameters of " + method.getName());
        PsiParameterList parameterList = method.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();

        Variable[] variables = new Variable[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            variables[i] = Variable.describeFor(parameters[i]);
        }

        return new ParametersOfMethod(variables);

    }

    public VariableMatchResult canMatchVariable(Variable variable) {
        if (Objects.isNull(variable.getPsiType()) || StringUtil.isBlankStr(variable.getName())) {
            return VariableMatchResult.failure();
        }
        return canMatchVariable(variable.getPsiType(), variable.getName());
    }

    public VariableMatchResult canMatchVariable(PsiType type, String name) {
        for (Variable insideVariable : variables) {
            VariableMatchResult matchResult = insideVariable.canMatchVariable(type, name);
            if (matchResult.success()) {
                return matchResult;
            }
        }
        return VariableMatchResult.failure();
    }

    public Variable[] getVariables() {
        return variables;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public Variable findVariableByRoute(String route) {
        if (StringUtil.isBlankStr(route)) {
            return Variable.unknown();
        }
        Variable variable = router.get(route);
        if (Objects.isNull(variable)) {
            variable = Variable.unknown();
        }
        return variable;
    }

    @Override
    public String toString() {
        return "ParametersOfMethod{" +
                "variables=" + Arrays.toString(variables) +
                ", parameterCount=" + parameterCount +
                '}';
    }
}
