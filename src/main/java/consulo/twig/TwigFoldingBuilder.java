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
import consulo.document.Document;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.editor.folding.FoldingBuilder;
import consulo.language.editor.folding.FoldingDescriptor;
import consulo.language.psi.PsiElement;
import consulo.twig.psi.TwigBlock;
import consulo.twig.psi.TwigElements;
import consulo.twig.psi.TwigTag;
import consulo.twig.psi.TwigVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
@ExtensionImpl
public class TwigFoldingBuilder implements FoldingBuilder
{
	@Nonnull
	@Override
	public FoldingDescriptor[] buildFoldRegions(@Nonnull ASTNode astNode, @Nonnull Document document)
	{
		final List<FoldingDescriptor> list = new ArrayList<FoldingDescriptor>();
		PsiElement psi = astNode.getPsi();
		psi.accept(new TwigVisitor()
		{
			@Override
			public void visitElement(PsiElement element)
			{
				element.acceptChildren(this);
			}

			@Override
			public void visitBlock(TwigBlock block)
			{
				super.visitBlock(block);

				TwigTag openTag = block.getOpenTag();
				String name = openTag.getName();
				if(name != null && block.getCloseTag() != null)
				{
					list.add(new FoldingDescriptor(block, block.getTextRange()));
				}
			}
		});
		return list.toArray(new FoldingDescriptor[list.size()]);
	}

	@Nullable
	@Override
	public String getPlaceholderText(@Nonnull ASTNode astNode)
	{
		PsiElement psi = astNode.getPsi();
		IElementType elementType = astNode.getElementType();
		if(elementType == TwigElements.BLOCK)
		{
			TwigBlock block = (TwigBlock) psi;
			return "{% " + block.getOpenTag().getName() + " %}";
		}
		return null;
	}

	@Override
	public boolean isCollapsedByDefault(@Nonnull ASTNode astNode)
	{
		return false;
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return TwigLanguage.INSTANCE;
	}
}
