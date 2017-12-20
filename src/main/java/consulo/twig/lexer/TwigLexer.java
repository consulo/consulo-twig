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

package consulo.twig.lexer;

import java.io.Reader;

import consulo.twig.psi.TwigTokens;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigLexer extends MergingLexerAdapter
{
	public TwigLexer()
	{
		super(new FlexAdapter(new _TwigLexer((Reader) null)), TokenSet.create(TwigTokens.COMMENT));
	}
}
