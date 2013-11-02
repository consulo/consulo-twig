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

package org.mustbe.consulo.twig.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigBlock extends ASTWrapperPsiElement
{
	public TwigBlock(@NotNull ASTNode node)
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
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof TwigVisitor)
		{
			((TwigVisitor) visitor).visitBlock(this);
		}
		else
		{
			super.accept(visitor);
		}
	}
}
