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

package consulo.twig.table;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
public class TwigTableBlock
{
	public static enum TwigBlockType
	{
		BLOCK("block"),
		LINE_BLOCK("line-block"),
		SEMI_BLOCK("semi-block");
		private final String myTag;

		TwigBlockType(String tag)
		{
			myTag = tag;
		}

		public static TwigBlockType byTab(String tag)
		{
			for(TwigBlockType twigBlockType : values())
			{
				if(twigBlockType.myTag.equals(tag))
				{
					return twigBlockType;
				}
			}
			return null;
		}
	}

	private String myName;
	private TwigBlockType myType;

	public TwigTableBlock(String name, TwigBlockType type)
	{
		myName = name;
		myType = type;
	}

	public String getName()
	{
		return myName;
	}

	public TwigBlockType getType()
	{
		return myType;
	}
}
