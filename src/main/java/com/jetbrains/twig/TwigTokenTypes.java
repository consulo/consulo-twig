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

import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.ast.TokenType;

/**
 * Tokens of Twig, in the shape of PhpStorm's {@code TwigTokenTypes}.
 *
 * @author VISTALL
 * @since 2026-10-30
 */
public interface TwigTokenTypes {
    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

    /**
     * Template text between the Twig blocks
     */
    IElementType TEXT = token("TEXT");

    /**
     * {@code {%}, also with the whitespace control {@code {%-} and {@code {%~}
     */
    IElementType STATEMENT_BLOCK_START = token("STATEMENT_BLOCK_START");
    IElementType STATEMENT_BLOCK_END = token("STATEMENT_BLOCK_END");
    IElementType PRINT_BLOCK_START = token("PRINT_BLOCK_START");
    IElementType PRINT_BLOCK_END = token("PRINT_BLOCK_END");
    IElementType COMMENT_START = token("COMMENT_START");
    IElementType COMMENT_END = token("COMMENT_END");
    /**
     * Comment body without the delimiters and the leading whitespace
     */
    IElementType COMMENT_TEXT = token("COMMENT_TEXT");

    /**
     * The word after {@code {%}
     */
    IElementType TAG_NAME = token("TAG_NAME");
    IElementType IDENTIFIER = token("IDENTIFIER");
    /**
     * {@code _self}, {@code _context}, {@code _charset}
     */
    IElementType RESERVED_ID = token("RESERVED_ID");
    IElementType NUMBER = token("NUMBER");
    /**
     * String content without the quotes
     */
    IElementType STRING_TEXT = token("STRING_TEXT");
    IElementType SINGLE_QUOTE = token("SINGLE_QUOTE");
    IElementType DOUBLE_QUOTE = token("DOUBLE_QUOTE");

    IElementType AND = token("AND");
    IElementType OR = token("OR");
    IElementType NOT = token("NOT");
    IElementType IN = token("IN");
    IElementType IS = token("IS");
    /**
     * {@code as} of import and from tags
     */
    IElementType AS_KEYWORD = token("AS_KEYWORD");
    /**
     * {@code import} of from tags
     */
    IElementType IMPORT_KEYWORD = token("IMPORT_KEYWORD");
    /**
     * Not produced, {@code with} is an identifier
     */
    IElementType WITH_KEYWORD = token("WITH_KEYWORD");
    /**
     * Not produced, {@code only} is an identifier
     */
    IElementType ONLY_KEYWORD = token("ONLY_KEYWORD");

    IElementType EQ = token("EQ");
    IElementType EQ_EQ = token("EQ_EQ");
    IElementType NOT_EQ = token("NOT_EQ");
    IElementType LT = token("LT");
    IElementType LE = token("LE");
    IElementType GT = token("GT");
    IElementType GE = token("GE");
    IElementType PLUS = token("PLUS");
    IElementType MINUS = token("MINUS");
    IElementType MUL = token("MUL");
    IElementType DIV = token("DIV");
    IElementType FLOOR_DIV = token("FLOOR_DIV");
    IElementType MOD = token("MOD");
    IElementType POW = token("POW");
    IElementType CONCAT = token("CONCAT");
    IElementType FILTER = token("FILTER");
    IElementType DOT = token("DOT");
    IElementType RANGE = token("RANGE");
    IElementType SPREAD = token("SPREAD");
    IElementType COMMA = token("COMMA");
    IElementType COLON = token("COLON");
    IElementType QUESTION = token("QUESTION");
    IElementType NULL_COALESCE = token("NULL_COALESCE");
    IElementType ARROW = token("ARROW");
    IElementType LBRACE = token("LBRACE");
    IElementType RBRACE = token("RBRACE");
    IElementType LBRACE_SQ = token("LBRACE_SQ");
    IElementType RBRACE_SQ = token("RBRACE_SQ");
    IElementType LBRACE_CURL = token("LBRACE_CURL");
    IElementType RBRACE_CURL = token("RBRACE_CURL");

    TokenSet STRING_LITERALS = TokenSet.create(STRING_TEXT);
    TokenSet QUOTES = TokenSet.create(SINGLE_QUOTE, DOUBLE_QUOTE);
    TokenSet KEYWORDS = TokenSet.create(AND, OR, NOT, IN, IS, AS_KEYWORD, IMPORT_KEYWORD, WITH_KEYWORD, ONLY_KEYWORD);
    TokenSet BLOCK_STARTS = TokenSet.create(STATEMENT_BLOCK_START, PRINT_BLOCK_START, COMMENT_START);
    TokenSet BLOCK_ENDS = TokenSet.create(STATEMENT_BLOCK_END, PRINT_BLOCK_END, COMMENT_END);

    private static IElementType token(String debugName) {
        return new IElementType(debugName, TwigLanguage.INSTANCE);
    }
}
