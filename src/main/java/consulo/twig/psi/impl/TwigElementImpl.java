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
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.twig.psi.TwigElement;
import consulo.twig.psi.TwigVisitor;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public abstract class TwigElementImpl extends ASTWrapperPsiElement implements TwigElement
{
	public TwigElementImpl(@Nonnull ASTNode node)
	{
		super(node);
	}


	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof TwigVisitor)
		{
			accept((TwigVisitor) visitor);
		}
		else
		{
			super.accept(visitor);
		}
	}

	public abstract void accept(@Nonnull TwigVisitor visitor);
}
