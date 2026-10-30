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

import com.jetbrains.twig.TwigFile;
import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigCompositeElement;
import com.jetbrains.twig.elements.TwigElementTypes;
import com.jetbrains.twig.lexer.TwigLexer;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementTypeAsPsiFactory;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;

/**
 * Open for languages built on Twig, like Twig in Markdown code fences: they change the file node type and the file
 *
 * @author VISTALL
 * @since 2013-11-02
 */
@ExtensionImpl
public class TwigParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TwigTokenTypes.WHITE_SPACE);

    @Override
    public Language getLanguage() {
        return TwigLanguage.INSTANCE;
    }

    @Override
    public Lexer createLexer(LanguageVersion languageVersion) {
        return new TwigLexer();
    }

    @Override
    public PsiParser createParser(LanguageVersion languageVersion) {
        return new TwigParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return TwigElementTypes.FILE;
    }

    @Override
    public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
        return WHITE_SPACES;
    }

    /**
     * Comments are COMMENT composites built by the parser
     */
    @Override
    public TokenSet getCommentTokens(LanguageVersion languageVersion) {
        return TokenSet.EMPTY;
    }

    @Override
    public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
        return TwigTokenTypes.STRING_LITERALS;
    }

    @Override
    public PsiElement createElement(ASTNode node) {
        if (node.getElementType() instanceof IElementTypeAsPsiFactory factory) {
            return factory.createElement(node);
        }
        return new TwigCompositeElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new TwigFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
