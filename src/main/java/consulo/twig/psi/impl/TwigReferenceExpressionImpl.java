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
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.ReferenceProvidersRegistry;
import consulo.language.util.IncorrectOperationException;
import consulo.twig.psi.TwigReferenceExpression;
import consulo.twig.psi.TwigVisitor;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigReferenceExpressionImpl extends TwigExpressionImpl implements TwigReferenceExpression
{
	public TwigReferenceExpressionImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Nonnull
	@Override
	public PsiReference[] getReferences()
	{
		return ReferenceProvidersRegistry.getReferencesFromProviders(this);
	}

	@Override
	public void accept(@Nonnull TwigVisitor visitor)
	{
		visitor.visitReferenceExpression(this);
	}

	@Override
	public String getName()
	{
		return getText();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return this;
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}
}
