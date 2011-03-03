package org.jetbrains.jet.lang.resolve.java;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.JetScope;
import org.jetbrains.jet.lang.resolve.WritableFunctionGroup;
import org.jetbrains.jet.lang.types.*;

import java.util.Collections;

/**
 * @author abreslav
 */
public class JavaClassMembersScope implements JetScope {
    private final PsiClass psiClass;
    private final JavaSemanticServices semanticServices;
    private final boolean staticMembers;

    public JavaClassMembersScope(PsiClass psiClass, JavaSemanticServices semanticServices, boolean staticMembers) {
        this.psiClass = psiClass;
        this.semanticServices = semanticServices;
        this.staticMembers = staticMembers;
    }

    @Override
    public ClassDescriptor getClass(@NotNull String name) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public PropertyDescriptor getProperty(@NotNull String name) {
        PsiField field = psiClass.findFieldByName(name, true);
        if (field == null) return null;
        if (field.hasModifierProperty(PsiModifier.STATIC) != staticMembers) {
            return null;
        }

        PropertyDescriptorImpl propertyDescriptor = new PropertyDescriptorImpl(
                Collections.<Attribute>emptyList(),
                field.getName(),
                semanticServices.getTypeTransformer().transform(field.getType()));
        semanticServices.getTrace().recordDeclarationResolution(field, propertyDescriptor);
        return propertyDescriptor;
    }

    @NotNull
    @Override
    public FunctionGroup getFunctionGroup(@NotNull String name) {
        WritableFunctionGroup writableFunctionGroup = new WritableFunctionGroup(name);
        PsiMethod[] allMethods = psiClass.getAllMethods();
        for (PsiMethod method : allMethods) {
            if (method.hasModifierProperty(PsiModifier.STATIC) != staticMembers) {
                continue;
            }
            if (!name.equals(method.getName())) {
                 continue;
            }
            PsiParameter[] parameters = method.getParameterList().getParameters();
            FunctionDescriptorImpl functionDescriptor = new FunctionDescriptorImpl(
                    null,
                    Collections.<Attribute>emptyList(), // TODO
                    name,
                    Collections.<TypeParameterDescriptor>emptyList(), // TODO
                    semanticServices.getDescriptorResolver().resolveParameterDescriptors(parameters),
                    semanticServices.getTypeTransformer().transform(method.getReturnType())
            );
            semanticServices.getTrace().recordDeclarationResolution(method, functionDescriptor);
            writableFunctionGroup.addFunction(functionDescriptor);
        }
        return writableFunctionGroup;
    }

    @Override
    public ExtensionDescriptor getExtension(@NotNull String name) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public NamespaceDescriptor getNamespace(@NotNull String name) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public TypeParameterDescriptor getTypeParameter(@NotNull String name) {
        throw new UnsupportedOperationException(); // TODO
    }

    @NotNull
    @Override
    public Type getThisType() {
        throw new UnsupportedOperationException(); // TODO
    }
}
