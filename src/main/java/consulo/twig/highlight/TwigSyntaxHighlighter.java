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

import consulo.codeEditor.HighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;
import consulo.twig.lexer.TwigLexer;
import consulo.twig.psi.TwigTokens;

import jakarta.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigSyntaxHighlighter extends SyntaxHighlighterBase
{
	private static Map<IElementType, TextAttributesKey> ourMap = new HashMap<IElementType, TextAttributesKey>();

	static
	{
		ourMap.put(TwigTokens.COMMENT, TwigSyntaxHighlighterKeys.COMMENT);
		ourMap.put(TwigTokens.BLOCK_NAME, TwigSyntaxHighlighterKeys.KEYWORD);
		ourMap.put(TwigTokens.IS_KEYWORD, TwigSyntaxHighlighterKeys.KEYWORD);
		ourMap.put(TwigTokens.IN_KEYWORD, TwigSyntaxHighlighterKeys.KEYWORD);
		ourMap.put(TwigTokens.BAD_CHARACTER, HighlighterColors.BAD_CHARACTER);
		ourMap.put(TwigTokens.STRING, TwigSyntaxHighlighterKeys.STRING);
		ourMap.put(TwigTokens.DSTRING, TwigSyntaxHighlighterKeys.STRING);
	}

	@Nonnull
	@Override
	public Lexer getHighlightingLexer()
	{
		return new TwigLexer();
	}

	@Nonnull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType elementType)
	{
		return pack(ourMap.get(elementType));
	}
}
