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

package consulo.twig.highlight;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.twig.TwigFileViewProvider;
import consulo.twig.psi.TwigTokens;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigEditorHighlighter extends LayeredLexerEditorHighlighter
{

	public TwigEditorHighlighter(@Nullable final Project project, @Nullable final VirtualFile virtualFile, @Nonnull final EditorColorsScheme colors)
	{
		super(new TwigSyntaxHighlighter(), colors);
		final SyntaxHighlighter highlighter = getTemplateDataLanguageHighlighter(project, virtualFile);
		registerLayer(TwigTokens.T_INLINE_HTML, new LayerDescriptor(new SyntaxHighlighter()
		{
			@Nonnull
			public Lexer getHighlightingLexer()
			{
				return highlighter.getHighlightingLexer();
			}

			@Nonnull
			public TextAttributesKey[] getTokenHighlights(final IElementType tokenType)
			{
				return highlighter.getTokenHighlights(tokenType);
			}
		}, ""));
	}

	@Nonnull
	private static SyntaxHighlighter getTemplateDataLanguageHighlighter(final Project project, final VirtualFile virtualFile)
	{
		FileType type = project == null || virtualFile == null ? null : TwigFileViewProvider.getTemplateDataLanguage(virtualFile, project).getAssociatedFileType();
		FileType fileType = type == null ? PlainTextFileType.INSTANCE : type;
		SyntaxHighlighter highlighter = TwigSyntaxHighlighterFactory.getSyntaxHighlighter(fileType, project, virtualFile);
		assert highlighter != null;
		return highlighter;
	}

}