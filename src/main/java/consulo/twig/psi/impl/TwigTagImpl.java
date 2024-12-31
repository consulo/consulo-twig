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
import consulo.language.util.IncorrectOperationException;
import consulo.twig.psi.TwigPsiUtil;
import consulo.twig.psi.TwigTag;
import consulo.twig.psi.TwigTokens;
import consulo.twig.psi.TwigVisitor;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigTagImpl extends TwigElementImpl implements TwigTag
{
	public TwigTagImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@Nonnull TwigVisitor visitor)
	{
		visitor.visitTag(this);
	}

	public String getOpenedTagName()
	{
		String name = getName();
		if(name == null)
		{
			return null;
		}
		if(StringUtil.startsWith(name, "end"))
		{
			return name.substring(3, name.length());
		}

		return name;
	}

	@Nonnull
	@Override
	public PsiElement getOpenElement()
	{
		return findNotNullChildByType(TwigTokens.STMT_OPEN);
	}

	@Nullable
	@Override
	public PsiElement getCloseElement()
	{
		return findChildByType(TwigTokens.STMT_CLOSE);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		for(PsiElement element : getChildren())
		{
			if(!TwigPsiUtil.treeWalkUp(processor, element, element, state))
			{
				return false;
			}
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}

	@Override
	public String getName()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? null : nameIdentifier.getText();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(TwigTokens.BLOCK_NAME);
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}
}
