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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.twig.lexer.TwigLexer;
import org.mustbe.consulo.twig.psi.TwigTokens;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigSyntaxHighlighter extends SyntaxHighlighterBase
{
	private static Map<IElementType, TextAttributesKey> ourMap = new HashMap<IElementType, TextAttributesKey>()
	{
		{
			put(TwigTokens.COMMENT, TwigSyntaxHighlighterKeys.COMMENT);
			put(TwigTokens.BLOCK_NAME, TwigSyntaxHighlighterKeys.KEYWORD);
			put(TwigTokens.IS_KEYWORD, TwigSyntaxHighlighterKeys.KEYWORD);
			put(TwigTokens.IN_KEYWORD, TwigSyntaxHighlighterKeys.KEYWORD);
			put(TwigTokens.BAD_CHARACTER, HighlighterColors.BAD_CHARACTER);
			put(TwigTokens.STRING, TwigSyntaxHighlighterKeys.STRING);
			put(TwigTokens.DSTRING, TwigSyntaxHighlighterKeys.STRING);
		}
	};

	@NotNull
	@Override
	public Lexer getHighlightingLexer()
	{
		return new TwigLexer();
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType elementType)
	{
		return pack(ourMap.get(elementType));
	}
}
