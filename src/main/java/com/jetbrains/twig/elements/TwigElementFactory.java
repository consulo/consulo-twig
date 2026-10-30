/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jetbrains.twig.elements;

import com.jetbrains.twig.TwigLanguage;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileFactory;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2026-10-30
 */
public final class TwigElementFactory {
    private TwigElementFactory() {
    }

    /**
     * @return the first element of the type in the template text
     */
    @RequiredReadAction
    public static @Nullable PsiElement createPsiElement(Project project, String text, IElementType type) {
        PsiFile file = PsiFileFactory.getInstance(project).createFileFromText("dummy.twig", TwigLanguage.INSTANCE, text);
        return find(file, type);
    }

    @RequiredReadAction
    private static @Nullable PsiElement find(PsiElement root, IElementType type) {
        for (PsiElement child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNode().getElementType() == type) {
                return child;
            }
            PsiElement found = find(child, type);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
