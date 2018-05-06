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

package consulo.twig.psi.impl.light;

import org.jetbrains.annotations.NonNls;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.twig.TwigLanguage;
import consulo.twig.psi.TwigReferenceExpression;
import consulo.twig.psi.TwigVariable;
import consulo.twig.psi.TwigVariableType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class LightTwigVariableDeclaration extends LightElement implements TwigReferenceExpression
{
	private TwigVariable myVariable;

	public LightTwigVariableDeclaration(PsiFile psiFile, TwigVariable variable)
	{
		super(psiFile.getManager(), TwigLanguage.INSTANCE);
		myVariable = variable;

		setNavigationElement(psiFile);
	}

	@Override
	public String toString()
	{
		return "TwigLightReference: " + myVariable.getName();
	}

	@Override
	public String getName()
	{
		return myVariable.getName();
	}

	@Override
	public String getText()
	{
		return getName();
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

	@Override
	public TwigVariableType getType()
	{
		return myVariable.getVariableType();
	}
}
