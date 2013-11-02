/**
 * Main code by Robert Gruendler <r.gruendler@gmail.com> but was fully rewrited
 */
package org.mustbe.consulo.twig.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mustbe.consulo.twig.psi.TwigTokens;
%%

/* -----------------Options and Declarations Section----------------- */

%class _TwigLexer
%implements FlexLexer
%public
%unicode
%public

%function advance
%type IElementType

%eof{ return;
%eof}

/* Macro Declarations  */

TWIG_VAR_OPEN = "{{"
TWIG_VAR_CLOSE = "}}"

COMMENT_OPEN = "{#"
COMMENT_CLOSE = "#}"

TWIG_STMT_OPEN = "{%"
TWIG_STMT_CLOSE = "%}"

IDENTIFIER=[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*
WHITESPACE=[ \n\r\t]+

ANY_CHAR=[.]

DOUBLE_QUOTES_CHARS=(([^\"\\]|("\\"{ANY_CHAR})))

/*

LNUM=[0-9]+
DNUM=([0-9]*"."[0-9]+)|([0-9]+"."[0-9]*)
EXPONENT_DNUM=(({LNUM}|{DNUM})[eE][+-]?{LNUM})
HNUM="0x"[0-9a-fA-F]+
TABS_AND_SPACES=[ \t]*
BACKQUOTE_CHARS=(([^`\\]|("\\"{ANY_CHAR})))
NEWLINE=("\r"|"\n"|"\r\n")

*/


/* lexical states */

%state ST_TWIG_VAR
%state ST_TWIG_COMMENT
%state ST_TWIG_BLOCK_NAME

%state ST_BLOCK_EXPRESSION
%state ST_EXPRESSION_EXPRESSION

%state ST_DOUBLE_QUOTES
%state ST_BACKQUOTE


%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> (  ( [^{] | "{" [^?%s{#] )+  ) |" {s" | "{"
{
        // raw content
        return TwigTokens.T_INLINE_HTML;
}

<YYINITIAL>
{
    {TWIG_VAR_OPEN}
    {
        yybegin(ST_EXPRESSION_EXPRESSION);
        return TwigTokens.VAR_OPEN;
    }

	{COMMENT_OPEN}
	{
		yybegin(ST_TWIG_COMMENT);
		return TwigTokens.COMMENT;
	}

    {TWIG_STMT_OPEN}
    {
        yybegin(ST_TWIG_BLOCK_NAME);
        return TwigTokens.STMT_OPEN;
    }

    {WHITESPACE} { return TwigTokens.WHITE_SPACE; }

    {ANY_CHAR}   { return TwigTokens.BAD_CHARACTER; }
}

<ST_TWIG_COMMENT>
{
	{COMMENT_CLOSE}     {yybegin(YYINITIAL); return TwigTokens.COMMENT; }

    {WHITESPACE}        { return TwigTokens.COMMENT; }

    .                   { return TwigTokens.COMMENT; }
}

<ST_TWIG_BLOCK_NAME>
{
    {IDENTIFIER}
    {
        yybegin(ST_BLOCK_EXPRESSION);
        return TwigTokens.BLOCK_NAME;
    }

    {TWIG_STMT_CLOSE}
    {
        yybegin(YYINITIAL);
        return TwigTokens.STMT_CLOSE;
    }

    {WHITESPACE}  { return TwigTokens.WHITE_SPACE; }
}

<ST_EXPRESSION_EXPRESSION, ST_BLOCK_EXPRESSION>
{
    (b?[\"]{DOUBLE_QUOTES_CHARS}*[\"])  {return TwigTokens.DSTRING;}

    (b?[']([^'\\]|("\\"{ANY_CHAR}))*['])  {return TwigTokens.STRING;}

    "in"         { return TwigTokens.IN_KEYWORD; }

    "is"         { return TwigTokens.IS_KEYWORD; }

    {IDENTIFIER} {return TwigTokens.IDENTIFIER; }

    "("          { return TwigTokens.LPAR; }

    ")"          { return TwigTokens.RPAR; }

    "|"          { return TwigTokens.OR; }

    {WHITESPACE} { return TwigTokens.WHITE_SPACE; }

    {ANY_CHAR}   { return TwigTokens.BAD_CHARACTER; }
}

<ST_BLOCK_EXPRESSION>
{
    {TWIG_STMT_CLOSE}
    {
        yybegin(YYINITIAL);
        return TwigTokens.STMT_CLOSE;
    }
}

<ST_EXPRESSION_EXPRESSION>
{
    {TWIG_VAR_CLOSE}
    {
        yybegin(YYINITIAL);
        return TwigTokens.VAR_CLOSE;
    }
}
