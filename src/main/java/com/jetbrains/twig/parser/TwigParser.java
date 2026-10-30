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

package com.jetbrains.twig.parser;

import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigElementTypes;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersion;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Parser producing PhpStorm's Twig tree: tags and print blocks are flat token lists with a few composites
 * (references, calls, hashes, arrays), tags with an end tag are wrapped into statements.
 *
 * @author VISTALL
 * @since 2013-11-02
 */
public class TwigParser implements PsiParser {
    private enum Mode {
        /**
         * identifiers are references and calls
         */
        EXPRESSION,
        /**
         * identifiers stay bare, only hashes and arrays are composites
         */
        FLAT,
        /**
         * a bare name, then an expression: block and macro
         */
        NAME_THEN_EXPRESSION
    }

    private static final TokenSet STOP_TOKENS = TokenSet.create(TwigTokenTypes.STATEMENT_BLOCK_END, TwigTokenTypes.PRINT_BLOCK_END,
        TwigTokenTypes.TEXT, TwigTokenTypes.STATEMENT_BLOCK_START, TwigTokenTypes.PRINT_BLOCK_START, TwigTokenTypes.COMMENT_START);
    private static final TokenSet HASH_VALUE_END = TokenSet.create(TwigTokenTypes.COMMA, TwigTokenTypes.RBRACE_CURL);
    private static final TokenSet ARRAY_VALUE_END = TokenSet.create(TwigTokenTypes.COMMA, TwigTokenTypes.RBRACE_SQ);
    private static final TokenSet PARENTHESES_END = TokenSet.create(TwigTokenTypes.RBRACE);

    // words of the tag syntax which are not variables
    private static final Set<String> BARE_WORDS = Set.of("with");
    private static final Set<String> INCLUDE_BARE_WORDS = Set.of("with", "only", "ignore", "missing");

    // end tags of the file, only tags with one of them are looked up for a body
    private final Set<String> myEndTagNames = new HashSet<>();
    private Set<String> myBareWords = BARE_WORDS;

    @Override
    public ASTNode parse(IElementType root, PsiBuilder builder, LanguageVersion languageVersion) {
        collectEndTagNames(builder);

        PsiBuilder.Marker file = builder.mark();
        Deque<String> ends = new ArrayDeque<>();
        while (!builder.eof()) {
            parseItem(builder, ends);
        }
        file.done(root);
        return builder.getTreeBuilt();
    }

    private void collectEndTagNames(PsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();
        while (!builder.eof()) {
            if (builder.getTokenType() == TwigTokenTypes.TAG_NAME) {
                String name = builder.getTokenText();
                if (name != null && name.startsWith("end")) {
                    myEndTagNames.add(name);
                }
            }
            builder.advanceLexer();
        }
        start.rollbackTo();
    }

    private void parseItem(PsiBuilder builder, Deque<String> ends) {
        IElementType type = builder.getTokenType();
        if (type == TwigTokenTypes.STATEMENT_BLOCK_START) {
            parseTagOrStatement(builder, ends);
        }
        else if (type == TwigTokenTypes.PRINT_BLOCK_START) {
            parsePrintBlock(builder);
        }
        else if (type == TwigTokenTypes.COMMENT_START) {
            parseComment(builder);
        }
        else {
            builder.advanceLexer();
        }
    }

    private void parsePrintBlock(PsiBuilder builder) {
        PsiBuilder.Marker block = builder.mark();
        builder.advanceLexer();
        myBareWords = BARE_WORDS;
        parseExpressions(builder, TokenSet.EMPTY, false);
        if (builder.getTokenType() == TwigTokenTypes.PRINT_BLOCK_END) {
            builder.advanceLexer();
        }
        else {
            builder.error("'}}' expected");
        }
        block.done(TwigElementTypes.PRINT_BLOCK);
    }

    private void parseComment(PsiBuilder builder) {
        PsiBuilder.Marker comment = builder.mark();
        builder.advanceLexer();
        if (builder.getTokenType() == TwigTokenTypes.COMMENT_TEXT) {
            builder.advanceLexer();
        }
        if (builder.getTokenType() == TwigTokenTypes.COMMENT_END) {
            builder.advanceLexer();
        }
        comment.done(TwigElementTypes.COMMENT);
    }

    // ------------------------------------------------------------------------------------------------------------------------------------
    // tags

