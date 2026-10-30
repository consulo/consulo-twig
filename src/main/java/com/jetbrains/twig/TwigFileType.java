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

import com.jetbrains.twig.icons.TwigIcons;
import consulo.language.file.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.twig.localize.TwigLocalize;
import consulo.ui.image.Image;

/**
 * @author VISTALL
 * @since 2013-11-02
 */
public class TwigFileType extends LanguageFileType {
    public static final TwigFileType INSTANCE = new TwigFileType();

    private TwigFileType() {
        super(TwigLanguage.INSTANCE);
    }

    @Override
    public String getId() {
        return "TWIG";
    }

    @Override
    public LocalizeValue getDescription() {
        return TwigLocalize.twigFileTypeDescription();
    }

    @Override
    public String getDefaultExtension() {
        return "twig";
    }

    @Override
    public Image getIcon() {
        return TwigIcons.TwigFileIcon;
    }
}
