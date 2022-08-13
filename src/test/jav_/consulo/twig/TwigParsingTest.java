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

package consulo.twig;

import consulo.testFramework.ParsingTestCase;

/**
 * @author VISTALL
 * @since 02.11.13.
 */
public abstract class TwigParsingTest extends ParsingTestCase
{
	public TwigParsingTest()
	{
		super("parsing", "twig");
	}

	@Override
	protected boolean checkAllPsiRoots()
	{
		return false;
	}

	@Override
	protected boolean shouldContainTempFiles()
	{
		return false;
	}

	public void testBlockParsing()
	{
		doTest(true);
	}

	public void testBlockParsingNotClosed()
	{
		doTest(true);
	}

	public void testBlockParsingWrongTag()
	{
		doTest(true);
	}

	public void testComments()
	{
		doTest(true);
	}

	public void testExpressionBody()
	{
		doTest(true);
	}

	public void testBlockStart()
	{
		doTest(true);
	}

	public void testBlockParsingWithoutName()
	{
		doTest(true);
	}

	@Override
	protected String getTestDataPath()
	{
		return "/testData";
	}
}
