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

package consulo.twig.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.action.TypedHandlerDelegate;
import consulo.language.psi.PsiFile;
import consulo.project.Project;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.11.13.
 */
@ExtensionImpl
public class TwigTypedHandler extends TypedHandlerDelegate
{
	private static char[][] ourInsertChars = new char[][]{
			{
					'{',
					'%',
					'%',
					'}'
			},
			{
					'{',
					'{',
					'}',
					'}'
			},
			{
					'{',
					'#',
					'#',
					'}'
			}
	};

	@Override
	public Result charTyped(char c, Project project, Editor editor, @Nonnull PsiFile file)
	{
		if(editor.getDocument().getTextLength() <= 1)
		{
			return super.charTyped(c, project, editor, file);
		}

		int offset = editor.getCaretModel().getOffset();
		for(char[] insertChar : ourInsertChars)
		{
			if(insertChar[1] == c)
			{
				CharSequence charsSequence = editor.getDocument().getCharsSequence();
				if(charsSequence.charAt(offset - 2) == insertChar[0])
				{
					editor.getDocument().insertString(offset, new StringBuilder(4).append("  ").append(insertChar[2]).append(insertChar[3]));
					editor.getCaretModel().moveToOffset(offset + 1);
					return Result.STOP;
				}
			}
		}

		return super.charTyped(c, project, editor, file);
	}
}
