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

package org.mustbe.consulo.twig.psi.references;

import org.mustbe.consulo.twig.psi.TwigConstantExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
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

	@Override
	public TwigConstantExpression handleContentChange(TwigConstantExpression twigConstantExpression, TextRange textRange, String s) throws IncorrectOperationException
	{
		return null;
	}
}
