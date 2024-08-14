package io.vortex.cvtr.process.data;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import io.vortex.cvtr.JavaClassSupport;
import io.vortex.cvtr.StringSupport;

import java.util.Arrays;

// 白芷禾
@Deprecated
public class MethodParameter {

    private static final String ROUTE_SEPARATOR_REGEX = "\\.";

    private final String parameterName;

    private final PsiClass parameterPsiClass;

    private final PsiType parameterPsiType;

    // 对应类的全限定名
    private final String classCanonicalName;

    private final boolean hasPlainObjectInside;

    private final boolean isPlainObject;

    private MethodParameter[] insideObjects;

    // todo:lithiumnzinc 2024/8/13 14:00 > 补全route xx.xy.yy.aaa
    private String route;

    public static MethodParameter describeFor(PsiParameter parameter) {
        String name = parameter.getName();
        PsiClass psiClass = PsiTypesUtil.getPsiClass(parameter.getType());
        if (psiClass == null) {
            throw new IllegalArgumentException("Cannot find class " + parameter.getType().getCanonicalText());
        }
        PsiField[] fields = psiClass.getFields();
        boolean hasPlainObjectInside = false;
        for (PsiField field : fields) {
            PsiType fieldType = field.getType();
            if (JavaClassSupport.Api.isPlainObject(fieldType)) {
                hasPlainObjectInside = true;
                break;
            }
        }
        // todo:lithiumnzinc 2024/8/13 14:06 > 这里可能有问题
        return new MethodParameter(name, parameter.getType(), psiClass, psiClass.getQualifiedName(), parameter.getName(), hasPlainObjectInside);
    }

    public static MethodParameter unknown() {
        return new MethodParameter(null, null, null, null, "");
    }

    private MethodParameter(String parameterName, PsiType parameterPsiType, PsiClass parameterPsiClass, String classCanonicalName, String route) {
        this(parameterName, parameterPsiType, parameterPsiClass, classCanonicalName, route, false);
    }

    private MethodParameter(String parameterName, PsiType parameterPsiType, PsiClass parameterPsiClass, String classCanonicalName, String route, boolean hasPlainObjectInside) {
        this.parameterName = parameterName;
        this.parameterPsiType = parameterPsiType;
        this.parameterPsiClass = parameterPsiClass;
        this.classCanonicalName = classCanonicalName;
        this.hasPlainObjectInside = hasPlainObjectInside;
        this.route = route;
        this.isPlainObject = JavaClassSupport.Api.isPlainObject(parameterPsiClass);

        if (isPlainObject) {
            this.insideObjects = new MethodParameter[parameterPsiClass.getAllFields().length];
            int index = 0;
            for (PsiField field : parameterPsiClass.getAllFields()) {
                PsiType fieldType = field.getType();
                PsiClass fieldClass = PsiTypesUtil.getPsiClass(field.getType());
                String fieldName = field.getName();
                MethodParameter element;
                if (fieldClass == null) {
                    element = MethodParameter.unknown();
                } else {
                    element = new MethodParameter(field.getName(), fieldType, fieldClass, fieldClass.getQualifiedName(), parameterName + "." + fieldName);
                }
                this.insideObjects[index++] = element;
            }
        } else {
            this.insideObjects = new MethodParameter[0];
        }
    }

    public String getParameterName() {
        return parameterName;
    }

    public PsiClass getParameterPsiClass() {
        return parameterPsiClass;
    }

    public PsiType getParameterPsiType() {
        return parameterPsiType;
    }

    public String getClassCanonicalName() {
        return classCanonicalName;
    }

    public boolean hasPlainObjectInside() {
        return this.hasPlainObjectInside;
    }

    public boolean isPlainObject() {
        return isPlainObject;
    }

//    public VariableMatchResult canMatchVariable(PsiType fieldType, String fieldName, String referenceRoute) {
//        if (this.isPlainObject) {
//            if (fieldType.equals(this.parameterPsiType) && this.parameterName.equals(fieldName)) {
//                return VariableMatchResult.success(this);
//            }
//
//            for (MethodParameter insideParameter : this.insideObjects) {
//                // format of variable referenceRoute: xx.getXXX().getXXX()
//                // 是自定义的类 那么要往下找
//                String finalRef;
//                if (StringSupport.isBlankStr(referenceRoute)) {
//                    finalRef = this.parameterName;
//                } else {
//                    finalRef = referenceRoute + "." + this.parameterName;
//                }
//                VariableMatchResult tempResult = insideParameter.canMatchVariable(fieldType, fieldName, finalRef);
//                if (tempResult.success()) {
//                    return tempResult;
//                }
//            }
//            return VariableMatchResult.failure();
//        } else {
//            if (this.parameterPsiType.equals(fieldType)
//                    // todo:lithiumnzinc 2024/8/12 17:49 > 这里不知道要不要匹配名字就是
//                    && this.parameterName.equals(fieldName)) {
//                String finalRef;
//                if (StringSupport.isBlankStr(referenceRoute)) {
//                    // 说明当前 methodParameter就是在匹配的 field
//                    finalRef = fieldName;
//                } else {
//                    // 说明是通过 get来的
//                    finalRef = referenceRoute + "." + this.parameterName;
//                }
//                return VariableMatchResult.success(this);
//            }
//        }
//        return VariableMatchResult.failure();
//    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MethodParameter)) {
            return false;
        }
        MethodParameter other = (MethodParameter) obj;
        if (StringSupport.isBlankStr(other.getParameterName())) {
            return false;
        }
        if (other.getParameterPsiClass() == null) {
            return false;
        }
        return other.getParameterName().equals(this.getParameterName())
                && other.getParameterPsiClass().equals(this.getParameterPsiClass());
    }

    /*
    这个 set 方法的逻辑，主要是考虑到，一般来说 route 的最后一个 . 后面的才是 Java 的内置类型
    所以前面的 set 不能像 get 一样直接一路 . 过去就行
     */

    // methodParameter 不应该调用它的 setter 所以不需要 set 的相关逻辑
    public String methodTextOfGettingThisParam() {
        String[] separatedRoute = route.split(ROUTE_SEPARATOR_REGEX);
        // 至少会有一个element
        StringBuilder res = new StringBuilder(separatedRoute[0]);
        if (separatedRoute.length > 1) {
            for (int i = 1; i < separatedRoute.length; i++) {
                res.append(".get")
                        .append(StringSupport.upperFirst(route))
                        .append("()");
            }
        }
        return res.toString();
    }

    @Override
    public String toString() {
        return "MethodParameter{" +
                "parameterName='" + parameterName + '\'' +
                ", parameterPsiClass=" + parameterPsiClass +
                ", parameterPsiType=" + parameterPsiType +
                ", classCanonicalName='" + classCanonicalName + '\'' +
                ", hasPlainObjectInside=" + hasPlainObjectInside +
                ", isPlainObject=" + isPlainObject +
                ", insideObjects=" + Arrays.toString(insideObjects) +
                ", route='" + route + '\'' +
                '}';
    }
}
