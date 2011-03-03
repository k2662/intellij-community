package org.jetbrains.jet.lang.types;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.jet.lang.resolve.JetScope;

/**
 * @author abreslav
 */
public class BindingTrace {
    public static final BindingTrace DUMMY = new BindingTrace();

    public void recordExpressionType(@NotNull JetExpression expression, @NotNull Type type) {
    }

    public void recordReferenceResolution(@NotNull JetReferenceExpression expression, @NotNull DeclarationDescriptor descriptor) {

    }

    public void recordDeclarationResolution(@NotNull PsiElement declaration, @NotNull DeclarationDescriptor descriptor) {

    }

    public void recordTypeResolution(@NotNull JetTypeReference typeReference, @NotNull Type type) {

    }

    public void setToplevelScope(JetScope toplevelScope) {

    }
}
