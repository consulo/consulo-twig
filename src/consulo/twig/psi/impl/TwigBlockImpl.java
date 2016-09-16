/*
 * Copyright 2013-2014 must-be.org
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.twig.psi.TwigBlock;
import consulo.twig.psi.TwigElements;
import consulo.twig.psi.TwigPsiUtil;
import consulo.twig.psi.TwigTag;
import consulo.twig.psi.TwigVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigBlockImpl extends TwigElementImpl implements TwigBlock
{
	public TwigBlockImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
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
	public void accept(@NotNull TwigVisitor visitor)
	{
		visitor.visitBlock(this);
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place)
	{
		TwigTag openTag = getOpenTag();
		if(!TwigPsiUtil.treeWalkUp(processor, openTag, openTag, state))
		{
			return false;
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}
}
