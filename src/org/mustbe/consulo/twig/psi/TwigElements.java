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

import org.mustbe.consulo.twig.TwigLanguage;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IElementTypeAsPsiFactory;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public interface TwigElements
{
	IElementType OPEN_TAG = new IElementTypeAsPsiFactory("OPEN_TAG", TwigLanguage.INSTANCE, TwigTag.class);

	IElementType CLOSE_TAG = new IElementTypeAsPsiFactory("CLOSE_TAG", TwigLanguage.INSTANCE, TwigTag.class);

	IElementType BLOCK = new IElementTypeAsPsiFactory("BLOCK", TwigLanguage.INSTANCE, TwigBlock.class);

	IElementType EXPRESSION_BODY = new IElementTypeAsPsiFactory("EXPRESSION_BODY", TwigLanguage.INSTANCE, TwigExpressionBody.class);

	IElementType CONSTANT_EXPRESSION = new IElementTypeAsPsiFactory("CONSTANT_EXPRESSION", TwigLanguage.INSTANCE, TwigConstantExpression.class);

	IElementType REFERENCE_EXPRESSION = new IElementTypeAsPsiFactory("REFERENCE_EXPRESSION", TwigLanguage.INSTANCE, TwigReferenceExpression.class);

	IElementType BINARY_EXPRESSION = new IElementTypeAsPsiFactory("BINARY_EXPRESSION", TwigLanguage.INSTANCE, TwigBinaryExpression.class);
}
