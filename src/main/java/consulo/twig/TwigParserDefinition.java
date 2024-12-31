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

package consulo.twig;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;
import consulo.twig.lexer.TwigLexer;
import consulo.twig.parser.TwigParser;
import consulo.twig.psi.TwigFile;
import consulo.twig.psi.TwigTokens;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
@ExtensionImpl
public class TwigParserDefinition implements ParserDefinition
{
	private static final IFileElementType FILE_ELEMENT_TYPE = new IFileElementType("TWIG_FILE_ELEMENT_TYPE", TwigLanguage.INSTANCE);

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return TwigLanguage.INSTANCE;
	}

	@Nonnull
	@Override
	public Lexer createLexer(@Nonnull LanguageVersion languageVersion)
	{
		return new TwigLexer();
	}

	@Nonnull
	@Override
	public PsiParser createParser(@Nonnull LanguageVersion languageVersion)
	{
		return new TwigParser();
	}

	@Nonnull
	@Override
	public IFileElementType getFileNodeType()
	{
		return FILE_ELEMENT_TYPE;
	}

	@Nonnull
	@Override
	public TokenSet getWhitespaceTokens(@Nonnull LanguageVersion languageVersion)
	{
		return TokenSet.create(TwigTokens.WHITE_SPACE);
	}

	@Nonnull
	@Override
	public TokenSet getCommentTokens(@Nonnull LanguageVersion languageVersion)
	{
		return TokenSet.create(TwigTokens.COMMA);
	}

	@Nonnull
	@Override
	public TokenSet getStringLiteralElements(@Nonnull LanguageVersion languageVersion)
	{
		return TokenSet.create(TwigTokens.STRING, TwigTokens.DSTRING);
	}

	@Nonnull
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
