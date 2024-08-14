package io.vortex.cvtr.process.data;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.vortex.cvtr.JavaClassSupport;
import io.vortex.cvtr.PsiTypeEnsurance;
import io.vortex.cvtr.StringSupport;

import java.util.*;

public class ReturnOfMethod {

    private final String returnTypeCanonicalName;

    private final PsiClass returnTypePsiClass;

    private final PsiType returnType;

    // todo:lithiumnzinc 2024/8/13 16:44 > 似乎不需要这个了
    private final boolean isPlainObject;

    private final Variable rootVariable;

    // todo:lithiumnzinc 2024/8/14 09:58 > 这里的问题在于 如果用数组的话 没有办法提前知道元素的个数
    private List<Variable> insidePlainVariables;

    private final Map<String, Variable> router = new HashMap<>();

    public ReturnOfMethod(PsiClass returnTypePsiClass, PsiType returnType) {
        this.returnType = PsiTypeEnsurance.ensurePrimitiveToBoxed(returnType);
        this.returnTypePsiClass = returnTypePsiClass;
        this.returnTypeCanonicalName = returnTypePsiClass.getQualifiedName();
        this.isPlainObject = JavaClassSupport.Api.isPlainObject(returnTypePsiClass);
        this.rootVariable = Variable.describeFor(returnType, StringSupport.lowerFirst(returnTypePsiClass.getName()));

        // todo:lithiumnzinc 2024/8/14 10:08 > 这个 extract 方法放在 Variable 类里面比较好
        this.insidePlainVariables = extractPlainVariables(rootVariable);
        fillInRouter(rootVariable);

        System.out.println("---------------------- Returns Router Info ----------------------");
        System.out.println(this.router.size());
        this.router.forEach((key, value) -> System.out.println(key + " --- >>> " + value.getName()));
        System.out.println("---------------------- Returns Router Info ----------------------");
    }

    public Variable findVariableByRoute(String route) {
        if (StringSupport.isBlankStr(route)) {
            return Variable.unknown();
        }
        Variable variable = router.get(route);
        if (Objects.isNull(variable)) {
            return Variable.unknown();
        }
        return variable;
    }

    private void fillInRouter(Variable variable) {
        this.router.put(variable.getRefRoute(), variable);
        if (variable.isPlainObject()) {
            for (Variable inside : variable.getInsideVariables()) {
                fillInRouter(inside);
            }
        }
    }

    private List<Variable> extractPlainVariables(Variable rootVariable) {
        List<Variable> res = new ArrayList<>();
        if (rootVariable.isPlainObject()) {
            res.add(rootVariable);
            for (Variable insideVariable : rootVariable.getInsideVariables()) {
                if (insideVariable.isPlainObject()) {
                    res.addAll(extractPlainVariables(insideVariable));
                }
            }
        }
        return res;
    }

    public static ReturnOfMethod extractFrom(PsiMethod method) {
        PsiType returnType = PsiTypeEnsurance.ensurePrimitiveToBoxed(method.getReturnType());
        PsiClass returnClass = PsiTypesUtil.getPsiClass(returnType);
        if (returnClass == null) {
            throw new IllegalArgumentException("Cannot resolve return type: " + returnType);
        }
        return new ReturnOfMethod(returnClass, returnType);
    }

    public String getReturnTypeCanonicalName() {
        return returnTypeCanonicalName;
    }

    public PsiClass getReturnTypePsiClass() {
        return returnTypePsiClass;
    }

    public PsiType getReturnType() {
        return returnType;
    }

    public boolean isPlainObject() {
        return isPlainObject;
    }

    public Variable getRootVariable() {
        return rootVariable;
    }

    public List<Variable> plainObjects() {
        // todo:lithiumnzinc 2024/8/14 10:12 > 安全的发布 最好只是 unmodifiable
        return new ArrayList<>(this.insidePlainVariables);
    }

    @Override
    public String toString() {
        return "ReturnOfMethod{" +
                "returnTypeCanonicalName='" + returnTypeCanonicalName + '\'' +
                ", returnTypePsiClass=" + returnTypePsiClass +
                ", returnType=" + returnType +
                ", isPlainObject=" + isPlainObject +
                ", rootVariable=" + rootVariable +
                '}';
    }
}
