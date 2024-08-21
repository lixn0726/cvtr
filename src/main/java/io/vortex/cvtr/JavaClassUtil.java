package io.vortex.cvtr;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JavaClassUtil {

    private static class JdkTypesSupport {

        public static final String arraySign = "[]";

        public static final List<String> javaPrimitiveTypes = new ArrayList<String>() {{
            add("int");
            add("long");
            add("float");
            add("double");
            add("short");
            add("byte");
            add("boolean");
            add("char");
        }};

        public static final List<String> javaBoxingTypes = new ArrayList<String>() {{
            add("java.lang.Integer");
            add("java.lang.Long");
            add("java.lang.Float");
            add("java.lang.Double");
            add("java.lang.Short");
            add("java.lang.Byte");
            add("java.lang.Boolean");
            add("java.lang.Character");
            add("java.lang.String");
        }};

        public static final List<String> javaCollectionTypes = new ArrayList<String>() {{
            add("java.util.Collection");
        }};

        public static final List<String> javaListTypes = new ArrayList<String>() {{
            add("java.util.List");
        }};

        public static final List<String> javaMapTypes = new ArrayList<String>() {{
            add("java.util.Map");
        }};

    }

    public static class Api {

        public static boolean isJdkType(PsiClass type) {
            // qualified NOTICE
            return isJdkType(type.getQualifiedName());
        }

        public static boolean isJdkType(String type) {
            return StringUtil.isBlankStr(type) || type.startsWith("java");
        }

        public static boolean isPrimitiveType(PsiClass type) {
            return isPrimitiveType(type.getName());
        }

        public static boolean isPrimitiveType(String type) {
            return JdkTypesSupport.javaPrimitiveTypes.contains(type);
        }

        public static boolean isArrayType(PsiType type) {
            PsiClass clz = PsiTypesUtil.getPsiClass(type);
            return !Objects.isNull(clz) && isArrayType(clz);
        }

        public static boolean isArrayType(PsiClass type) {
            return isArrayType(type.getName());
        }

        public static boolean isArrayType(String type) {
            if (StringUtil.isBlankStr(type)) {
                return false;
            }
            return type.endsWith("[]");
        }

        public static boolean isEnumType(PsiClass psiClass) {
            return psiClass.isEnum();
        }

        public static boolean isOptionalType(PsiClass psiClass) {
            return isOptionalType(psiClass.getName());
        }

        public static boolean isOptionalType(String type) {
            return "optional".equalsIgnoreCase(type);
        }

        public static boolean isBoxingType(PsiClass type) {
            return isBoxingType(type.getName());
        }

        public static boolean isBoxingType(String type) {
            return JdkTypesSupport.javaBoxingTypes.contains(type);
        }

        public static boolean isCollectionType(PsiType type) {
            PsiClass clz = PsiTypesUtil.getPsiClass(type);
            return !Objects.isNull(clz) && isCollectionType(clz);
        }

        public static boolean isCollectionType(PsiClass clz) {
            return isListType(clz) || isMapType(clz);
        }

        public static boolean isListType(PsiType psiType) {
            PsiClass clz = PsiTypesUtil.getPsiClass(psiType);
            return !Objects.isNull(clz) && isListType(clz);
        }

        public static boolean isListType(PsiClass psiClass) {
            String qualifiedName = psiClass.getQualifiedName();
            if (StringUtil.isBlankStr(qualifiedName)) {
                return false;
            }
            if (psiClass.isInterface()) {
                return JdkTypesSupport.javaListTypes.stream()
                        .anyMatch(qualifiedName::equalsIgnoreCase);
            } else if (psiClass.getImplementsList() != null) {
                for (PsiJavaCodeReferenceElement type : psiClass.getImplementsList().getReferenceElements()) {
                    if (JdkTypesSupport.javaListTypes.stream()
                            .anyMatch(listTypeStr -> type.getQualifiedName().contains(listTypeStr))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isMapType(PsiClass psiClass) {
            String qualifiedName = psiClass.getQualifiedName();
            if (StringUtil.isBlankStr(qualifiedName)) {
                return false;
            }
            if (psiClass.isInterface()) {
                return JdkTypesSupport.javaMapTypes.stream()
                        .anyMatch(qualifiedName::equalsIgnoreCase);
            } else if (psiClass.getImplementsList() != null) {
                for (PsiJavaCodeReferenceElement type : psiClass.getImplementsList().getReferenceElements()) {
                    if (JdkTypesSupport.javaMapTypes.stream()
                            .anyMatch(listTypeStr -> type.getQualifiedName().contains(listTypeStr))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static boolean isNotPlainObjectType(PsiType psiType) {
            return !isPlainObject(psiType);
        }

        public static boolean isNotPlainObjectType(PsiClass psiClass) {
            return !isPlainObject(psiClass);
        }

        public static boolean isPlainObject(PsiType psiType) {
            PsiClass psiClass = PsiTypesUtil.getPsiClass(psiType);
            if (psiClass == null) {
                return false;
            }
            return isPlainObject(psiClass);
        }

        public static boolean isPlainObject(PsiClass psiClass) {
            if (Objects.isNull(psiClass)) {
                return false;
            }
            return !isPrimitiveType(psiClass)
                    && !isEnumType(psiClass)
                    && !isBoxingType(psiClass)
                    && !isArrayType(psiClass)
                    && !isListType(psiClass)
                    && !isMapType(psiClass)
                    && !isOptionalType(psiClass)
                    && !isJdkType(psiClass);
        }

    }

}
