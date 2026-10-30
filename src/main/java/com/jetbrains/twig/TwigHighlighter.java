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

import com.jetbrains.twig.lexer.TwigLexer;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;
import consulo.codeEditor.HighlighterColors;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 2013-11-02
 */
public class TwigHighlighter {
    public static class TwigFileHighlighter extends SyntaxHighlighterBase {
        private static final Map<IElementType, TextAttributesKey> KEYS = new HashMap<>();

        static {
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.COMMENT_START, TwigTokenTypes.COMMENT_TEXT, TwigTokenTypes.COMMENT_END), TwigHighlighterData.COMMENT);
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.STATEMENT_BLOCK_START, TwigTokenTypes.STATEMENT_BLOCK_END,
                TwigTokenTypes.PRINT_BLOCK_START, TwigTokenTypes.PRINT_BLOCK_END), TwigHighlighterData.TAG);
            fillMap(KEYS, TokenSet.orSet(TwigTokenTypes.KEYWORDS, TokenSet.create(TwigTokenTypes.TAG_NAME, TwigTokenTypes.RESERVED_ID)),
                TwigHighlighterData.KEYWORD);
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.STRING_TEXT, TwigTokenTypes.SINGLE_QUOTE, TwigTokenTypes.DOUBLE_QUOTE), TwigHighlighterData.STRING);
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.NUMBER), TwigHighlighterData.NUMBER);
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.IDENTIFIER), TwigHighlighterData.IDENTIFIER);
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.LBRACE, TwigTokenTypes.RBRACE, TwigTokenTypes.LBRACE_SQ, TwigTokenTypes.RBRACE_SQ,
                TwigTokenTypes.LBRACE_CURL, TwigTokenTypes.RBRACE_CURL), TwigHighlighterData.BRACES);
            fillMap(KEYS, TokenSet.create(TwigTokenTypes.EQ, TwigTokenTypes.EQ_EQ, TwigTokenTypes.NOT_EQ, TwigTokenTypes.LT, TwigTokenTypes.LE,
                TwigTokenTypes.GT, TwigTokenTypes.GE, TwigTokenTypes.PLUS, TwigTokenTypes.MINUS, TwigTokenTypes.MUL, TwigTokenTypes.DIV,
                TwigTokenTypes.FLOOR_DIV, TwigTokenTypes.MOD, TwigTokenTypes.POW, TwigTokenTypes.CONCAT, TwigTokenTypes.FILTER, TwigTokenTypes.DOT,
                TwigTokenTypes.RANGE, TwigTokenTypes.SPREAD, TwigTokenTypes.COMMA, TwigTokenTypes.COLON, TwigTokenTypes.QUESTION,
                TwigTokenTypes.NULL_COALESCE, TwigTokenTypes.ARROW), TwigHighlighterData.OPERATION);
            KEYS.put(TwigTokenTypes.BAD_CHARACTER, HighlighterColors.BAD_CHARACTER);
        }

        public TwigFileHighlighter() {
            this(null);
        }

        public TwigFileHighlighter(@Nullable Project project) {
        }

        @Override
        public Lexer getHighlightingLexer() {
            return new TwigLexer();
        }

        @Override
        public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
            return pack(KEYS.get(tokenType));
        }
    }
}
