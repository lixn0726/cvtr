package io.vortex.cvtr.codegen;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import io.vortex.cvtr.ProjectNotification;
import io.vortex.cvtr.model.MethodProcessContext;

import java.util.Objects;

public class ConversionCodeGenerator {

    private static final CodeGen codeGen = new CodeGen();

    public void process(AnActionEvent e) {
        PsiElement element = e.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            if (methodNeedToProcess(psiMethod)) {
                doProcess(psiMethod);
            } else {
                ProjectNotification.notifyError(psiMethod,
                        "Cvtr Convert Error",
                        "Selected method is not suitable for generating code.");
            }
        }
    }

    private boolean methodNeedToProcess(PsiMethod psiMethod) {
        return !methodDontNeedToProcess(psiMethod);
    }

    @SuppressWarnings("all")
    private boolean methodDontNeedToProcess(PsiMethod psiMethod) {
        return psiMethod.getParameterList().isEmpty() || PsiType.VOID.equals(psiMethod.getReturnType());
    }

    private void doProcess(PsiMethod curMethod) {
        try {
            MethodProcessContext context = prepareContext(curMethod);
            codeGen.generateCode(context);
            writeCodeToEditor(context);
            ProjectNotification.notifyInfo(context,
                    "Cvtr Convert Success",
                    "Conversion code was generated. Please check again to make sure it's right.");
        } catch (Exception e) {
            ProjectNotification.notifyError(curMethod,
                    "Cvtr Convert Error",
                    "Conversion error. Cause: " + e.getMessage());
        }
    }

    private static MethodProcessContext prepareContext(PsiMethod psiMethod) {
        return MethodProcessContext.wrap(psiMethod);
    }

    private static void writeCodeToEditor(MethodProcessContext ctx) {
        PsiMethod curMethod = ctx.getMethod();
        Document documentOfEditor = FileDocumentManager.getInstance()
                .getDocument(curMethod.getContainingFile().getVirtualFile());
        WriteCommandAction.runWriteCommandAction(curMethod.getProject(),
                () -> {
                    assert documentOfEditor != null;
                    documentOfEditor.insertString(Objects.requireNonNull(curMethod.getBody()).getTextOffset() + 1, "\n" + ctx.getCodeToWave());
                });
    }

}
