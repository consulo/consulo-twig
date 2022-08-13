/**
 * @author VISTALL
 * @since 13-Aug-22
 */
module consulo.twig {
	requires consulo.ide.api;
	requires consulo.language.impl;

	requires com.intellij.xml;

	exports consulo.twig;
	exports consulo.twig.completion;
	exports consulo.twig.editor;
	exports consulo.twig.highlight;
	exports consulo.twig.lexer;
	exports consulo.twig.localize;
	exports consulo.twig.parser;
	exports consulo.twig.psi;
	exports consulo.twig.psi.impl;
	exports consulo.twig.psi.impl.light;
	exports consulo.twig.psi.references;
	exports consulo.twig.table;
}