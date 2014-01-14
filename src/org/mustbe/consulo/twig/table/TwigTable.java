/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.twig.table;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import com.intellij.openapi.util.JDOMUtil;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
@Logger
public class TwigTable
{
	public static TwigTable INSTANCE = new TwigTable();

	private Map<String, TwigTableBlock> myBlocks = new HashMap<String, TwigTableBlock>();

	private TwigTable()
	{
		load();
	}

	private void load()
	{
		InputStream resourceAsStream = getClass().getResourceAsStream("/twigTable.xml");
		if(resourceAsStream == null)
		{
			LOGGER.warn("No twig table");
			return;
		}

		try
		{
			Document document = JDOMUtil.loadDocument(resourceAsStream);
			loadDocument(document.getRootElement());
		}
		catch(JDOMException e)
		{
			TwigTable.LOGGER.error(e);
		}
		catch(IOException e)
		{
			TwigTable.LOGGER.error(e);
		}
	}

	private void loadDocument(Element element)
	{
		for(Element child : element.getChildren())
		{
			TwigTableBlock.TwigBlockType block = TwigTableBlock.TwigBlockType.byTab(child.getName());
			if(block != null)
			{
				String name = child.getAttributeValue("name");
				myBlocks.put(name, new TwigTableBlock(name, block));
			}
		}
	}

	public TwigTableBlock getBlock(String name)
	{
		return myBlocks.get(name);
	}

	public Collection<TwigTableBlock> getBlocks()
	{
		return myBlocks.values();
	}
}
