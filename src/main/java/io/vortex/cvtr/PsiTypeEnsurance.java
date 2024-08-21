package io.vortex.cvtr;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Inject attributes when the plugin started.
public class PsiTypeEnsurance implements ProjectActivity {

    private static PsiTypeEnsurance instance;

    private PsiManager psiManager;

    public static PsiType ensurePrimitiveToBoxed(PsiType origin) {
        return instance.ensureTypeNotPrimitive(origin);
    }

    public PsiType ensureTypeNotPrimitive(PsiType origin) {
        if (instance == null) {
            throw new IllegalStateException("PsiTypeEnsurance not initialized");
        }
        if (origin instanceof PsiPrimitiveType) {
            origin = ((PsiPrimitiveType) origin).getBoxedType(psiManager, GlobalSearchScope.allScope(psiManager.getProject()));
        }
        return origin;
    }

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        this.psiManager = PsiManager.getInstance(project);

        if (instance == null) {
            instance = this;
        }

        return null;
    }
}
