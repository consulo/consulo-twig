package com.jetbrains.twig.lexer;

import com.jetbrains.twig.TwigTokenTypes;
import consulo.language.ast.IElementType;
import consulo.language.lexer.LexerBase;

/**
 * Twig lexer in the shape of PhpStorm's Twig lexer: template text is TEXT, strings are quote, STRING_TEXT and quote,
 * the word after {% is TAG_NAME
 */
%%

%class _TwigLexer
%extends LexerBase
%public
%unicode

%function advanceImpl
%type IElementType

%eof{ return;
%eof}

%{
    // the tag of the current {% %} block, keywords like 'import' and 'as' depend on it
    private String myTagName = "";
    // open { in a print block, '}}' closes the block only outside of hashes
    private int myCurlyDepth;
    // the state of the expression a string is in
    private int myExpressionState = ST_STATEMENT;

    private IElementType identifier(CharSequence text) {
        String word = text.toString();
        switch (word) {
            case "and": return TwigTokenTypes.AND;
            case "or": return TwigTokenTypes.OR;
            case "not": return TwigTokenTypes.NOT;
            case "in": return TwigTokenTypes.IN;
            case "is": return TwigTokenTypes.IS;
            case "_self":
            case "_context":
            case "_charset":
                return TwigTokenTypes.RESERVED_ID;
            case "as":
                return "import".equals(myTagName) || "from".equals(myTagName) ? TwigTokenTypes.AS_KEYWORD : TwigTokenTypes.IDENTIFIER;
            case "import":
                return "from".equals(myTagName) ? TwigTokenTypes.IMPORT_KEYWORD : TwigTokenTypes.IDENTIFIER;
            default:
                return TwigTokenTypes.IDENTIFIER;
        }
    }

    private IElementType statementEnd() {
        boolean raw = "verbatim".equals(myTagName) || "raw".equals(myTagName);
        myTagName = "";
        yybegin(raw ? ST_VERBATIM : YYINITIAL);
        return TwigTokenTypes.STATEMENT_BLOCK_END;
    }

    // {% endverbatim %} after the current {%
    private boolean isVerbatimEnd() {
        int offset = zzMarkedPos;
        while (offset < zzEndRead && Character.isWhitespace(zzBuffer.charAt(offset))) {
            offset++;
        }
        CharSequence rest = zzBuffer.subSequence(offset, Math.min(zzEndRead, offset + 11));
        String text = rest.toString();
        return text.startsWith("endverbatim") || text.startsWith("endraw");
    }

    private IElementType singleChar(char c) {
        switch (c) {
            case '-': return TwigTokenTypes.MINUS;
            case '~': return TwigTokenTypes.CONCAT;
            default: return TwigTokenTypes.RBRACE_CURL;
        }
    }
%}

