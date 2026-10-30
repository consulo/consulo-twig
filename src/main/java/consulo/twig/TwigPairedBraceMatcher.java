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

package consulo.twig;

import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.BracePair;
import consulo.language.Language;
import consulo.language.PairedBraceMatcher;

/**
 * @author VISTALL
 * @since 2013-11-02
 */
@ExtensionImpl
public class TwigPairedBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[]{
        new BracePair(TwigTokenTypes.STATEMENT_BLOCK_START, TwigTokenTypes.STATEMENT_BLOCK_END, true),
        new BracePair(TwigTokenTypes.PRINT_BLOCK_START, TwigTokenTypes.PRINT_BLOCK_END, true),
        new BracePair(TwigTokenTypes.COMMENT_START, TwigTokenTypes.COMMENT_END, true),
        new BracePair(TwigTokenTypes.LBRACE, TwigTokenTypes.RBRACE, false),
        new BracePair(TwigTokenTypes.LBRACE_SQ, TwigTokenTypes.RBRACE_SQ, false),
        new BracePair(TwigTokenTypes.LBRACE_CURL, TwigTokenTypes.RBRACE_CURL, false)
    };

    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public Language getLanguage() {
        return TwigLanguage.INSTANCE;
    }
}
