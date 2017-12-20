/*
 * Copyright 2013-2016 consulo.io
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

import org.jetbrains.annotations.NotNull;
import consulo.twig.psi.TwigBlock;
import consulo.twig.psi.TwigElements;
import consulo.twig.psi.TwigTokens;
import consulo.twig.table.TwigTable;
import consulo.twig.table.TwigTableBlock;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import consulo.annotations.RequiredReadAction;
import consulo.codeInsight.completion.CompletionProvider;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigCompletionProvider extends CompletionContributor
{
	public TwigCompletionProvider()
	{
		extend(CompletionType.BASIC, StandardPatterns.psiElement(TwigTokens.BLOCK_NAME), new CompletionProvider()
		{
			@RequiredReadAction
			@Override
			public void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet)
			{
				PsiElement originalPosition = completionParameters.getPosition();
				if(originalPosition.getParent().getNode().getElementType() == TwigElements.CLOSE_TAG)
				{
					TwigBlock parent = (TwigBlock) originalPosition.getParent().getParent();

					completionResultSet.addElement(LookupElementBuilder.create("end" + parent.getOpenTag().getOpenedTagName()).withBoldness(true));
				}

				for(TwigTableBlock block : TwigTable.INSTANCE.getBlocks())
				{
					completionResultSet.addElement(LookupElementBuilder.create(block.getName()).withBoldness(true));
				}
			}
		});
	}
}
