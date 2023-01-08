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

package consulo.twig.psi.references;

import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;
import consulo.language.util.IncorrectOperationException;
import consulo.twig.psi.TwigConstantExpression;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
@ExtensionImpl
public class TwigConstantElementManipulator extends AbstractElementManipulator<TwigConstantExpression>
{
	@Override
	public TextRange getRangeInElement(TwigConstantExpression element)
	{
		int start = 0;
		int end = element.getTextLength();
		String text = element.getText();
		if(text.length() > 0 && text.charAt(0) == '"')
		{
			start = 1;
		}

		if(text.charAt(text.length() - 1) == '"')
		{
			end -= 1;
		}
		return new TextRange(start, end);
	}

	@Nonnull
	@Override
	public Class<TwigConstantExpression> getElementClass()
	{
		return TwigConstantExpression.class;
	}

	@Override
	public TwigConstantExpression handleContentChange(TwigConstantExpression twigConstantExpression, TextRange textRange, String s) throws IncorrectOperationException
	{
		return null;
	}
}
