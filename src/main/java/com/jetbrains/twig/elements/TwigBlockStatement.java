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

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import org.jspecify.annotations.Nullable;

/**
 * {@code {% block name %}...{% endblock %}}
 *
 * @author VISTALL
 * @since 2026-10-30
 */
public class TwigBlockStatement extends TwigStatement {
    public TwigBlockStatement(ASTNode node) {
        super(node);
    }

    @RequiredReadAction
    @Override
    public @Nullable String getName() {
        return getFirstChild() instanceof TwigBlockTag blockTag ? blockTag.getName() : null;
    }
}
