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

import consulo.language.psi.PsiElementVisitor;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigVisitor extends PsiElementVisitor
{
	public void visitBlock(TwigBlock block)
	{
		visitElement(block);
	}

	public void visitTag(TwigTag tag)
	{
		visitElement(tag);
	}

	public void visitExpressionBody(TwigExpressionBody twigExpression)
	{
		visitElement(twigExpression);
	}

	public void visitBinaryExpression(TwigBinaryExpression twigBinaryExpression)
	{
		visitElement(twigBinaryExpression);
	}

	public void visitConstantExpression(TwigConstantExpression twigConstantExpression)
	{
		visitElement(twigConstantExpression);
	}

	public void visitReferenceExpression(TwigReferenceExpression twigReferenceExpression)
	{
		visitElement(twigReferenceExpression);
	}
}
