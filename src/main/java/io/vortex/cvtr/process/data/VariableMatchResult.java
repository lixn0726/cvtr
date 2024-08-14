package io.vortex.cvtr.process.data;

public class VariableMatchResult {

    private final boolean success;

    private final Variable matchedVariable;

    // todo:lithiumnzinc 2024/8/13 15:17 > 换个 api 名字

    private VariableMatchResult(boolean success, Variable matchedVariable) {
        this.success = success;
        this.matchedVariable = matchedVariable;
    }

    public static VariableMatchResult success(Variable matchedVariable) {
        return new VariableMatchResult(true, matchedVariable);
    }

    public static VariableMatchResult failure() {
        return new VariableMatchResult(false, Variable.unknown());
    }

    public boolean success() {
        return success;
    }

    public Variable getMatchedVariable() {
        return matchedVariable;
    }

    public String getMatchParameterGetterMethodText() {
        return this.matchedVariable.methodTextOfGettingThis();
    }
}
