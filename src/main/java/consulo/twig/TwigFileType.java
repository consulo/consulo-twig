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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.twig.localize.TwigLocalize;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "TWIG";
	}

	@Nonnull
	@Override
	public LocalizeValue getDescription()
	{
		return TwigLocalize.twigFileTypeDescription();
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
