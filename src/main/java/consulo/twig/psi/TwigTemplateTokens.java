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

import consulo.language.ast.IElementType;
import consulo.language.impl.psi.template.TemplateDataElementType;
import consulo.twig.TwigLanguage;
/**
 * @author VISTALL
 * @since 02.11.13.
 */
public interface TwigTemplateTokens
{
	IElementType OUTER_ELEMENT_TYPE = new IElementType("TWIG_FRAGMENT", TwigLanguage.INSTANCE);

	TemplateDataElementType TEMPLATE_DATA = new TemplateDataElementType("TWIG_TEMPLATE_DATA", TwigLanguage.INSTANCE, TwigTokens.T_INLINE_HTML, OUTER_ELEMENT_TYPE);
}
