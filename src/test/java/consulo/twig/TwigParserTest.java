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

import com.jetbrains.twig.TwigFile;
import com.jetbrains.twig.TwigLanguage;
import com.jetbrains.twig.TwigTokenTypes;
import com.jetbrains.twig.elements.TwigArrayLiteral;
import com.jetbrains.twig.elements.TwigBlockStatement;
import com.jetbrains.twig.elements.TwigComment;
import com.jetbrains.twig.elements.TwigCompositeElement;
import com.jetbrains.twig.elements.TwigElementTypes;
import com.jetbrains.twig.elements.TwigFieldReference;
import com.jetbrains.twig.elements.TwigStatement;
import com.jetbrains.twig.elements.TwigTag;
import com.jetbrains.twig.elements.TwigTagWithFileReference;
import com.jetbrains.twig.elements.TwigVariableReference;
import consulo.language.ast.ASTNode;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileFactory;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.test.junit.impl.extension.ConsuloProjectLoader;
import consulo.test.junit.impl.extension.InjectingRecord;
import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

/**
 * The tree in the shape of PhpStorm's Twig plugin.
 * Notation: composites are TYPE[children], leaves TYPE(text) or the text of delimiters and quotes, whitespace is left out.
 *
 * @author VISTALL
 * @since 2026-10-30
 */
@NullUnmarked
@ExtendWith(ConsuloProjectLoader.class)
public class TwigParserTest {
    public record Context(PsiFileFactory psiFileFactory) implements InjectingRecord {
    }

    private static final TokenSet NAMED_LEAVES = TokenSet.create(TwigTokenTypes.IDENTIFIER, TwigTokenTypes.STRING_TEXT, TwigTokenTypes.TAG_NAME,
        TwigTokenTypes.TEXT, TwigTokenTypes.COMMENT_TEXT, TwigTokenTypes.NUMBER, TwigTokenTypes.RESERVED_ID);

    private static PsiFile parse(Context context, String text) {
        PsiFile file = context.psiFileFactory().createFileFromText("a.twig", TwigLanguage.INSTANCE, text);
        Assertions.assertEquals(text, file.getText());
        return file;
    }

    private static String tree(Context context, String text) {
        return children(parse(context, text).getNode());
    }

    private static String children(ASTNode parent) {
        List<String> parts = new ArrayList<>();
        for (ASTNode child = parent.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (!(child.getPsi() instanceof PsiWhiteSpace)) {
                parts.add(node(child));
            }
        }
        return String.join(" ", parts);
    }

    private static String node(ASTNode node) {
        String type = node.getElementType().toString();
        if (node.getFirstChildNode() != null) {
            return type + "[" + children(node) + "]";
        }
        if (NAMED_LEAVES.contains(node.getElementType())) {
            return type + "(" + node.getText() + ")";
        }
        return node.getText();
    }