    private void parseTagOrStatement(PsiBuilder builder, Deque<String> ends) {
        String name = getTagName(builder);
        switch (name) {
            case "block" -> {
                if (isShortBlock(builder)) {
                    PsiBuilder.Marker statement = builder.mark();
                    parseTag(builder, TwigElementTypes.BLOCK_TAG, Mode.NAME_THEN_EXPRESSION, BARE_WORDS);
                    statement.done(TwigElementTypes.BLOCK_STATEMENT);
                }
                else {
                    parseStatement(builder, ends, name, TwigElementTypes.BLOCK_TAG, TwigElementTypes.BLOCK_STATEMENT, Mode.NAME_THEN_EXPRESSION);
                }
            }
            case "if" -> parseStatement(builder, ends, name, TwigElementTypes.IF_TAG, TwigElementTypes.IF_STATEMENT, Mode.EXPRESSION);
            case "for" -> parseStatement(builder, ends, name, TwigElementTypes.FOR_TAG, TwigElementTypes.FOR_STATEMENT, Mode.EXPRESSION);
            case "macro" -> parseStatement(builder, ends, name, TwigElementTypes.MACRO_TAG, TwigElementTypes.MACRO_STATEMENT, Mode.NAME_THEN_EXPRESSION);
            case "embed" -> parseStatement(builder, ends, name, TwigElementTypes.EMBED_TAG, TwigElementTypes.EMBED_STATEMENT, Mode.EXPRESSION);
            case "filter" -> parseStatement(builder, ends, name, TwigElementTypes.FILTER_TAG, TwigElementTypes.FILTER_STATEMENT, Mode.FLAT);
            case "set" -> {
                if (isBlockSet(builder)) {
                    parseStatement(builder, ends, name, TwigElementTypes.SET_TAG, TwigElementTypes.SET_STATEMENT, Mode.EXPRESSION);
                }
                else {
                    parseTag(builder, TwigElementTypes.SET_TAG, Mode.EXPRESSION, BARE_WORDS);
                }
            }
            case "include" -> parseTag(builder, TwigElementTypes.INCLUDE_TAG, Mode.EXPRESSION, INCLUDE_BARE_WORDS);
            case "import", "from" -> parseTag(builder, TwigElementTypes.IMPORT_TAG, Mode.EXPRESSION, BARE_WORDS);
            case "extends" -> parseTag(builder, TwigElementTypes.EXTENDS_TAG, Mode.EXPRESSION, BARE_WORDS);
            case "else" -> parseTag(builder, TwigElementTypes.ELSE_TAG, Mode.EXPRESSION, BARE_WORDS);
            case "elseif" -> parseTag(builder, TwigElementTypes.ELSEIF_TAG, Mode.EXPRESSION, BARE_WORDS);
            default -> {
                if (!name.isEmpty() && !name.startsWith("end") && hasEndTag(builder, name, ends)) {
                    parseStatement(builder, ends, name, TwigElementTypes.TAG, TwigElementTypes.TAG_STATEMENT, Mode.FLAT);
                }
                else {
                    parseTag(builder, TwigElementTypes.TAG, Mode.FLAT, BARE_WORDS);
                }
            }
        }
    }

    /**
     * The opening tag, the body up to the end tag, the end tag
     */
    private void parseStatement(PsiBuilder builder, Deque<String> ends, String name, IElementType tagType, IElementType statementType, Mode mode) {
        PsiBuilder.Marker statement = builder.mark();
        parseTag(builder, tagType, mode, statementType == TwigElementTypes.EMBED_STATEMENT ? INCLUDE_BARE_WORDS : BARE_WORDS);

        String endName = "end" + name;
        ends.push(endName);
        while (!builder.eof()) {
            if (builder.getTokenType() == TwigTokenTypes.STATEMENT_BLOCK_START) {
                String tagName = getTagName(builder);
                if (tagName.equals(endName)) {
                    parseTag(builder, TwigElementTypes.TAG, Mode.FLAT, BARE_WORDS);
                    break;
                }
                if ("else".equals(tagName) && ("if".equals(name) || "for".equals(name))) {
                    parseTag(builder, TwigElementTypes.ELSE_TAG, Mode.EXPRESSION, BARE_WORDS);
                    continue;
                }
                if ("elseif".equals(tagName) && "if".equals(name)) {
                    parseTag(builder, TwigElementTypes.ELSEIF_TAG, Mode.EXPRESSION, BARE_WORDS);
                    continue;
                }
                if (ends.contains(tagName)) {
                    // the end of an outer statement: this one is not closed
                    break;
                }
            }
            parseItem(builder, ends);
        }
        ends.pop();
        statement.done(statementType);
    }

