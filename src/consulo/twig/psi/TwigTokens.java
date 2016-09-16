/*
 * Copyright 2013-2014 must-be.org
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

package consulo.twig.psi;

import consulo.twig.TwigLanguage;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public interface TwigTokens extends TokenType
{
	IElementType STMT_OPEN = new IElementType("STMT_OPEN", TwigLanguage.INSTANCE);

	IElementType STMT_CLOSE = new IElementType("STMT_CLOSE", TwigLanguage.INSTANCE);

	IElementType VAR_OPEN = new IElementType("VAR_OPEN", TwigLanguage.INSTANCE);

	IElementType VAR_CLOSE = new IElementType("VAR_CLOSE", TwigLanguage.INSTANCE);

	IElementType COMMA = new IElementType("COMMA", TwigLanguage.INSTANCE);

	IElementType COMMENT = new IElementType("COMMENT", TwigLanguage.INSTANCE);

	IElementType BLOCK_NAME = new IElementType("BLOCK_NAME", TwigLanguage.INSTANCE);

	IElementType IDENTIFIER = new IElementType("IDENTIFIER", TwigLanguage.INSTANCE);

	IElementType T_INLINE_HTML = new IElementType("T_INLINE_HTML", TwigLanguage.INSTANCE);

	IElementType STRING = new IElementType("STRING", TwigLanguage.INSTANCE);

	IElementType DSTRING = new IElementType("DSTRING", TwigLanguage.INSTANCE);

	IElementType LPAR = new IElementType("LPAR", TwigLanguage.INSTANCE);

	IElementType RPAR = new IElementType("RPAR", TwigLanguage.INSTANCE);

	IElementType OR = new IElementType("OR", TwigLanguage.INSTANCE);

	IElementType PERC = new IElementType("PERC", TwigLanguage.INSTANCE);

	IElementType PLUS = new IElementType("PLUS", TwigLanguage.INSTANCE);

	IElementType MINUS = new IElementType("MINUS", TwigLanguage.INSTANCE);

	IElementType EQ = new IElementType("EQ", TwigLanguage.INSTANCE);

	IElementType IN_KEYWORD = new IElementType("IN_KEYWORD", TwigLanguage.INSTANCE);

	IElementType IS_KEYWORD = new IElementType("IS_KEYWORD", TwigLanguage.INSTANCE);

	TokenSet BINARY_TOKENS = TokenSet.create(IN_KEYWORD, IS_KEYWORD, OR, EQ, PERC, PLUS, MINUS);
}
