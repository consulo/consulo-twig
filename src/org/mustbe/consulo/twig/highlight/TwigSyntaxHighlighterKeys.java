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

package org.mustbe.consulo.twig.highlight;

import org.mustbe.consulo.twig.TwigLanguage;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public interface TwigSyntaxHighlighterKeys
{
	TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(TwigLanguage.INSTANCE, DefaultLanguageHighlighterColors.BLOCK_COMMENT);

	TextAttributesKey TAG = TextAttributesKey.createTextAttributesKey(TwigLanguage.INSTANCE, DefaultLanguageHighlighterColors.MARKUP_TAG);

	TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(TwigLanguage.INSTANCE, DefaultLanguageHighlighterColors.KEYWORD);
}
