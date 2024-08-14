package io.vortex.cvtr.process;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import io.vortex.cvtr.JavaClassSupport;
import io.vortex.cvtr.process.data.MethodProcessContext;
import io.vortex.cvtr.process.data.MethodSignature;
import io.vortex.cvtr.process.data.ParametersOfMethod;
import io.vortex.cvtr.process.data.ReturnOfMethod;

import java.util.Objects;

public class CodeGenerateProcess {

    public void process(AnActionEvent e) {
        PsiElement element = e.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            if (methodDontNeedToProcess(psiMethod)) {
                return;
            } else {
                progress(psiMethod);
            }
        }
    }

    @SuppressWarnings("all")
    private boolean methodDontNeedToProcess(PsiMethod psiMethod) {
        return psiMethod.getParameterList().isEmpty() || PsiType.VOID.equals(psiMethod.getReturnType());
    }

    CodeGenerator generator = new CodeGenerator();

    private void progress(PsiMethod curMethod) {

        MethodProcessContext context = MethodProcessContext.wrap(curMethod);
        MethodSignature methodSignature = context.getMethodSignature();
        ReturnOfMethod returnOfMethod = methodSignature.getReturnOfMethod();
        ParametersOfMethod parameters = methodSignature.getOrderedParameters();


        GeneratorV2 rg = new GeneratorV2();
        rg.generateCode(context);

        Document documentOfEditor = FileDocumentManager.getInstance()
                .getDocument(curMethod.getContainingFile().getVirtualFile());

        WriteCommandAction.runWriteCommandAction(curMethod.getProject(),
                () -> {
                    assert documentOfEditor != null;
                    documentOfEditor.insertString(Objects.requireNonNull(curMethod.getBody()).getTextOffset() + 1, "\n" + context.getCodeToWave());
                });

    }


    static boolean fieldIsPlainObject(PsiField field) {
        return JavaClassSupport.Api.isPlainObject(field.getType());
    }

}