    private void parseTag(PsiBuilder builder, IElementType tagType, Mode mode, Set<String> bareWords) {
        PsiBuilder.Marker tag = builder.mark();
        builder.advanceLexer();
        if (builder.getTokenType() == TwigTokenTypes.TAG_NAME) {
            builder.advanceLexer();
        }

        myBareWords = bareWords;
        if (mode == Mode.NAME_THEN_EXPRESSION) {
            if (builder.getTokenType() == TwigTokenTypes.IDENTIFIER) {
                builder.advanceLexer();
            }
            else if (TwigTokenTypes.QUOTES.contains(builder.getTokenType())) {
                parseString(builder);
            }
        }
        parseExpressions(builder, TokenSet.EMPTY, mode == Mode.FLAT);

        if (builder.getTokenType() == TwigTokenTypes.STATEMENT_BLOCK_END) {
            builder.advanceLexer();
        }
        else {
            builder.error("'%}' expected");
        }
        tag.done(tagType);
    }

    /**
     * The TAG_NAME after the current {@code {%}, or an empty string
     */
    private static String getTagName(PsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();
        builder.advanceLexer();
        String name = builder.getTokenType() == TwigTokenTypes.TAG_NAME ? builder.getTokenText() : null;
        start.rollbackTo();
        return name == null ? "" : name;
    }

    /**
     * {@code {% block title 'Title' %}} has no body
     */
    private static boolean isShortBlock(PsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();
        builder.advanceLexer();
        if (builder.getTokenType() == TwigTokenTypes.TAG_NAME) {
            builder.advanceLexer();
        }
        if (builder.getTokenType() == TwigTokenTypes.IDENTIFIER) {
            builder.advanceLexer();
        }
        else if (TwigTokenTypes.QUOTES.contains(builder.getTokenType())) {
            parseString(builder);
        }
        boolean shortBlock = !builder.eof() && builder.getTokenType() != TwigTokenTypes.STATEMENT_BLOCK_END;
        start.rollbackTo();
        return shortBlock;
    }

    /**
     * {@code {% set foo %}...{% endset %}} has no {@code =}
     */
    private static boolean isBlockSet(PsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();
        builder.advanceLexer();
        boolean assignment = false;
        while (!builder.eof() && !STOP_TOKENS.contains(builder.getTokenType())) {
            if (builder.getTokenType() == TwigTokenTypes.EQ) {
                assignment = true;
                break;
            }
            builder.advanceLexer();
        }
        start.rollbackTo();
        return !assignment;
    }

    /**
     * Whether {@code {% endname %}} follows on the same nesting level, before the end of an outer statement
     */
    private boolean hasEndTag(PsiBuilder builder, String name, Deque<String> ends) {
        String endName = "end" + name;
        if (!myEndTagNames.contains(endName)) {
            return false;
        }

        PsiBuilder.Marker start = builder.mark();
        builder.advanceLexer();
        builder.advanceLexer();
        int depth = 0;
        boolean found = false;
        while (!builder.eof()) {
            if (builder.getTokenType() == TwigTokenTypes.TAG_NAME) {
                String tagName = builder.getTokenText();
                if (name.equals(tagName)) {
                    depth++;
                }
                else if (endName.equals(tagName)) {
                    if (depth == 0) {
                        found = true;
                        break;
                    }
                    depth--;
                }
                else if (ends.contains(tagName)) {
                    break;
                }
            }
            builder.advanceLexer();
        }
        start.rollbackTo();
        return found;
    }

    // ------------------------------------------------------------------------------------------------------------------------------------
    // expressions: flat token lists with composites for references, calls, hashes and arrays

    private void parseExpressions(PsiBuilder builder, TokenSet stopAt, boolean flat) {
        while (!builder.eof() && !STOP_TOKENS.contains(builder.getTokenType()) && !stopAt.contains(builder.getTokenType())) {
            parseOperand(builder, flat);
        }
    }

    private void parseOperand(PsiBuilder builder, boolean flat) {
        IElementType type = builder.getTokenType();
        if (type == TwigTokenTypes.IDENTIFIER) {
            if (flat || myBareWords.contains(builder.getTokenText())) {
                builder.advanceLexer();
                return;
            }
            PsiBuilder.Marker reference = builder.mark();
            builder.advanceLexer();
            if (builder.getTokenType() == TwigTokenTypes.LBRACE) {
                parseParentheses(builder, false);
                reference.done(TwigElementTypes.FUNCTION_CALL);
            }
            else {
                reference.done(TwigElementTypes.VARIABLE_REFERENCE);
            }
            parseMemberAccess(builder, reference);
        }
        else if (TwigTokenTypes.QUOTES.contains(type)) {
            parseString(builder);
        }
        else if (type == TwigTokenTypes.LBRACE_CURL) {
            parseHash(builder, flat);
        }
        else if (type == TwigTokenTypes.LBRACE_SQ) {
            parseArray(builder, flat);
        }
        else if (type == TwigTokenTypes.LBRACE) {
            parseParentheses(builder, flat);
        }
        else {
            builder.advanceLexer();
        }
    }

