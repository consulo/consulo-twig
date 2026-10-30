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

import com.jetbrains.twig.TwigTokenTypes;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.ast.TokenSet;
import org.jspecify.annotations.Nullable;

/**
 * {@code {% block name %}} or {@code {% block 'name' %}}
 *
 * @author VISTALL
 * @since 2026-10-30
 */
public class TwigBlockTag extends TwigCompositeElement {
    public TwigBlockTag(ASTNode node) {
        super(node);
    }

    @RequiredReadAction
    @Override
    public @Nullable String getName() {
        ASTNode name = getNode().findChildByType(TokenSet.create(TwigTokenTypes.IDENTIFIER, TwigTokenTypes.STRING_TEXT));
        return name == null ? null : name.getText();
    }
}
