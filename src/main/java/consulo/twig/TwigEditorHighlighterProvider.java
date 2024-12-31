package consulo.twig;

import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.EditorHighlighter;
import consulo.colorScheme.EditorColorsScheme;
import consulo.language.editor.highlight.EditorHighlighterProvider;
import consulo.project.Project;
import consulo.twig.highlight.TwigEditorHighlighter;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2019-02-26
 */
@ExtensionImpl
public class TwigEditorHighlighterProvider implements EditorHighlighterProvider
{
	@Override
	public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nonnull FileType fileType, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme colors)
	{
		return new TwigEditorHighlighter(project, virtualFile, colors);
	}

	@Nonnull
	@Override
	public FileType getFileType()
	{
		return TwigFileType.INSTANCE;
	}
}
