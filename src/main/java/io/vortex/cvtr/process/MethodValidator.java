package io.vortex.cvtr.process;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;

public class MethodValidator {

    @SuppressWarnings("all")
    public void validateMethod(PsiMethod selectedMethod) {
        if (selectedMethod.getParameterList().isEmpty()) {
            throw new RuntimeException("Method parameter list is empty, cannot generate code for it.");
        }
        if (selectedMethod.getReturnType().equals(PsiType.VOID)) {
            throw new RuntimeException("Method returns void, cannot generate code for it.");
        }
    }


}
