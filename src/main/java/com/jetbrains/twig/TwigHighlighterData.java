/*
 * Copyright 2013-2026 consulo.io
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

package com.jetbrains.twig;

import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.colorScheme.TextAttributesKey;

/**
 * Highlighting keys of Twig, in the shape of PhpStorm's {@code TwigHighlighterData}
 *
 * @author VISTALL
 * @since 2026-10-30
 */
public interface TwigHighlighterData {
    /**
     * Background of the {@code {% %}} and {@code {{ }}} blocks
     */
    TextAttributesKey TWIG_TEMPLATE = TextAttributesKey.createTextAttributesKey("TWIG_TEMPLATE", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("TWIG_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    TextAttributesKey TAG = TextAttributesKey.createTextAttributesKey("TWIG_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("TWIG_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("TWIG_STRING", DefaultLanguageHighlighterColors.STRING);
    TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("TWIG_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("TWIG_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
    TextAttributesKey OPERATION = TextAttributesKey.createTextAttributesKey("TWIG_OPERATION", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("TWIG_BRACES", DefaultLanguageHighlighterColors.BRACES);
}
