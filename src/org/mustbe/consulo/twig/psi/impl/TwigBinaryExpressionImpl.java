/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.twig.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.twig.psi.TwigBinaryExpression;
import org.mustbe.consulo.twig.psi.TwigTokens;
import org.mustbe.consulo.twig.psi.TwigVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigBinaryExpressionImpl extends TwigExpressionImpl implements TwigBinaryExpression
{
	public TwigBinaryExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	@Override
	public PsiElement getLeftElement()
	{
		return getFirstChild();
	}

	@NotNull
	@Override
	public IElementType getTokenType()
	{
		PsiElement element = findNotNullChildByType(TwigTokens.BINARY_TOKENS);
		return element.getNode().getElementType();
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
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
	public void accept(@NotNull TwigVisitor visitor)
	{
		visitor.visitBinaryExpression(this);
	}
}
