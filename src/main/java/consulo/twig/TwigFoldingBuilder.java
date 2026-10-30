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

package consulo.twig;

import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.elements.TwigComment;
import com.jetbrains.twig.elements.TwigElementTypes;
import com.jetbrains.twig.elements.TwigStatement;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.Document;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.editor.folding.FoldingBuilderEx;
import consulo.language.editor.folding.FoldingDescriptor;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Folds statements with an end tag and multi-line comments
 *
 * @author VISTALL
 * @since 2013-11-02
 */
@ExtensionImpl
public class TwigFoldingBuilder extends FoldingBuilderEx {
    @RequiredReadAction
    @Override
    public FoldingDescriptor[] buildFoldRegions(PsiElement root, Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        for (TwigStatement statement : PsiTreeUtil.findChildrenOfType(root, TwigStatement.class)) {
            PsiElement last = statement.getLastChild();
            if (statement.getFirstChild() != last && last != null && last.getNode().getElementType() == TwigElementTypes.TAG && isMultiLine(document, statement)) {
                descriptors.add(new FoldingDescriptor(statement.getNode(), statement.getTextRange()));
            }
        }
        for (TwigComment comment : PsiTreeUtil.findChildrenOfType(root, TwigComment.class)) {
            if (isMultiLine(document, comment)) {
                descriptors.add(new FoldingDescriptor(comment.getNode(), comment.getTextRange()));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }

    private static boolean isMultiLine(Document document, PsiElement element) {
        TextRange range = element.getTextRange();
        return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset());
    }

    @RequiredReadAction
    @Override
    public @Nullable String getPlaceholderText(ASTNode node) {
        if (node.getElementType() == TwigElementTypes.COMMENT) {
            return "{# ... #}";
        }
        ASTNode tag = node.getFirstChildNode();
        return tag == null ? "..." : tag.getText() + "...";
    }

    @Override
    public boolean isCollapsedByDefault(ASTNode node) {
        return false;
    }

    @Override
    public Language getLanguage() {
        return TwigLanguage.INSTANCE;
    }
}
