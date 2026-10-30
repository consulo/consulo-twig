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

package com.jetbrains.twig.elements;

import consulo.language.ast.ElementTypeAsPsiFactory;
import com.jetbrains.twig.TwigLanguage;

/**
 * Element type of the statements of tags without an own type: {@code {% trans %}...{% endtrans %}}
 *
 * @author VISTALL
 * @since 2026-10-30
 */
public class TwigTag extends ElementTypeAsPsiFactory {
    public TwigTag(String debugName) {
        super(debugName, TwigLanguage.INSTANCE, TwigStatement::new);
    }
}
