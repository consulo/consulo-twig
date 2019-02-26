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

package consulo.twig.psi;

import java.util.List;

import javax.annotation.Nonnull;

import consulo.twig.TwigFileType;
import consulo.twig.TwigLanguage;
import consulo.twig.psi.impl.light.LightTwigVariableDeclaration;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigFile extends PsiFileBase
{
	public TwigFile(@Nonnull FileViewProvider viewProvider)
	{
		super(viewProvider, TwigLanguage.INSTANCE);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
	{
		for(TwigVariableProvider provider : TwigVariableProvider.EP_NAME.getExtensionList())
		{
			List<TwigVariable> variables = provider.getVariables(this);
			for(TwigVariable variable : variables)
			{
				LightTwigVariableDeclaration twigReferenceExpression = new LightTwigVariableDeclaration(this, variable);
				if(variable.getVariableType().getNavElement() == null)
				{
					twigReferenceExpression.setNavigationElement(this);
				}

				if(!processor.execute(twigReferenceExpression, state))
				{
					return false;
				}
			}
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}

	@Nonnull
	@Override
	public FileType getFileType()
	{
		return TwigFileType.INSTANCE;
	}
}