    /**
     * {@code .name}, {@code .name(args)} and {@code [key]} after a reference
     */
    private void parseMemberAccess(PsiBuilder builder, PsiBuilder.Marker qualifier) {
        PsiBuilder.Marker current = qualifier;
        while (true) {
            if (builder.getTokenType() == TwigTokenTypes.DOT && builder.lookAhead(1) == TwigTokenTypes.IDENTIFIER) {
                PsiBuilder.Marker member = current.precede();
                builder.advanceLexer();
                builder.advanceLexer();
                if (builder.getTokenType() == TwigTokenTypes.LBRACE) {
                    parseParentheses(builder, false);
                    member.done(TwigElementTypes.METHOD_CALL);
                }
                else {
                    member.done(TwigElementTypes.FIELD_REFERENCE);
                }
                current = member;
            }
            else if (builder.getTokenType() == TwigTokenTypes.LBRACE_SQ) {
                PsiBuilder.Marker member = current.precede();
                builder.advanceLexer();
                parseExpressions(builder, TokenSet.create(TwigTokenTypes.RBRACE_SQ), false);
                if (builder.getTokenType() == TwigTokenTypes.RBRACE_SQ) {
                    builder.advanceLexer();
                }
                member.done(TwigElementTypes.FIELD_REFERENCE);
                current = member;
            }
            else {
                return;
            }
        }
    }

    /**
     * {@code (args)}: no composite, the arguments are siblings of the parentheses
     */
    private void parseParentheses(PsiBuilder builder, boolean flat) {
        builder.advanceLexer();
        parseExpressions(builder, PARENTHESES_END, flat);
        if (builder.getTokenType() == TwigTokenTypes.RBRACE) {
            builder.advanceLexer();
        }
    }

    /**
     * {@code 'text'}: quote, STRING_TEXT, quote; no composite
     */
    private static void parseString(PsiBuilder builder) {
        IElementType quote = builder.getTokenType();
        builder.advanceLexer();
        if (builder.getTokenType() == TwigTokenTypes.STRING_TEXT) {
            builder.advanceLexer();
        }
        if (builder.getTokenType() == quote) {
            builder.advanceLexer();
        }
    }

    /**
     * {@code {key: value, 'key': value, shorthand}}: keys are bare identifiers
     */
    private void parseHash(PsiBuilder builder, boolean flat) {
        PsiBuilder.Marker hash = builder.mark();
        builder.advanceLexer();
        while (!builder.eof() && !STOP_TOKENS.contains(builder.getTokenType())) {
            IElementType type = builder.getTokenType();
            if (type == TwigTokenTypes.RBRACE_CURL) {
                builder.advanceLexer();
                break;
            }
            if (type == TwigTokenTypes.COMMA) {
                builder.advanceLexer();
                continue;
            }

            // key
            if (type == TwigTokenTypes.IDENTIFIER) {
                builder.advanceLexer();
            }
            else if (type == TwigTokenTypes.COLON) {
                // no key
            }
            else {
                parseOperand(builder, flat);
            }
            // optional key of the types tag: {foo?: 'string'}
            if (builder.getTokenType() == TwigTokenTypes.QUESTION && builder.lookAhead(1) == TwigTokenTypes.COLON) {
                builder.advanceLexer();
            }
            if (builder.getTokenType() == TwigTokenTypes.COLON) {
                builder.advanceLexer();
                parseExpressions(builder, HASH_VALUE_END, flat);
            }
            else if (builder.getTokenType() != TwigTokenTypes.COMMA && builder.getTokenType() != TwigTokenTypes.RBRACE_CURL) {
                // not a hash entry: keep the tokens up to the next entry
                parseExpressions(builder, HASH_VALUE_END, flat);
            }
        }
        hash.done(TwigElementTypes.LITERAL);
    }

    /**
     * {@code [a, 'b']}: each element is an ARRAY_VALUE
     */
    private void parseArray(PsiBuilder builder, boolean flat) {
        PsiBuilder.Marker array = builder.mark();
        builder.advanceLexer();
        while (!builder.eof() && !STOP_TOKENS.contains(builder.getTokenType())) {
            IElementType type = builder.getTokenType();
            if (type == TwigTokenTypes.RBRACE_SQ) {
                builder.advanceLexer();
                break;
            }
            if (type == TwigTokenTypes.COMMA) {
                builder.advanceLexer();
                continue;
            }
            PsiBuilder.Marker value = builder.mark();
            parseExpressions(builder, ARRAY_VALUE_END, flat);
            value.done(TwigElementTypes.ARRAY_VALUE);
        }
        array.done(TwigElementTypes.ARRAY_LITERAL);
    }
}
