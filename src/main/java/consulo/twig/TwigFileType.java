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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.twig.highlight.TwigEditorHighlighter;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigFileType extends LanguageFileType
{
	public static final TwigFileType INSTANCE = new TwigFileType();

	public TwigFileType()
	{
		super(TwigLanguage.INSTANCE);

		FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, new EditorHighlighterProvider()
		{
			@Override
			public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nonnull FileType fileType, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme colors)
			{
				return new TwigEditorHighlighter(project, virtualFile, colors);
			}
		});

	}

	@Nonnull
	@Override
	public String getId()
	{
		return "TWIG";
	}

	@Nonnull
	@Override
	public String getDescription()
	{
		return TwigBundle.message("twig.file.type.description");
	}

	@Nonnull
	@Override
	public String getDefaultExtension()
	{
		return "twig";
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return AllIcons.FileTypes.Custom;
	}
}
