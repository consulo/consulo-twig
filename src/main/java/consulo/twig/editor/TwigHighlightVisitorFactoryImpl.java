package consulo.twig.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.rawHighlight.HighlightVisitor;
import consulo.language.editor.rawHighlight.HighlightVisitorFactory;
import consulo.language.psi.PsiFile;
import consulo.twig.psi.TwigFile;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 26/03/2023
 */
@ExtensionImpl
public class TwigHighlightVisitorFactoryImpl implements HighlightVisitorFactory
{
	@Override
	public boolean suitableForFile(@Nonnull PsiFile psiFile)
	{
		return psiFile instanceof TwigFile;
	}

	@Nonnull
	@Override
	public HighlightVisitor createVisitor()
	{
		return new TwigHighlightVisitorImpl();
	}
}
