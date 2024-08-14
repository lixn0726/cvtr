package io.vortex.cvtr.process.data;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.vortex.cvtr.JavaClassSupport;
import io.vortex.cvtr.PsiTypeEnsurance;
import io.vortex.cvtr.SpecialCharacters;
import io.vortex.cvtr.StringSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Variable {

    // todo:lithiumnzinc 2024/8/14 20:32 >
    private static final List<String> marker = new ArrayList<>();

    private static final String ROUTE_SEPARATE_REGEX = "\\.";

    private final String name;

    // 统一装换成包装类型来处理
    private final PsiType psiType;

    private final PsiClass psiClass;

    private final boolean isPlainObject;

    private final Variable[] insideVariables;

    private final String refRoute;

    private Variable(PsiType psiType, String name, String refRoute) {
        // todo:lithiumnzinc 2024/8/13 20:50 > 这里的 GlobalSearchScope 是误打误撞搞出来的 后续最好搞清楚或者改掉
        this.psiType = PsiTypeEnsurance.ensurePrimitiveToBoxed(psiType);
        this.name = name;
        this.refRoute = refRoute;
        this.psiClass = PsiTypesUtil.getPsiClass(psiType);
        this.isPlainObject = JavaClassSupport.Api.isPlainObject(psiType);

        if (Objects.isNull(psiClass)) {
            this.insideVariables = new Variable[0];
            return;
        }

        if (isPlainObject) {
            this.insideVariables = new Variable[psiClass.getAllFields().length];
            int arrIdx = 0;

            for (PsiField field : psiClass.getAllFields()) {
                PsiType fieldType = PsiTypeEnsurance.ensurePrimitiveToBoxed(field.getType());
                PsiClass fieldClass = PsiTypesUtil.getPsiClass(fieldType);
                String fieldName = field.getName();
                Variable element;

                if (Objects.isNull(fieldClass)) {
                    element = unknown();
                } else {
                    element = new Variable(fieldType, fieldName, refRoute + "." + fieldName);
                }

                this.insideVariables[arrIdx++] = element;
            }
        } else {
            this.insideVariables = new Variable[0];
        }
    }

    public static Variable unknown() {
        return new Variable(null, null, StringSupport.emptyStr());
    }

    public static Variable describeFor(PsiParameter param) {
        return describeFor(param.getType(), param.getName());
    }

    public static Variable describeFor(PsiField field) {
        return describeFor(field.getType(), field.getName());
    }

    public static Variable describeFor(PsiType type, String name) {
        return new Variable(type, name, name);
    }

    public boolean hasVariableInside() {
        return isPlainObject && insideVariables != null && insideVariables.length > 0;
    }

    public VariableMatchResult canMatchVariable(Variable other) {
        return canMatchVariable(other.getPsiType(), other.getName());
    }

    public VariableMatchResult canMatchVariable(PsiType variableType, String variableName) {
        variableType = PsiTypeEnsurance.ensurePrimitiveToBoxed(variableType);
        if (this.psiType.equals(variableType) && this.name.equals(variableName)) {
            return VariableMatchResult.success(this);
        }
        if (this.isPlainObject) {
            for (Variable insideVariable : insideVariables) {
                VariableMatchResult innerResult = insideVariable.canMatchVariable(variableType, variableName);
                if (innerResult.success()) {
                    return innerResult;
                }
            }
        }
        return VariableMatchResult.failure();
    }

    // todo:lithiumnzinc 2024/8/13 16:21 > 在哪里放这两个方法比较好呢
    //  现在的问题在于 如果这里的 Variable 是抽象类，那么在构造函数里不方便操作 insideVariable 数组
    //  并且这里的方法我不想做统一的 而且我也不想在 Variable 上加个泛型
    //  还有一个原因就是 后续还会有兼容 List<T> 匹配和 Builder 类型的代码生成
    //  但是现在的情况下 感觉还是做成统一的比较合理一点
    public String methodTextOfGettingThis() {
        String[] separatedRoute = this.refRoute.split(ROUTE_SEPARATE_REGEX);
        StringBuilder res = new StringBuilder(separatedRoute[0]);
        if (separatedRoute.length > 1) {
            for (int i = 1; i < separatedRoute.length; i++) {
                res.append(".get")
                        .append(StringSupport.upperFirst(separatedRoute[i]))
                        .append("()");
            }
        }
        return res.toString();
    }


    public String methodTextOfSettingThis(MethodProcessContext ctx, String variableToSetThis) {
        if (!this.refRoute.contains(variableToSetThis)) {
            return StringSupport.emptyStr();
        }
        String[] separatedRoute = this.refRoute.split(ROUTE_SEPARATE_REGEX);
        StringBuilder res = new StringBuilder();

        ParametersOfMethod pom = ctx.getMethodSignature().getOrderedParameters();
        ReturnOfMethod rom = ctx.getMethodSignature().getReturnOfMethod();

        if (separatedRoute.length > 1) {
            for (int i = 0; i < separatedRoute.length - 1; i++) {
                String pair = separatedRoute[i] + "." + separatedRoute[i + 1];
                // todo:lithiumnzinc 2024/8/14 18:09 > 这里的 marker 需要改造成带有生命周期的
                if (ctx.isKnownRoute(pair)) {
                    continue;
                }

                StringBuilder completeRoute = new StringBuilder();
                for (int j = 0; j <= i + 1; j++) {
                    completeRoute.append(separatedRoute[j])
                            .append(".");
                }
                completeRoute.deleteCharAt(completeRoute.length() - 1);

                Variable variableInReturn = rom.findVariableByRoute(completeRoute.toString());

                res.append(SpecialCharacters.twoTabSpace())
                        .append(separatedRoute[i])
                        .append(".set")
                        .append(StringSupport.upperFirst(separatedRoute[i + 1]))
                        .append("(");

                if (variableInReturn.isPlainObject) {
                    List<Variable> plainObjects = rom.plainObjects();
                    if (plainObjects.contains(variableInReturn)) {
                        res.append(variableInReturn.getName());
                    }
                } else {
                    Variable variableInParam = pom.canMatchVariable(variableInReturn).getMatchedVariable();
                    res.append(variableInParam.methodTextOfGettingThis());
                }


                res.append(");\n");

                ctx.addKnownRoute(pair);
            }
        } else {
            res.append(separatedRoute[0])
                    .append(".set")
                    .append(StringSupport.upperFirst(this.name))
                    .append("(")
                    .append(ctx.getMethodSignature().getOrderedParameters()
                            .canMatchVariable(this)
                            .getMatchParameterGetterMethodText())
                    .append(");");
        }
        return res.toString();
    }

    private VariableMatchResult searchInside(String name) {
        if (StringSupport.isBlankStr(name)) {
            return VariableMatchResult.failure();
        }
        if (!isPlainObject) {
            return this.getName().equals(name) ? VariableMatchResult.success(this) : VariableMatchResult.failure();
        }
        for (Variable insideVariable : insideVariables) {
            if (insideVariable.getName().equals(name)) {
                return VariableMatchResult.success(insideVariable);
            }
        }
        return VariableMatchResult.failure();
    }

    public String getName() {
        return name;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public boolean isPlainObject() {
        return isPlainObject;
    }

    public Variable[] getInsideVariables() {
        return insideVariables;
    }

    public String getRefRoute() {
        return refRoute;
    }

    public String getFirstRefInRoute() {
        // todo:lithiumnzinc 2024/8/14 10:18 > what to do when it returns ''
        return refRoute.split(ROUTE_SEPARATE_REGEX)[0];
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                ", psiType=" + psiType +
                ", psiClass=" + psiClass +
                ", isPlainObject=" + isPlainObject +
                ", insideVariables=" + Arrays.toString(insideVariables) +
                ", refRoute='" + refRoute + '\'' +
                '}';
    }
}
