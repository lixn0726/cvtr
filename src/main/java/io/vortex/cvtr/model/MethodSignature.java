package io.vortex.cvtr.model;

import com.intellij.psi.PsiMethod;

public class MethodSignature {

    private final ParametersOfMethod orderedParameters;
    private final ReturnOfMethod returnOfMethod;
    private final String methodNameStr;

    public MethodSignature(PsiMethod method) {
        this.orderedParameters = ParametersOfMethod.extractFrom(method);
        this.returnOfMethod = ReturnOfMethod.extractFrom(method);
        this.methodNameStr = method.getName();
    }

    static MethodSignature extractFrom(PsiMethod method) {
        return new MethodSignature(method);
    }

    public ParametersOfMethod getOrderedParameters() {
        return orderedParameters;
    }

    public ReturnOfMethod getReturnOfMethod() {
        return returnOfMethod;
    }

    @Override
    public String toString() {
        return "MethodSignature{" +
                "orderedParameters=" + orderedParameters +
                ", returnOfMethod=" + returnOfMethod +
                ", methodNameStr='" + methodNameStr + '\'' +
                '}';
    }
}
