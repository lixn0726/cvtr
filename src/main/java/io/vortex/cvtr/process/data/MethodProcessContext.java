package io.vortex.cvtr.process.data;

import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodProcessContext {

    private static final AtomicInteger processIdCounter = new AtomicInteger(0);

    private final PsiManager psiManager;
    private final PsiMethod method;
    private final StringBuilder codeToWave = new StringBuilder();
    private final MethodSignature methodSignature;
    // todo:lithiumnzinc 2024/8/13 21:51 > 用来规避在 set 方法中的重复生成
    private final List<String> knownRoute = new ArrayList<>();

    private MethodProcessContext(PsiMethod methodToProcess) {
        this.psiManager = PsiManager.getInstance(methodToProcess.getProject());
        this.method = methodToProcess;
        this.methodSignature = MethodSignature.extractFrom(methodToProcess);
    }

    public static MethodProcessContext wrap(PsiMethod methodToProcess) {
        return new MethodProcessContext(methodToProcess);
    }

    public String getCodeToWave() {
        return codeToWave.toString();
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    public PsiMethod getMethod() {
        return method;
    }

    public void appendCodeLine(String code) {
        codeToWave.append(code);
    }

    public boolean isKnownRoute(String route) {
        return knownRoute.contains(route);
    }

    public void addKnownRoute(String route) {
        knownRoute.add(route);
    }

    public PsiManager getPsiManager() {
        return psiManager;
    }

}
