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

package consulo.twig.psi.references;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.twig.psi.TwigBinaryExpression;
import consulo.twig.psi.TwigConstantExpression;
import consulo.twig.psi.TwigElement;
import consulo.twig.psi.TwigPsiUtil;
import consulo.twig.psi.TwigReferenceExpression;
import consulo.twig.psi.TwigTag;
import consulo.twig.psi.TwigTokens;
import consulo.twig.psi.impl.light.LightTwigVariableDeclaration;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Comparing;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigReferenceContributor extends PsiReferenceContributor
{
	private static class VariablePsiScopeProcessor extends BaseScopeProcessor
	{
		private List<PsiElement> myResults = new ArrayList<PsiElement>();
		private String myVariableName;
		private TwigElement myElement;

		public VariablePsiScopeProcessor(@Nullable TwigElement element)
		{
			myElement = element;
			myVariableName = element == null ? null : element.getText();
		}

		@Override
		public boolean execute(@Nonnull PsiElement element, ResolveState resolveState)
		{
			if(myElement == element)
			{
				return true;
			}

			if(isVariableDeclaration(element))
			{
				if(myVariableName == null || Comparing.equal(element.getText(), myVariableName))
				{
					myResults.add(element);
				}
			}
			return true;
		}

		public ResolveResult[] getResults()
		{
			ResolveResult[] rs = new ResolveResult[myResults.size()];
			for(int i = 0; i < rs.length; i++)
			{
				rs[i] = new PsiElementResolveResult(myResults.get(i));
			}
			return rs;
		}

		public List<PsiElement> getElements()
		{
			return myResults;
		}
	}

	public static boolean isNotReference(PsiElement expression)
	{
		return isVariableDeclaration(expression) /*TODO || isKeyword(expression)*/;
	}

	public static boolean isVariableDeclaration(PsiElement expression)
	{
		if(!(expression instanceof TwigReferenceExpression))
		{
			return false;
		}

		if(expression instanceof LightTwigVariableDeclaration)
		{
			return true;
		}

		PsiElement parent = expression.getParent();
		if(parent instanceof TwigTag)
		{
			// {% block REF %}
			if(Comparing.equal(((TwigTag) parent).getName(), "block"))
			{
				return true;
			}
		}
		else if(parent instanceof TwigBinaryExpression)
		{
			// {% for REF in REF2 %}
			IElementType tokenType = ((TwigBinaryExpression) parent).getTokenType();
			if(tokenType == TwigTokens.IN_KEYWORD && ((TwigBinaryExpression) parent).getLeftElement() == expression)
			{
				return true;
			}

			// {% set a = 1 %}
			if(tokenType == TwigTokens.EQ && ((TwigBinaryExpression) parent).getLeftElement() == expression)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar)
	{
		psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.psiElement(TwigReferenceExpression.class), new PsiReferenceProvider()
		{
			@Nonnull
			@Override
			public PsiReference[] getReferencesByElement(@Nonnull PsiElement element, @Nonnull ProcessingContext processingContext)
			{
				if(isNotReference(element))
				{
					return PsiReference.EMPTY_ARRAY;
				}

				return new PsiReference[]
				{
						new PsiPolyVariantReferenceBase<TwigElement>((TwigElement) element)
						{
							@Nonnull
							@Override
							public ResolveResult[] multiResolve(boolean b)
							{
								VariablePsiScopeProcessor processor = new VariablePsiScopeProcessor(getElement());

								TwigPsiUtil.treeWalkUp(processor, getElement(), getElement().getContainingFile());

								return processor.getResults();
							}

							@Nonnull
							@Override
							public Object[] getVariants()
							{
								VariablePsiScopeProcessor processor = new VariablePsiScopeProcessor(null);

								TwigPsiUtil.treeWalkUp(processor, getElement(), getElement().getContainingFile());

								List<PsiElement> elements = processor.getElements();
								List<LookupElement> lookupElements = new ArrayList<LookupElement>(elements.size());
								for(PsiElement psiElement : elements)
								{
									TwigReferenceExpression expression = (TwigReferenceExpression) psiElement;

									LookupElementBuilder lookupElement = LookupElementBuilder.create(psiElement.getText());
									lookupElement = lookupElement.withIcon(AllIcons.Nodes.Variable);
									lookupElement = lookupElement.withTypeText(expression.getType().getType());
									lookupElements.add(lookupElement);
								}
								return ArrayUtil.toObjectArray(lookupElements);
							}
						}
				};
			}
		});

		psiReferenceRegistrar.registerReferenceProvider(StandardPatterns.psiElement(TwigConstantExpression.class).withParent(TwigTag.class), new PsiReferenceProvider()
		{
			@Nonnull
			@Override
			public PsiReference[] getReferencesByElement(@Nonnull PsiElement element, @Nonnull ProcessingContext processingContext)
			{
				TwigTag parent = (TwigTag) element.getParent();
				if(Comparing.equal(parent.getName(), "extends"))
				{
					FileReferenceSet set = FileReferenceSet.createSet(element, true, false, false);
					return set.getAllReferences();
				}
				return new PsiReference[0];
			}
		});
	}
}
