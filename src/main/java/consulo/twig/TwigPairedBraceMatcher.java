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
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigPairedBraceMatcher implements PairedBraceMatcher
{
	private static final BracePair[] ourPairs = new BracePair[]
	{
	//	new BracePair(TwigTokens.STMT_OPEN, TwigTokens.STMT_CLOSE, false),
	//	new BracePair(TwigTokens.VAR_OPEN, TwigTokens.VAR_CLOSE, false)
	};

	@Override
	public BracePair[] getPairs()
	{
		return ourPairs;
	}

	@Override
	public boolean isPairedBracesAllowedBeforeType(@Nonnull IElementType elementType, @Nullable IElementType elementType2)
	{
		return false;
	}

	@Override
	public int getCodeConstructStart(PsiFile psiFile, int i)
	{
		return i;
	}
}
