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

import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import consulo.language.ast.ASTNode;
import consulo.language.ast.ElementTypeAsPsiFactory;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.psi.PsiElement;
import consulo.language.impl.psi.template.TemplateDataElementType;

import java.util.function.Function;

/**
 * Composites of Twig, in the shape of PhpStorm's {@code TwigElementTypes}; the debug names are the constant names
 *
 * @author VISTALL
 * @since 2026-10-30
 */
public interface TwigElementTypes {
    IFileElementType FILE = new IFileElementType("TWIG_FILE", TwigLanguage.INSTANCE);

    IElementType OUTER_ELEMENT_TYPE = new IElementType("TWIG_FRAGMENT", TwigLanguage.INSTANCE);
    TemplateDataElementType TEMPLATE_DATA =
        new TemplateDataElementType("TWIG_TEMPLATE_DATA", TwigLanguage.INSTANCE, TwigTokenTypes.TEXT, OUTER_ELEMENT_TYPE);

    IElementType COMMENT = type("COMMENT", TwigComment::new);

    IElementType PRINT_BLOCK = type("PRINT_BLOCK", TwigCompositeElement::new);
    /**
     * Tags without an own type, end tags included
     */
    IElementType TAG = type("TAG", TwigCompositeElement::new);
    /**
     * Statements of the tags without an own type
     */
    IElementType TAG_STATEMENT = new TwigTag("TAG_STATEMENT");

    IElementType BLOCK_TAG = type("BLOCK_TAG", TwigBlockTag::new);
    IElementType BLOCK_STATEMENT = type("BLOCK_STATEMENT", TwigBlockStatement::new);
    IElementType IF_TAG = type("IF_TAG", TwigCompositeElement::new);
    IElementType ELSEIF_TAG = type("ELSEIF_TAG", TwigCompositeElement::new);
    IElementType ELSE_TAG = type("ELSE_TAG", TwigCompositeElement::new);
    IElementType IF_STATEMENT = type("IF_STATEMENT", TwigStatement::new);
    IElementType FOR_TAG = type("FOR_TAG", TwigCompositeElement::new);
    IElementType FOR_STATEMENT = type("FOR_STATEMENT", TwigStatement::new);
    IElementType SET_TAG = type("SET_TAG", TwigCompositeElement::new);
    IElementType SET_STATEMENT = type("SET_STATEMENT", TwigStatement::new);
    IElementType MACRO_TAG = type("MACRO_TAG", TwigCompositeElement::new);
    IElementType MACRO_STATEMENT = type("MACRO_STATEMENT", TwigStatement::new);
    IElementType EMBED_TAG = type("EMBED_TAG", TwigCompositeElement::new);
    IElementType EMBED_STATEMENT = type("EMBED_STATEMENT", TwigStatement::new);
    IElementType FILTER_TAG = type("FILTER_TAG", TwigCompositeElement::new);
    IElementType FILTER_STATEMENT = type("FILTER_STATEMENT", TwigStatement::new);
    IElementType INCLUDE_TAG = type("INCLUDE_TAG", TwigTagWithFileReference::new);
    /**
     * {@code {% import %}} and {@code {% from %}}
     */
    IElementType IMPORT_TAG = type("IMPORT_TAG", TwigTagWithFileReference::new);
    IElementType EXTENDS_TAG = type("EXTENDS_TAG", TwigExtendsTag::new);

    IElementType VARIABLE_REFERENCE = type("VARIABLE_REFERENCE", TwigVariableReference::new);
    IElementType FIELD_REFERENCE = type("FIELD_REFERENCE", TwigFieldReference::new);
    IElementType METHOD_CALL = type("METHOD_CALL", TwigFieldReference::new);
    /**
     * {@code foo(args)}: the name identifier is the first child
     */
    IElementType FUNCTION_CALL = type("FUNCTION_CALL", TwigCompositeElement::new);
    /**
     * Hash {@code {key: value}}
     */
    IElementType LITERAL = type("LITERAL", TwigCompositeElement::new);
    IElementType ARRAY_LITERAL = type("ARRAY_LITERAL", TwigArrayLiteral::new);
    IElementType ARRAY_VALUE = type("ARRAY_VALUE", TwigArrayValue::new);

    private static IElementType type(String debugName, Function<ASTNode, ? extends PsiElement> factory) {
        return new ElementTypeAsPsiFactory(debugName, TwigLanguage.INSTANCE, factory);
    }
}
