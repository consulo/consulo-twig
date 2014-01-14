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

import gnu.trove.THashSet;

import java.util.Arrays;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.twig.psi.TwigTemplateTokens;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.LanguageSubstitutors;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.templateLanguages.TemplateLanguage;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider
{
	private final Language myTemplateDataLanguage;

	public TwigFileViewProvider(final PsiManager manager, final VirtualFile virtualFile, final boolean physical)
	{
		super(manager, virtualFile, physical);
		final Language language = getTemplateDataLanguage(virtualFile, manager.getProject());
		myTemplateDataLanguage = language instanceof TemplateLanguage ? PlainTextLanguage.INSTANCE : LanguageSubstitutors.INSTANCE.substituteLanguage(language, virtualFile, manager.getProject());
	}

	public TwigFileViewProvider(final PsiManager manager, final VirtualFile virtualFile, final boolean physical, final Language templateDataLanguage)
	{
		super(manager, virtualFile, physical);
		myTemplateDataLanguage = templateDataLanguage;
	}

	@NotNull
	public static Language getTemplateDataLanguage(@NotNull VirtualFile virtualFile, @NotNull Project project)
	{
		final Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);
		return language == null ? getTemplateDataLanguageByExtention(virtualFile) : language;
	}

	@NotNull
	private static Language getTemplateDataLanguageByExtention(VirtualFile virtualFile)
	{
		String name = virtualFile.getName();
		int index2 = name.lastIndexOf('.');
		if(index2 < 3)
		{
			return HTMLLanguage.INSTANCE;
		}
		int index1 = 1 + name.lastIndexOf('.', index2 - 1);
		if(index1 < 1)
		{
			return HTMLLanguage.INSTANCE;
		}
		String dataLanguageFileExtension = name.substring(index1, index2).toUpperCase();
		FileType fileType = FileTypeManager.getInstance().getStdFileType(dataLanguageFileExtension);
		if(fileType instanceof LanguageFileType)
		{
			return ((LanguageFileType) fileType).getLanguage();
		}
		return HTMLLanguage.INSTANCE;
	}

	@NotNull
	@Override
	public Language getBaseLanguage()
	{
		return TwigLanguage.INSTANCE;
	}

	@NotNull
	@Override
	public Set<Language> getLanguages()
	{
		return new THashSet<Language>(Arrays.asList(TwigLanguage.INSTANCE, getTemplateDataLanguage()));
	}

	@Nullable
	@Override
	protected PsiFile createFile(final Language lang)
	{
		if(lang == getBaseLanguage())
		{
			return createFileInner(lang);
		}
		if(lang == getTemplateDataLanguage())
		{
			final PsiFileImpl file = (PsiFileImpl) createFileInner(lang);
			file.setContentElementType(TwigTemplateTokens.TEMPLATE_DATA);
			return file;
		}
		return null;
	}

	private PsiFile createFileInner(Language lang)
	{
		return LanguageParserDefinitions.INSTANCE.forLanguage(lang).createFile(this);
	}

	@Override
	protected TwigFileViewProvider cloneInner(final VirtualFile copy)
	{
		return new TwigFileViewProvider(getManager(), copy, false, myTemplateDataLanguage);
	}

	@NotNull
	public Language getTemplateDataLanguage()
	{
		return myTemplateDataLanguage;
	}
}
