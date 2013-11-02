/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.twig.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.twig.psi.TwigElements;
import org.mustbe.consulo.twig.psi.TwigTokens;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public class TwigParser implements PsiParser, TwigTokens, TwigElements
{
	@NotNull
	private static Pair<Boolean, String> tagInfo(@Nullable String tagName)
	{
		if(tagName == null)
		{
			return new Pair<Boolean, String>(false, null);
		}
		if(StringUtil.startsWith(tagName, "end"))
		{
			return new Pair<Boolean, String>(true, tagName.substring(3, tagName.length()));
		}
		else
		{
			return new Pair<Boolean, String>(false, tagName);
		}
	}

	@NotNull
	@Override
	public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder, @NotNull LanguageVersion languageVersion)
	{
		PsiBuilder.Marker mark = builder.mark();

		Deque<Pair<PsiBuilder.Marker, String>> tags = new ArrayDeque<Pair<PsiBuilder.Marker, String>>();

		parseAll(builder, tags);

		Iterator<Pair<PsiBuilder.Marker, String>> pairIterator = tags.descendingIterator();
		while(pairIterator.hasNext())
		{
			Pair<PsiBuilder.Marker, String> next = pairIterator.next();

			next.getFirst().done(BLOCK);
		}

		mark.done(elementType);
		return builder.getTreeBuilt();
	}

	private void parseAll(PsiBuilder builder, Deque<Pair<PsiBuilder.Marker, String>> tags)
	{
		while(!builder.eof())
		{
			IElementType tokenType = builder.getTokenType();
			if(tokenType == STMT_OPEN)
			{
				parseTag(builder, tags);
			}
			builder.advanceLexer();
		}
	}

	private void parseTag(PsiBuilder builder, Deque<Pair<PsiBuilder.Marker, String>> tags)
	{
		PsiBuilder.Marker marker = builder.mark();

		builder.advanceLexer();

		Pair<Boolean, String> tagInfo = tagInfo(null);

		Pair<PsiBuilder.Marker, String> lastTag = tags.peekLast();

		while(!builder.eof() && builder.getTokenType() != STMT_CLOSE)
		{
			if(builder.getTokenType() == T_BLOCK_NAME)
			{
				tagInfo = tagInfo(builder.getTokenText());
			}

			builder.advanceLexer();
		}

		if(tagInfo.getSecond() == null)
		{
			builder.error("Name expected");
		}

		if(builder.getTokenType() == STMT_CLOSE)
		{
			builder.advanceLexer();
		}
		else
		{
			builder.error("%} expected");
		}

		marker.done(tagInfo.getFirst() ? CLOSE_TAG : OPEN_TAG);

		if(tagInfo.getFirst())
		{
			if(lastTag != null)
			{
				lastTag = tags.pollLast();
				lastTag.getFirst().done(BLOCK);
			}
		}
		else
		{
			if(tagInfo.getSecond() != null)
			{
				tags.addLast(new Pair<PsiBuilder.Marker, String>(marker.precede(), tagInfo.getSecond()));

				parseAll(builder, tags);
			}
		}
	}
}
