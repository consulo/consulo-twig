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

package consulo.twig.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.twig.psi.TwigBinaryExpression;
import consulo.twig.psi.TwigTokens;
import consulo.twig.psi.TwigVisitor;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigBinaryExpressionImpl extends TwigExpressionImpl implements TwigBinaryExpression
{
	public TwigBinaryExpressionImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Nonnull
	@Override
	public PsiElement getLeftElement()
	{
		return getFirstChild();
	}

	@Nonnull
	@Override
	public IElementType getTokenType()
	{
		PsiElement element = findNotNullChildByType(TwigTokens.BINARY_TOKENS);
		return element.getNode().getElementType();
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		for(PsiElement element : getChildren())
		{
			if(!processor.execute(element, state))
			{
				return false;
			}
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}

	@Override
	public void accept(@Nonnull TwigVisitor visitor)
	{
		visitor.visitBinaryExpression(this);
	}
}