WHITE_SPACE=[ \n\r\t\f]+
IDENTIFIER=[a-zA-Z_$\u0080-￿][a-zA-Z0-9_$\u0080-￿]*
NUMBER=[0-9]+("."[0-9]+)?([eE][+-]?[0-9]+)?
TEXT=([^{]|"{"[^{%#])+

%state ST_TAG_NAME
%state ST_STATEMENT
%state ST_PRINT
%state ST_SINGLE_QUOTE
%state ST_DOUBLE_QUOTE
%state ST_COMMENT
%state ST_COMMENT_BODY
%state ST_VERBATIM

%%

<YYINITIAL> {
    "{{"[-~]? { myCurlyDepth = 0; yybegin(ST_PRINT); return TwigTokenTypes.PRINT_BLOCK_START; }
    "{%"[-~]? { myCurlyDepth = 0; myTagName = ""; yybegin(ST_TAG_NAME); return TwigTokenTypes.STATEMENT_BLOCK_START; }
    "{#"[-~]? { yybegin(ST_COMMENT); return TwigTokenTypes.COMMENT_START; }
    {TEXT} { return TwigTokenTypes.TEXT; }
    "{" { return TwigTokenTypes.TEXT; }
}

<ST_VERBATIM> {
    "{%"[-~]? {
        if (isVerbatimEnd()) {
            myCurlyDepth = 0;
            myTagName = "";
            yybegin(ST_TAG_NAME);
            return TwigTokenTypes.STATEMENT_BLOCK_START;
        }
        return TwigTokenTypes.TEXT;
    }
    ([^{]|"{"[^%])+ { return TwigTokenTypes.TEXT; }
    "{" { return TwigTokenTypes.TEXT; }
}

<ST_COMMENT> {
    {WHITE_SPACE} { return TwigTokenTypes.WHITE_SPACE; }
    [-~]?"#}" { yybegin(YYINITIAL); return TwigTokenTypes.COMMENT_END; }
    [^] { yypushback(1); yybegin(ST_COMMENT_BODY); }
}

<ST_COMMENT_BODY> {
    [-~]?"#}" { yybegin(YYINITIAL); return TwigTokenTypes.COMMENT_END; }
    [^] { return TwigTokenTypes.COMMENT_TEXT; }
}

<ST_TAG_NAME> {
    {WHITE_SPACE} { return TwigTokenTypes.WHITE_SPACE; }
    {IDENTIFIER} { myTagName = yytext().toString(); yybegin(ST_STATEMENT); return TwigTokenTypes.TAG_NAME; }
    [^] { yypushback(1); yybegin(ST_STATEMENT); }
}

<ST_STATEMENT> {
    [-~]?"%}" { return statementEnd(); }
}

<ST_PRINT> {
    [-~]?"}}" {
        if (myCurlyDepth > 0) {
            // the end of a hash: one char only
            yypushback(yylength() - 1);
            if (yycharat(0) == '}') {
                myCurlyDepth--;
            }
            return singleChar(yycharat(0));
        }
        yybegin(YYINITIAL);
        return TwigTokenTypes.PRINT_BLOCK_END;
    }
}

<ST_STATEMENT, ST_PRINT> {
    {WHITE_SPACE} { return TwigTokenTypes.WHITE_SPACE; }

    "'" { myExpressionState = yystate(); yybegin(ST_SINGLE_QUOTE); return TwigTokenTypes.SINGLE_QUOTE; }
    "\"" { myExpressionState = yystate(); yybegin(ST_DOUBLE_QUOTE); return TwigTokenTypes.DOUBLE_QUOTE; }

    {NUMBER} { return TwigTokenTypes.NUMBER; }
    {IDENTIFIER} { return identifier(yytext()); }

    "==" { return TwigTokenTypes.EQ_EQ; }
    "!=" { return TwigTokenTypes.NOT_EQ; }
    "<=" { return TwigTokenTypes.LE; }
    ">=" { return TwigTokenTypes.GE; }
    "<" { return TwigTokenTypes.LT; }
    ">" { return TwigTokenTypes.GT; }
    "=>" { return TwigTokenTypes.ARROW; }
    "=" { return TwigTokenTypes.EQ; }
    "+" { return TwigTokenTypes.PLUS; }
    "-" { return TwigTokenTypes.MINUS; }
    "**" { return TwigTokenTypes.POW; }
    "*" { return TwigTokenTypes.MUL; }
    "//" { return TwigTokenTypes.FLOOR_DIV; }
    "/" { return TwigTokenTypes.DIV; }
    "%" { return TwigTokenTypes.MOD; }
    "~" { return TwigTokenTypes.CONCAT; }
    "|" { return TwigTokenTypes.FILTER; }
    "..." { return TwigTokenTypes.SPREAD; }
    ".." { return TwigTokenTypes.RANGE; }
    "." { return TwigTokenTypes.DOT; }
    "," { return TwigTokenTypes.COMMA; }
    ":" { return TwigTokenTypes.COLON; }
    "??" { return TwigTokenTypes.NULL_COALESCE; }
    "?" { return TwigTokenTypes.QUESTION; }
    "(" { return TwigTokenTypes.LBRACE; }
    ")" { return TwigTokenTypes.RBRACE; }
    "[" { return TwigTokenTypes.LBRACE_SQ; }
    "]" { return TwigTokenTypes.RBRACE_SQ; }
    "{" { myCurlyDepth++; return TwigTokenTypes.LBRACE_CURL; }
    "}" { if (myCurlyDepth > 0) { myCurlyDepth--; } return TwigTokenTypes.RBRACE_CURL; }

    [^] { return TwigTokenTypes.BAD_CHARACTER; }
}

<ST_SINGLE_QUOTE> {
    "'" { yybegin(myExpressionState); return TwigTokenTypes.SINGLE_QUOTE; }
    ([^'\\\n]|\\[^\n])+ { return TwigTokenTypes.STRING_TEXT; }
    // an unterminated string ends with the line
    [^] { yypushback(1); yybegin(myExpressionState); }
}

<ST_DOUBLE_QUOTE> {
    "\"" { yybegin(myExpressionState); return TwigTokenTypes.DOUBLE_QUOTE; }
    ([^\"\\\n]|\\[^\n])+ { return TwigTokenTypes.STRING_TEXT; }
    [^] { yypushback(1); yybegin(myExpressionState); }
}
