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
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.twig.psi.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigBlockImpl extends TwigElementImpl implements TwigBlock
{
	public TwigBlockImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Nonnull
	public TwigTag getOpenTag()
	{
		return (TwigTag) findNotNullChildByType(TwigElements.OPEN_TAG);
	}

	@Nullable
	public TwigTag getCloseTag()
	{
		return (TwigTag) findChildByType(TwigElements.CLOSE_TAG);
	}

	@Override
	public void accept(@Nonnull TwigVisitor visitor)
	{
		visitor.visitBlock(this);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		TwigTag openTag = getOpenTag();
		if(!TwigPsiUtil.treeWalkUp(processor, openTag, openTag, state))
		{
			return false;
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}
}
