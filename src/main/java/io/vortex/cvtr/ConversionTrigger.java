package io.vortex.cvtr;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.vortex.cvtr.codegen.ConversionCodeGenerator;
import org.jetbrains.annotations.NotNull;
// delegate
public class ConversionTrigger extends AnAction {

    private static final ConversionCodeGenerator generator = new ConversionCodeGenerator();

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        generator.process(anActionEvent);
    }
}
