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
			else if(tokenType == VAR_OPEN)
			{
				parseExpression(builder);
			}
			else
			{
				builder.advanceLexer();
			}
		}
	}

	private void parseExpression(PsiBuilder builder)
	{
		PsiBuilder.Marker marker = builder.mark();

		builder.advanceLexer();

		PsiBuilder.Marker expressionMarker = parseBigExpression(builder);
		if(expressionMarker == null)
		{
			builder.error("Expression expected");
		}

		if(builder.getTokenType() == VAR_CLOSE)
		{
			builder.advanceLexer();
		}
		else
		{
			builder.error("}} expected");
		}

		marker.done(EXPRESSION_BODY);
	}

	private PsiBuilder.Marker parseSingleExpression(PsiBuilder builder)
	{
		PsiBuilder.Marker marker = builder.mark();
		IElementType tokenType = builder.getTokenType();
		if(tokenType == IDENTIFIER)
		{
			builder.advanceLexer();
			marker.done(REFERENCE_EXPRESSION);
		}
		else if(tokenType == STRING || tokenType == DSTRING)
		{
			builder.advanceLexer();
			marker.done(CONSTANT_EXPRESSION);
		}
		else
		{
			marker.drop();
			marker = null;
		}
		return marker;
	}

	private PsiBuilder.Marker parseBigExpression(PsiBuilder builder)
	{
		PsiBuilder.Marker marker = parseSingleExpression(builder);
		if(marker == null)
		{
			return null;
		}

		IElementType tokenType = builder.getTokenType();
		if(TwigTokens.BINARY_TOKENS.contains(tokenType))
		{
			marker = marker.precede();

			builder.advanceLexer();

			PsiBuilder.Marker expMarker = parseSingleExpression(builder);
			if(expMarker == null)
			{
				builder.error("Expression expected");
			}

			marker.done(BINARY_EXPRESSION);
		}
		return marker;
	}

	private void parseTag(PsiBuilder builder, Deque<Pair<PsiBuilder.Marker, String>> tags)
	{
		PsiBuilder.Marker marker = builder.mark();

		builder.advanceLexer();

		Pair<Boolean, String> tagInfo = tagInfo(null);
		if(builder.getTokenType() != BLOCK_NAME)
		{
			builder.error("Tag name expected");
		}
		else
		{
			tagInfo = tagInfo(builder.getTokenText());

			builder.advanceLexer();

			parseBigExpression(builder);
		}

		Pair<PsiBuilder.Marker, String> lastTag = tags.peekLast();

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
