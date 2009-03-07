/*
 * ValidateBufferToMarkdownSerialization.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import markerpen.docbook.Document;
import markerpen.textbase.Change;
import markerpen.textbase.CharacterSpan;
import markerpen.textbase.Common;
import markerpen.textbase.InsertChange;
import markerpen.textbase.Segment;
import markerpen.textbase.Span;
import markerpen.textbase.StringSpan;
import markerpen.textbase.TextStack;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import static markerpen.converter.DocBookConverter.parseTree;

public class ValidateStackToDocBookConversion extends TestCase
{
    public final void testLoadDocbook() throws IOException, ValidityException, ParsingException {
        final File source;
        final Segment[] segments;
        final TextStack text;

        source = new File("tests/markerpen/converter/HelloWorld.xml");
        assertTrue(source.exists());

        segments = parseTree(source);

        assertEquals(1, segments.length);

        text = segments[0].getText();
        assertNotNull(text);
        assertEquals("Hello world", text.toString());
    }

    public final void testWritePlainParas() throws IOException {
        final TextStack stack;
        final Span span;
        final Change change;
        final DocBookConverter converter;
        final Document book;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a trivial example
         */

        span = new StringSpan("Hello\nWorld", null);
        change = new InsertChange(0, span);
        stack = new TextStack();
        stack.apply(change);

        /*
         * Now run conversion process.
         */

        converter = new DocBookConverter();
        converter.append(stack);
        book = converter.result();

        assertNotNull(book);

        out = new ByteArrayOutputStream();
        book.toXML(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<book version=\"5.0\" xmlns=\"http://docbook.org/ns/docbook\">",
                "<chapter>",
                "<section>",
                "<para>",
                "Hello",
                "</para>",
                "<para>",
                "World",
                "</para>",
                "</section>",
                "</chapter>",
                "</book>"
        });
        assertEquals(blob, out.toString());
    }

    private static String combine(String[] elements) {
        StringBuilder buf;

        buf = new StringBuilder(128);

        for (String element : elements) {
            buf.append(element);
            buf.append('\n');
        }

        return buf.toString();
    }

    public final void testWriteComplexPara() throws IOException {
        final TextStack stack;
        final Span[] spans;
        int offset;
        Change change;
        final DocBookConverter converter;
        final Document book;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a more complicated example
         */

        spans = new Span[] {
                new StringSpan("Accessing the ", null),
                new StringSpan("/tmp", Common.FILENAME),
                new StringSpan(" directory directly is fine, but the ", null),
                new StringSpan("File", Common.TYPE),
                new StringSpan(" class ", null),
                new CharacterSpan('h', null),
                new CharacterSpan('a', null),
                new CharacterSpan('s', null),
                new CharacterSpan(' ', null),
                new CharacterSpan('a', null),
                new CharacterSpan(' ', null),
                new StringSpan("createTemp", Common.FUNCTION),
                new CharacterSpan('F', Common.FUNCTION),
                new CharacterSpan('i', Common.FUNCTION),
                new CharacterSpan('l', Common.FUNCTION),
                new CharacterSpan('e', Common.FUNCTION),
                new StringSpan("()", Common.FUNCTION),
                new StringSpan(" function that you are often better off using.", null),
        };

        stack = new TextStack();
        offset = 0;

        for (Span span : spans) {
            change = new InsertChange(offset, span);
            stack.apply(change);
            offset += span.getWidth();
        }

        /*
         * Now run conversion process.
         */

        converter = new DocBookConverter();
        converter.append(stack);
        book = converter.result();

        assertNotNull(book);

        out = new ByteArrayOutputStream();
        book.toXML(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<book version=\"5.0\" xmlns=\"http://docbook.org/ns/docbook\">",
                "<chapter>",
                "<section>",
                "<para>",
                "Accessing the " + "<filename>/tmp</filename>" + " directory",
                "directly is fine, but the " + "<type>" + "File" + "</type>",
                "class has a " + "<function>" + "createTempFile()" + "</function>",
                "function that you are often better off using.",
                "</para>",
                "</section>",
                "</chapter>",
                "</book>"
        });

        /*
         * WARNING. If the word wrap width of our save output changes, then
         * the expected result blob will need to be reformatted to for the
         * test to pass. That's acceptable.
         */
        assertEquals(blob, out.toString());
    }

    public final void testLoadComplexDocument() throws IOException, ValidityException, ParsingException {
        final File source;
        final Segment[] segments;
        final TextStack text;

        source = new File("tests/markerpen/converter/TemporaryFiles.xml");
        assertTrue(source.exists());

        segments = parseTree(source);
        assertEquals(1, segments.length);

        text = segments[0].getText();

        assertNotNull(text);
        assertEquals("Accessing the /tmp directory directly is fine, "
                + "but the File class has a createTempFile() function "
                + "that you are often better off using.", text.toString());
    }
}
