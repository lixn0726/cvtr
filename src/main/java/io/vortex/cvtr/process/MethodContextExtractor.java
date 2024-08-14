package io.vortex.cvtr.process;

import com.intellij.psi.PsiMethod;
import io.vortex.cvtr.process.data.MethodProcessContext;

public class MethodContextExtractor {

    public MethodProcessContext wrapInContext(PsiMethod selectedMethod) {
        return MethodProcessContext.wrap(selectedMethod);
    }

}
