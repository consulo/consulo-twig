/*******************************************************************************
 * This file is part of the Twig eclipse plugin.
 *
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 *
 * For the full copyright and license information, please view the _TwigLexerLicense
 * file that was distributed with this source code.
 ******************************************************************************/

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

LABEL=[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*
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
%state ST_TWIG_BLOCK
%state ST_TWIG_COMMENT
%state ST_TWIG_BLOCK_NAME
%state ST_DOUBLE_QUOTES
%state ST_BACKQUOTE


%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> (  ( [^{] | "{" [^?%s{#] )+  ) |" {s" | "{" {
        // raw content
        return TwigTokens.T_INLINE_HTML;
}

<YYINITIAL> {

    {TWIG_VAR_OPEN}  {
        yybegin(ST_TWIG_VAR);
        return TwigTokens.VAR_OPEN;
    }

	{COMMENT_OPEN} {
		yybegin(ST_TWIG_COMMENT);
		return TwigTokens.COMMENT;
	}

    {TWIG_STMT_OPEN}  {
        yybegin(ST_TWIG_BLOCK_NAME);
        return TwigTokens.STMT_OPEN;
    }

    {WHITESPACE}  { return TwigTokens.WHITE_SPACE; }
}

<ST_TWIG_COMMENT> {
	{COMMENT_CLOSE}  {yybegin(YYINITIAL); return TwigTokens.COMMENT; }
    \n               { return TwigTokens.COMMENT; }
    .                { return TwigTokens.COMMENT; }
}

/* after opening block tags, scanning for block name {% NAME */
<ST_TWIG_BLOCK_NAME> {

    {LABEL} {
        yybegin(ST_TWIG_BLOCK);
        return TwigTokens.T_BLOCK_NAME;
    }

    {TWIG_STMT_CLOSE} {
            yybegin(YYINITIAL);
            return TwigTokens.STMT_CLOSE;
     }

    {WHITESPACE}  { return TwigTokens.WHITE_SPACE; }
}

/* inside block tags {% %} */
<ST_TWIG_BLOCK> {

    {TWIG_STMT_CLOSE}  {
        yybegin(YYINITIAL);
        return TwigTokens.STMT_CLOSE;
    }

    {LABEL} {
        return TwigTokens.T_VARIABLE;
    }

    {WHITESPACE}  { return TwigTokens.WHITE_SPACE; }

    "," {return TwigTokens.COMMA;}
}

/* inside VAR tags {{ }} */
<ST_TWIG_VAR> {

    {TWIG_VAR_CLOSE}  {
        yybegin(YYINITIAL);
        return TwigTokens.VAR_CLOSE;
    }

    {LABEL} {
        return TwigTokens.T_VARIABLE;
    }

    {WHITESPACE}  { return TwigTokens.WHITE_SPACE; }

}
<ST_TWIG_BLOCK, ST_TWIG_VAR> {

        "("                                        { return TwigTokens.LPAR; }
        ")"                                        { return TwigTokens.RPAR; }
}


/* double quoted string */
<ST_TWIG_BLOCK, ST_TWIG_VAR>(b?[\"]{DOUBLE_QUOTES_CHARS}*[\"]) {
    return TwigTokens.T_CONSTANT_ENCAPSED_STRING;
}

/* single quoted string */
<ST_TWIG_BLOCK, ST_TWIG_VAR>(b?[']([^'\\]|("\\"{ANY_CHAR}))*[']) {
    return TwigTokens.T_CONSTANT_ENCAPSED_STRING;
}

<ST_TWIG_BLOCK, ST_TWIG_VAR, ST_TWIG_BLOCK_NAME, YYINITIAL> {ANY_CHAR} {
    return TwigTokens.BAD_CHARACTER;
}