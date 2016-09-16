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

package org.mustbe.consulo.twig;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.twig.lexer.TwigLexer;
import org.mustbe.consulo.twig.parser.TwigParser;
import org.mustbe.consulo.twig.psi.TwigFile;
import org.mustbe.consulo.twig.psi.TwigTokens;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import consulo.lang.LanguageVersion;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigParserDefinition implements ParserDefinition
{
	private static final IFileElementType FILE_ELEMENT_TYPE = new IFileElementType("TWIG_FILE_ELEMENT_TYPE", TwigLanguage.INSTANCE);

	@NotNull
	@Override
	public Lexer createLexer(@NotNull LanguageVersion languageVersion)
	{
		return new TwigLexer();
	}

	@NotNull
	@Override
	public PsiParser createParser(@NotNull LanguageVersion languageVersion)
	{
		return new TwigParser();
	}

	@NotNull
	@Override
	public IFileElementType getFileNodeType()
	{
		return FILE_ELEMENT_TYPE;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens(@NotNull LanguageVersion languageVersion)
	{
		return TokenSet.create(TwigTokens.WHITE_SPACE);
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens(@NotNull LanguageVersion languageVersion)
	{
		return TokenSet.create(TwigTokens.COMMA);
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements(@NotNull LanguageVersion languageVersion)
	{
		return TokenSet.create(TwigTokens.STRING, TwigTokens.DSTRING);
	}

	@NotNull
	@Override
	public PsiElement createElement(ASTNode astNode)
	{
		return new ASTWrapperPsiElement(astNode);
	}

	@Override
	public PsiFile createFile(FileViewProvider fileViewProvider)
	{
		return new TwigFile(fileViewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2)
	{
		return SpaceRequirements.MAY;
	}
}
