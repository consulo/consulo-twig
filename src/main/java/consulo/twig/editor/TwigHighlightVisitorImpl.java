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

package consulo.twig.editor;

import org.jetbrains.annotations.NotNull;
import consulo.twig.highlight.TwigSyntaxHighlighterKeys;
import consulo.twig.psi.TwigBlock;
import consulo.twig.psi.TwigConstantExpression;
import consulo.twig.psi.TwigExpressionBody;
import consulo.twig.psi.TwigFile;
import consulo.twig.psi.TwigReferenceExpression;
import consulo.twig.psi.TwigTag;
import consulo.twig.psi.TwigVisitor;
import consulo.twig.psi.references.TwigReferenceContributor;
import consulo.twig.table.TwigTable;
import consulo.twig.table.TwigTableBlock;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigHighlightVisitorImpl extends TwigVisitor implements HighlightVisitor
{
	private HighlightInfoHolder myHighlightInfoHolder;

	@Override
	public boolean suitableForFile(@NotNull PsiFile psiFile)
	{
		return psiFile instanceof TwigFile;
	}

	@Override
	public void visit(@NotNull PsiElement element)
	{
		element.accept(this);
	}

	@Override
	public boolean analyze(@NotNull PsiFile psiFile, boolean b, @NotNull HighlightInfoHolder highlightInfoHolder, @NotNull Runnable runnable)
	{
		myHighlightInfoHolder = highlightInfoHolder;
		runnable.run();
		return true;
	}

	@Override
	public void visitElement(PsiElement element)
	{
		element.acceptChildren(this);
	}

	@Override
	public void visitTag(TwigTag tag)
	{
		super.visitTag(tag);

		myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(tag).textAttributes(TwigSyntaxHighlighterKeys.TAG).create());
	}

	@Override
	public void visitExpressionBody(TwigExpressionBody twigExpression)
	{
		super.visitExpressionBody(twigExpression);

		myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(twigExpression).textAttributes(TwigSyntaxHighlighterKeys.TAG).create());
	}

	@Override
	public void visitConstantExpression(TwigConstantExpression twigConstantExpression)
	{
		super.visitConstantExpression(twigConstantExpression);

		boolean foundFileRef = false;
		PsiElement ref = null;
		PsiReference[] references = twigConstantExpression.getReferences();
		for(PsiReference reference : references)
		{
			if(reference instanceof FileReference)
			{
				foundFileRef = true;

				FileReference lastReference = ((FileReference) reference).getFileReferenceSet().getLastReference();
				PsiElement temp = lastReference == null ? null : lastReference.resolve();

				if(temp instanceof PsiFile)
				{
					ref = temp;
					break;
				}
			}
		}

		if(foundFileRef && ref == null)
		{
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("File is not resolved").range(twigConstantExpression).create());
		}
	}

	@Override
	public void visitReferenceExpression(TwigReferenceExpression twigReferenceExpression)
	{
		super.visitReferenceExpression(twigReferenceExpression);

		if(TwigReferenceContributor.isNotReference(twigReferenceExpression))
		{
			return;
		}

		PsiElement ref = null;
		PsiReference[] references = twigReferenceExpression.getReferences();
		for(PsiReference reference : references)
		{
			ref = reference.resolve();
			if(ref != null)
			{
				break;
			}
		}

		if(ref == null)
		{
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("Reference is not resolved").range(twigReferenceExpression).create());
		}
	}

	@Override
	public void visitBlock(TwigBlock block)
	{
		super.visitBlock(block);

		TwigTag openTag = block.getOpenTag();
		TwigTag closeTag = block.getCloseTag();

		TwigTableBlock tableBlock = TwigTable.INSTANCE.getBlock(openTag.getName());
		if(tableBlock == null)
		{
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("Unknown tag").range(openTag.getNameIdentifier()).create());

			if(closeTag != null && Comparing.equal(openTag.getOpenedTagName(), closeTag.getOpenedTagName()))
			{
				myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("Unknown tag").range(closeTag.getNameIdentifier()).create());
			}
		}
		else if(tableBlock.getType() == TwigTableBlock.TwigBlockType.BLOCK)
		{
			if(closeTag == null)
			{
				myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR).descriptionAndTooltip("Tag is not closed").range(openTag).create());
				return;
			}

			if(!Comparing.equal(openTag.getOpenedTagName(), closeTag.getOpenedTagName()))
			{
				myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR).descriptionAndTooltip("Wrong close tag name").range(closeTag.getNameIdentifier()).create());
			}
		}
	}

	@NotNull
	@Override
	public HighlightVisitor clone()
	{
		return new TwigHighlightVisitorImpl();
	}

	@Override
	public int order()
	{
		return 0;
	}
}
