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

import consulo.application.progress.ProgressIndicatorProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiInvalidElementAccessException;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigPsiUtil
{
	private static final Logger LOGGER = Logger.getInstance(TwigPsiUtil.class);

	private TwigPsiUtil()
	{
	}

	public static boolean treeWalkUp(@Nonnull PsiScopeProcessor processor, @Nonnull PsiElement entrance, @Nullable PsiElement maxScope)
	{
		return treeWalkUp(processor, entrance, maxScope, ResolveState.initial());
	}

	public static boolean treeWalkUp(@Nonnull final PsiScopeProcessor processor, @Nonnull final PsiElement entrance, @Nullable final PsiElement maxScope, @Nonnull final ResolveState state)
	{
		if(!entrance.isValid())
		{
			LOGGER.error(new PsiInvalidElementAccessException(entrance));
		}
		PsiElement prevParent = entrance;
		PsiElement scope = entrance;

		while(scope != null)
		{
			ProgressIndicatorProvider.checkCanceled();

			if(!scope.processDeclarations(processor, state, prevParent, entrance))
			{
				return false; // resolved
			}


			if(scope == maxScope)
			{
				break;
			}
			prevParent = scope;
			scope = prevParent.getContext();
			if(scope != null && scope != prevParent.getParent() && !scope.isValid())
			{
				break;
			}

		}

		return true;
	}

	public static boolean walkChildrenScopes(@Nonnull PsiElement thisElement, @Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, PsiElement place)
	{
		PsiElement child = null;
		if(lastParent != null && lastParent.getParent() == thisElement)
		{
			child = lastParent.getPrevSibling();
			if(child == null)
			{
				return true; // first element
			}
		}

		if(child == null)
		{
			child = thisElement.getLastChild();
		}

		while(child != null)
		{
			if(!child.processDeclarations(processor, state, null, place))
			{
				return false;
			}
			child = child.getPrevSibling();
		}

		return true;
	}

}
