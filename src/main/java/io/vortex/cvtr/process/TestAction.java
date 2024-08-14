package io.vortex.cvtr.process;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class TestAction extends AnAction {

    CodeGenerateProcess process = new CodeGenerateProcess();

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        process.process(anActionEvent);
    }
}