    @Test
    public void testPrintBlocks(Context context) {
        Assertions.assertEquals("PRINT_BLOCK[{{ VARIABLE_REFERENCE[IDENTIFIER(foo)] }}]", tree(context, "{{ foo }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ FIELD_REFERENCE[FIELD_REFERENCE[VARIABLE_REFERENCE[IDENTIFIER(a)] . IDENTIFIER(b)] . IDENTIFIER(c)] }}]",
            tree(context, "{{ a.b.c }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ METHOD_CALL[VARIABLE_REFERENCE[IDENTIFIER(foo)] . IDENTIFIER(bar) ( NUMBER(1) )] }}]",
            tree(context, "{{ foo.bar(1) }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ FIELD_REFERENCE[VARIABLE_REFERENCE[IDENTIFIER(foo)] [ ' STRING_TEXT(x) ' ]] }}]",
            tree(context, "{{ foo['x'] }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ ' STRING_TEXT(x) ' | VARIABLE_REFERENCE[IDENTIFIER(trans)] }}]", tree(context, "{{ 'x'|trans }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ ' STRING_TEXT(x) ' | FUNCTION_CALL[IDENTIFIER(trans) ( LITERAL[{ }] , ' STRING_TEXT(dom) ' )] }}]",
            tree(context, "{{ 'x'|trans({}, 'dom') }}"));
        Assertions.assertEquals(
            "PRINT_BLOCK[{{ FUNCTION_CALL[IDENTIFIER(path) ( ' STRING_TEXT(route) ' , LITERAL[{ ' STRING_TEXT(a) ' : VARIABLE_REFERENCE[IDENTIFIER(b)] , IDENTIFIER(id) : NUMBER(1) }] )] }}]",
            tree(context, "{{ path('route', {'a': b, id: 1}) }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ FUNCTION_CALL[IDENTIFIER(trans) ( VARIABLE_REFERENCE[IDENTIFIER(domain)] : ' STRING_TEXT(x) ' )] }}]",
            tree(context, "{{ trans(domain: 'x') }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{ RESERVED_ID(_self) . FUNCTION_CALL[IDENTIFIER(input) ( ' ' )] }}]", tree(context, "{{ _self.input('') }}"));
        Assertions.assertEquals("PRINT_BLOCK[{{- VARIABLE_REFERENCE[IDENTIFIER(a)] ~ \" STRING_TEXT(b) \" -}}]", tree(context, "{{- a ~ \"b\" -}}"));
        // '}}' of a hash is not the end of the block
        Assertions.assertEquals("PRINT_BLOCK[{{ LITERAL[{ IDENTIFIER(a) : LITERAL[{ IDENTIFIER(b) : NUMBER(1) }] }] }}]", tree(context, "{{ {a: {b: 1}} }}"));
    }

    @Test
    public void testArrays(Context context) {
        Assertions.assertEquals(
            "TAG[{% TAG_NAME(foo) ARRAY_LITERAL[[ ARRAY_VALUE[IDENTIFIER(a)] , ARRAY_VALUE[' STRING_TEXT(b) ' ~ IDENTIFIER(c)] ]] %}]",
            tree(context, "{% foo [a, 'b' ~ c] %}"));
        PsiFile file = parse(context, "{% if x in ['a', 'b'] %}{% endif %}");
        TwigArrayLiteral array = PsiTreeUtil.findChildOfType(file, TwigArrayLiteral.class);
        Assertions.assertEquals(2, array.getArrayValues().size());
    }

    @Test
    public void testBlocks(Context context) {
        Assertions.assertEquals(
            "BLOCK_STATEMENT[BLOCK_TAG[{% TAG_NAME(block) IDENTIFIER(body) %}] TEXT(x ) PRINT_BLOCK[{{ FUNCTION_CALL[IDENTIFIER(parent) ( )] }}] TAG[{% TAG_NAME(endblock) %}]]",
            tree(context, "{% block body %}x {{ parent() }}{% endblock %}"));
        // the short form has no body
        Assertions.assertEquals(
            "BLOCK_STATEMENT[BLOCK_TAG[{% TAG_NAME(block) IDENTIFIER(outer) %}] BLOCK_STATEMENT[BLOCK_TAG[{% TAG_NAME(block) IDENTIFIER(title) ' STRING_TEXT(x) ' %}]] TAG[{% TAG_NAME(endblock) %}]]",
            tree(context, "{% block outer %}{% block title 'x' %}{% endblock %}"));

        PsiFile file = parse(context, "{%- block 'quoted' -%}{% endblock %}");
        TwigBlockStatement block = (TwigBlockStatement) file.getFirstChild();
        Assertions.assertEquals("quoted", block.getName());
    }

    @Test
    public void testIncludeTags(Context context) {
        Assertions.assertEquals("EXTENDS_TAG[{% TAG_NAME(extends) ' STRING_TEXT(base.html.twig) ' %}]", tree(context, "{% extends 'base.html.twig' %}"));
        Assertions.assertEquals(
            "INCLUDE_TAG[{% TAG_NAME(include) ' STRING_TEXT(x.html.twig) ' IDENTIFIER(with) LITERAL[{ ' STRING_TEXT(a) ' : VARIABLE_REFERENCE[IDENTIFIER(b)] }] IDENTIFIER(only) %}]",
            tree(context, "{% include 'x.html.twig' with {'a': b} only %}"));
        Assertions.assertEquals(
            "IMPORT_TAG[{% TAG_NAME(import) ' STRING_TEXT(forms.html) ' as VARIABLE_REFERENCE[IDENTIFIER(forms)] %}]",
            tree(context, "{% import 'forms.html' as forms %}"));
        Assertions.assertEquals(
            "IMPORT_TAG[{% TAG_NAME(from) RESERVED_ID(_self) import VARIABLE_REFERENCE[IDENTIFIER(a)] as VARIABLE_REFERENCE[IDENTIFIER(b)] , VARIABLE_REFERENCE[IDENTIFIER(c)] %}]",
            tree(context, "{% from _self import a as b, c %}"));

        PsiFile file = parse(context, "{% from 'x' import a as b %}");
        PsiElement importTag = file.getFirstChild();
        Assertions.assertNotNull(importTag.getNode().findChildByType(TwigTokenTypes.IMPORT_KEYWORD));
        Assertions.assertNotNull(importTag.getNode().findChildByType(TwigTokenTypes.AS_KEYWORD));
        Assertions.assertInstanceOf(TwigTagWithFileReference.class, importTag);
        // embed is no file reference tag
        Assertions.assertFalse(parse(context, "{% embed 'x' %}{% endembed %}").getFirstChild().getFirstChild() instanceof TwigTagWithFileReference);
    }

    @Test
    public void testStatements(Context context) {
        Assertions.assertEquals(
            "IF_STATEMENT[IF_TAG[{% TAG_NAME(if) VARIABLE_REFERENCE[IDENTIFIER(a)] and VARIABLE_REFERENCE[IDENTIFIER(b)] %}] TEXT(x) "
                + "ELSEIF_TAG[{% TAG_NAME(elseif) VARIABLE_REFERENCE[IDENTIFIER(c)] %}] TEXT(y) ELSE_TAG[{% TAG_NAME(else) %}] TEXT(z) TAG[{% TAG_NAME(endif) %}]]",
            tree(context, "{% if a and b %}x{% elseif c %}y{% else %}z{% endif %}"));
        Assertions.assertEquals(
            "FOR_STATEMENT[FOR_TAG[{% TAG_NAME(for) VARIABLE_REFERENCE[IDENTIFIER(key)] , VARIABLE_REFERENCE[IDENTIFIER(user)] in VARIABLE_REFERENCE[IDENTIFIER(users)] | VARIABLE_REFERENCE[IDENTIFIER(filter)] %}] "
                + "ELSE_TAG[{% TAG_NAME(else) %}] TAG[{% TAG_NAME(endfor) %}]]",
            tree(context, "{% for key, user in users|filter %}{% else %}{% endfor %}"));
        Assertions.assertEquals("SET_TAG[{% TAG_NAME(set) VARIABLE_REFERENCE[IDENTIFIER(foo)] = FUNCTION_CALL[IDENTIFIER(bar) ( ' STRING_TEXT(x) ' )] %}]",
            tree(context, "{% set foo = bar('x') %}"));
        Assertions.assertEquals("SET_STATEMENT[SET_TAG[{% TAG_NAME(set) VARIABLE_REFERENCE[IDENTIFIER(foo)] %}] TEXT(x) TAG[{% TAG_NAME(endset) %}]]",
            tree(context, "{% set foo %}x{% endset %}"));
        Assertions.assertEquals(
            "MACRO_STATEMENT[MACRO_TAG[{% TAG_NAME(macro) IDENTIFIER(input) ( VARIABLE_REFERENCE[IDENTIFIER(name)] , VARIABLE_REFERENCE[IDENTIFIER(value)] = ' ' ) %}] TAG[{% TAG_NAME(endmacro) %}]]",
            tree(context, "{% macro input(name, value = '') %}{% endmacro %}"));
        Assertions.assertEquals("FILTER_STATEMENT[FILTER_TAG[{% TAG_NAME(filter) IDENTIFIER(upper) %}] TEXT(x) TAG[{% TAG_NAME(endfilter) %}]]",
            tree(context, "{% filter upper %}x{% endfilter %}"));
        // standalone else and elseif parse an expression
        Assertions.assertEquals("ELSEIF_TAG[{% TAG_NAME(elseif) FUNCTION_CALL[IDENTIFIER(foo) ( ' STRING_TEXT(x) ' )] %}]",
            tree(context, "{% elseif foo('x') %}"));
    }

    @Test
    public void testGenericTags(Context context) {
        Assertions.assertEquals("TAG[{% TAG_NAME(form_theme) IDENTIFIER(form) . IDENTIFIER(child) ' STRING_TEXT(f.html.twig) ' %}]",
            tree(context, "{% form_theme form.child 'f.html.twig' %}"));

        String component = "{% component Alert with {type: 'x'} %}body{% endcomponent %}";
        Assertions.assertEquals(
            "TAG_STATEMENT[TAG[{% TAG_NAME(component) IDENTIFIER(Alert) IDENTIFIER(with) LITERAL[{ IDENTIFIER(type) : ' STRING_TEXT(x) ' }] %}] TEXT(body) TAG[{% TAG_NAME(endcomponent) %}]]",
            tree(context, component));
        PsiElement statement = parse(context, component).getFirstChild();
        Assertions.assertInstanceOf(TwigStatement.class, statement);
        Assertions.assertInstanceOf(TwigTag.class, statement.getNode().getElementType());

        // the text after a tag is one token, its previous sibling is the tag
        PsiFile trans = parse(context, "{% trans %} text {{ a }}{% endtrans %}");
        PsiElement text = trans.getFirstChild().getFirstChild().getNextSibling();
        Assertions.assertEquals(TwigTokenTypes.TEXT, text.getNode().getElementType());
        Assertions.assertEquals(" text ", text.getText());

        // without an end tag, no statement
        Assertions.assertEquals("TAG[{% TAG_NAME(render) ' STRING_TEXT(x) ' %}] TEXT(a)", tree(context, "{% render 'x' %}a"));
        // types tag
        Assertions.assertEquals(
            "TAG[{% TAG_NAME(types) LITERAL[{ IDENTIFIER(foo) : ' STRING_TEXT(X) ' , IDENTIFIER(bar) ? : ' STRING_TEXT(Y) ' }] %}]",
            tree(context, "{% types { foo: 'X', bar?: 'Y' } %}"));
    }

    @Test
    public void testComments(Context context) {
        Assertions.assertEquals("COMMENT[{# COMMENT_TEXT(@var foo \\Foo ) #}]", tree(context, "{# @var foo \\Foo #}"));
        PsiFile file = parse(context, "{#\n  multi\n  line\n#}");
        PsiElement comment = file.getFirstChild();
        Assertions.assertInstanceOf(PsiComment.class, comment);
        Assertions.assertInstanceOf(TwigComment.class, comment);
        Assertions.assertTrue(comment.getText().startsWith("{#"));
        Assertions.assertTrue(comment.getNode().findChildByType(TwigTokenTypes.COMMENT_TEXT).getText().startsWith("multi"));
    }

    @Test
    public void testStringsAndVerbatim(Context context) {
        // empty string: no STRING_TEXT; interpolation stays in the text
        Assertions.assertEquals("PRINT_BLOCK[{{ ' ' \" STRING_TEXT(a #{b} c) \" }}]", tree(context, "{{ '' \"a #{b} c\" }}"));
        Assertions.assertEquals("TAG_STATEMENT[TAG[{% TAG_NAME(verbatim) %}] TEXT({{ not twig }}) TAG[{% TAG_NAME(endverbatim) %}]]",
            tree(context, "{% verbatim %}{{ not twig }}{% endverbatim %}"));
    }

    @Test
    public void testPsiApi(Context context) {
        PsiFile file = parse(context, "{{ foo.bar }}{% block a %}{% endblock %}");
        Assertions.assertInstanceOf(TwigFile.class, file);

        TwigFieldReference field = PsiTreeUtil.findChildOfType(file, TwigFieldReference.class);
        Assertions.assertEquals("foo", ((TwigVariableReference) field.getOwner()).getName());
        Assertions.assertEquals("bar", field.getName());

        // composites only, top level elements are file children
        List<PsiElement> children = List.of(file.getChildren());
        Assertions.assertEquals(2, children.size());
        Assertions.assertTrue(children.stream().allMatch(TwigCompositeElement.class::isInstance));
        Assertions.assertEquals(TwigElementTypes.PRINT_BLOCK, children.get(0).getNode().getElementType());
        Assertions.assertEquals("PRINT_BLOCK", TwigElementTypes.PRINT_BLOCK.toString());
        Assertions.assertSame(TwigLanguage.INSTANCE, TwigElementTypes.FUNCTION_CALL.getLanguage());
    }

    @Test
    public void testBrokenInput(Context context) {
        // no exceptions, text is kept
        parse(context, "{% foo 'v' }%");
        parse(context, "{{ foobar({}, 'a");
        parse(context, "{% if %}{% for %}{% endif %}");
        parse(context, "{% %}");
        parse(context, "{# open");
        parse(context, "{");
        parse(context, "{% block a %}{{ x }");
    }

    @Test
    public void testHtmlLayerHasNoErrors(Context context) {
        PsiFile file = parse(context, "<div>\n<a href=\"{{ path('app_about') }}\">About</a>\n{% if a %}<b>x</b>{% endif %}\n</div>");
        PsiFile html = file.getViewProvider().getPsi(consulo.html.language.HTMLLanguage.INSTANCE);
        Assertions.assertNotNull(html);
        List<String> errors = PsiTreeUtil.findChildrenOfType(html, consulo.language.psi.PsiErrorElement.class).stream()
            .map(error -> error.getErrorDescription() + " at " + error.getTextOffset() + " '" + error.getParent().getText() + "'")
            .toList();
        Assertions.assertEquals(List.of(), errors, consulo.language.impl.DebugUtil.psiToString(html, false));
    }
}
