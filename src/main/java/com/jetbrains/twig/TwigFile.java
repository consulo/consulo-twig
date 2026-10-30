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

import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.PsiFileBase;
import consulo.virtualFileSystem.fileType.FileType;

/**
 * Root of the Twig tree: template text, print blocks, tags, statements and comments are its children
 *
 * @author VISTALL
 * @since 2013-11-02
 */
public class TwigFile extends PsiFileBase {
    public TwigFile(FileViewProvider viewProvider) {
        super(viewProvider, TwigLanguage.INSTANCE);
    }

    @Override
    public FileType getFileType() {
        return TwigFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "TwigFile:" + getName();
    }
}
