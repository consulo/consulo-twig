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

package consulo.twig.highlight;

import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.colorScheme.TextAttributesKey;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public interface TwigSyntaxHighlighterKeys
{
	TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("TWIG_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);

	TextAttributesKey TAG = TextAttributesKey.createTextAttributesKey("TWIG_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);

	TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("TWIG_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

	TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("TWIG_STRING", DefaultLanguageHighlighterColors.STRING);
}
