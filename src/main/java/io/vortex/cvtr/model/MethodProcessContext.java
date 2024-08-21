package io.vortex.cvtr.model;

import com.intellij.psi.PsiMethod;
import io.vortex.cvtr.SpecialCharacters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// todo:lithiumnzinc 2024/8/21 15:49 > add some info into this class to notify when things were done.
public class MethodProcessContext {

    private final PsiMethod method;
    private final StringBuilder codeToWave = new StringBuilder();
    private final MethodSignature methodSignature;

    private final List<String> knownRoute = new ArrayList<>();


    private MethodProcessContext(PsiMethod methodToProcess) {
        this.method = methodToProcess;
        this.methodSignature = MethodSignature.extractFrom(methodToProcess);
    }

    public static MethodProcessContext wrap(PsiMethod methodToProcess) {
        return new MethodProcessContext(methodToProcess);
    }

    public String getCodeToWave() {
        formatCode();
        return codeToWave.toString();
    }

    private void formatCode() {
        List<String> plainObjectNames = this.methodSignature.getReturnOfMethod().plainObjects()
                .stream().map(Variable::getName)
                .collect(Collectors.toList());
        String code = codeToWave.toString();
        String[] rows = code.split("\n");
        int listIndex = 0;

        String prevPrefix = plainObjectNames.get(0);
        for (int i = plainObjectNames.size() + 1; i < rows.length; i++) {
            if (listIndex == (plainObjectNames.size() - 1)) {
                break;
            }
            if (!rows[i].trim().startsWith(prevPrefix)) {
                // todo:lithiumnzinc 2024/8/21 16:57 > 找个更好的格式化的方法
                codeToWave.insert(codeToWave.indexOf(rows[i]), SpecialCharacters.lineSeparator());
                prevPrefix = plainObjectNames.get(++listIndex);
            }
        }
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    public PsiMethod getMethod() {
        return method;
    }

    public MethodProcessContext addCodeText(String code) {
        codeToWave.append(code);
        return this;
    }

    public boolean isKnownRoute(String route) {
        return knownRoute.contains(route);
    }

    public void addKnownRoute(String route) {
        knownRoute.add(route);
    }

}
