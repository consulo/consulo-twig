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

package consulo.twig.completion;

import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigStatement;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.StandardPatterns;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import consulo.twig.table.TwigTable;
import consulo.twig.table.TwigTableBlock;

/**
 * Tag names after {@code {%}, the end tag of the enclosing statement first
 *
 * @author VISTALL
 * @since 2013-11-02
 */
@ExtensionImpl
public class TwigCompletionProvider extends CompletionContributor {
    public TwigCompletionProvider() {
        extend(CompletionType.BASIC, StandardPatterns.psiElement(TwigTokenTypes.TAG_NAME), new CompletionProvider() {
            @RequiredReadAction
            @Override
            public void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
                PsiElement tag = parameters.getPosition().getParent();
                PsiElement statement = tag == null ? null : tag.getParent();
                PsiElement openTag = statement instanceof TwigStatement ? statement.getFirstChild() : null;
                if (openTag != null && openTag != tag) {
                    ASTNode openTagName = openTag.getNode().findChildByType(TwigTokenTypes.TAG_NAME);
                    if (openTagName != null) {
                        result.addElement(LookupElementBuilder.create("end" + openTagName.getText()).bold());
                    }
                }
                for (TwigTableBlock block : TwigTable.INSTANCE.getBlocks()) {
                    result.addElement(LookupElementBuilder.create(block.getName()).bold());
                }
            }
        });
    }

    @Override
    public Language getLanguage() {
        return TwigLanguage.INSTANCE;
    }
}
