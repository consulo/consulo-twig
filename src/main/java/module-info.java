/**
 * @author VISTALL
 * @since 08/01/2023
 */
import org.jspecify.annotations.NullMarked;

@NullMarked
module consulo.twig
{
	requires consulo.application.api;
	requires consulo.code.editor.api;
	requires consulo.color.scheme.api;
	requires consulo.document.api;
	requires consulo.language.api;
	requires consulo.language.editor.api;
	requires consulo.language.impl;
	requires consulo.localize.api;
	requires consulo.logging.api;
	requires consulo.project.api;
	requires consulo.ui.api;
	requires consulo.util.collection;
	requires consulo.util.jdom;
	requires consulo.util.lang;
	requires consulo.virtual.file.system.api;

	requires com.intellij.xml.html.api;

	exports com.jetbrains.twig;
	exports com.jetbrains.twig.elements;
	exports com.jetbrains.twig.icons;
	exports com.jetbrains.twig.lexer;
	exports com.jetbrains.twig.parser;
	exports consulo.twig;
	exports consulo.twig.icon;
	exports consulo.twig.completion;
	exports consulo.twig.editor;
	exports consulo.twig.highlight;
	exports consulo.twig.localize;
	exports consulo.twig.table;
}
