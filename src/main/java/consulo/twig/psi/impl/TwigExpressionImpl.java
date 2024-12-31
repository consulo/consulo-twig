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
import consulo.twig.psi.TwigExpression;
import consulo.twig.psi.TwigVariableType;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public abstract class TwigExpressionImpl extends TwigElementImpl implements TwigExpression
{
	public TwigExpressionImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Override
	public TwigVariableType getType()
	{
		return TwigVariableType.UNKNOWN;
	}
}
