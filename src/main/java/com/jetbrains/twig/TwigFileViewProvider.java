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

package com.jetbrains.twig;

import com.jetbrains.twig.elements.TwigElementTypes;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.file.FileTypeManager;
import consulo.language.file.LanguageFileType;
import consulo.language.impl.file.MultiplePsiFilesPerDocumentFileViewProvider;
import consulo.language.impl.psi.PsiFileImpl;
import consulo.language.parser.ParserDefinition;
import consulo.language.plain.PlainTextLanguage;
import consulo.language.psi.LanguageSubstitutors;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.template.TemplateDataLanguageMappings;
import consulo.language.template.TemplateLanguage;
import consulo.language.template.TemplateLanguageFileViewProvider;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Twig file with the template data language, HTML by default: {@code foo.html.twig}
 *
 * @author VISTALL
 * @since 2013-11-02
 */
public class TwigFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {
    private final Language myTemplateDataLanguage;

    public TwigFileViewProvider(PsiManager manager, VirtualFile virtualFile, boolean physical) {
        super(manager, virtualFile, physical);
        Language language = getTemplateDataLanguage(virtualFile, manager.getProject());
        myTemplateDataLanguage = language instanceof TemplateLanguage
            ? PlainTextLanguage.INSTANCE
            : LanguageSubstitutors.substituteLanguage(language, virtualFile, manager.getProject());
    }

    public TwigFileViewProvider(PsiManager manager, VirtualFile virtualFile, boolean physical, Language templateDataLanguage) {
        super(manager, virtualFile, physical);
        myTemplateDataLanguage = templateDataLanguage;
    }

    public static Language getTemplateDataLanguage(VirtualFile virtualFile, Project project) {
        Language language = TemplateDataLanguageMappings.getInstance(project).getMapping(virtualFile);
        return language == null ? getTemplateDataLanguageByExtension(virtualFile) : language;
    }

    /**
     * {@code foo.xml.twig} is XML, everything else HTML
     */
    private static Language getTemplateDataLanguageByExtension(VirtualFile virtualFile) {
        String name = virtualFile.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot < 3) {
            return HTMLLanguage.INSTANCE;
        }
        int dataStart = 1 + name.lastIndexOf('.', lastDot - 1);
        if (dataStart < 1) {
            return HTMLLanguage.INSTANCE;
        }
        String dataLanguageFileExtension = name.substring(dataStart, lastDot).toUpperCase();
        FileType fileType = FileTypeManager.getInstance().getStdFileType(dataLanguageFileExtension);
        if (fileType instanceof LanguageFileType languageFileType) {
            return languageFileType.getLanguage();
        }
        return HTMLLanguage.INSTANCE;
    }

    @Override
    public Language getBaseLanguage() {
        return TwigLanguage.INSTANCE;
    }

    @Override
    public Set<Language> getLanguages() {
        return Set.of(TwigLanguage.INSTANCE, getTemplateDataLanguage());
    }

    @Override
    protected @Nullable PsiFile createFile(Language lang) {
        ParserDefinition parserDefinition = ParserDefinition.forLanguage(lang);
        if (parserDefinition == null) {
            return null;
        }
        if (lang == getBaseLanguage()) {
            return parserDefinition.createFile(this);
        }
        if (lang == getTemplateDataLanguage()) {
            PsiFileImpl file = (PsiFileImpl) parserDefinition.createFile(this);
            file.setContentElementType(TwigElementTypes.TEMPLATE_DATA);
            return file;
        }
        return null;
    }

    @Override
    protected TwigFileViewProvider cloneInner(VirtualFile copy) {
        return new TwigFileViewProvider(getManager(), copy, false, myTemplateDataLanguage);
    }

    @Override
    public Language getTemplateDataLanguage() {
        return myTemplateDataLanguage;
    }
}
