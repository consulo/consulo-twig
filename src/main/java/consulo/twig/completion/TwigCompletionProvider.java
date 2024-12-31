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

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.completion.*;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.StandardPatterns;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import consulo.twig.TwigLanguage;
import consulo.twig.psi.TwigBlock;
import consulo.twig.psi.TwigElements;
import consulo.twig.psi.TwigTokens;
import consulo.twig.table.TwigTable;
import consulo.twig.table.TwigTableBlock;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
@ExtensionImpl
public class TwigCompletionProvider extends CompletionContributor
{
	public TwigCompletionProvider()
	{
		extend(CompletionType.BASIC, StandardPatterns.psiElement(TwigTokens.BLOCK_NAME), new CompletionProvider()
		{
			@RequiredReadAction
			@Override
			public void addCompletions(@Nonnull CompletionParameters completionParameters, ProcessingContext processingContext, @Nonnull CompletionResultSet completionResultSet)
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

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return TwigLanguage.INSTANCE;
	}
}
