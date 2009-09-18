/*
 * ValidateTextChainToQuackConversion.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 * 
 * Forked from quill.converter.ValidateTextChainToDocBookConversion
 */
package quill.quack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.IOTestCase;
import quill.textbase.Change;
import quill.textbase.Common;
import quill.textbase.ComponentSegment;
import quill.textbase.DataLayer;
import quill.textbase.HeadingSegment;
import quill.textbase.InsertTextualChange;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateTextChainToQuackConversion extends IOTestCase
{
    public final void testLoadQuack() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final Series series;
        final TextChain text;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/HelloWorld.xml");

        series = data.getActiveDocument().get(0);
        assertEquals(2, series.size());

        text = series.get(1).getText();
        assertNotNull(text);
        assertEquals("Hello world", text.toString());
    }

    public final void testWritePlainParas() throws IOException {
        final TextChain chain;
        final DataLayer data;
        final Span span;
        final Change change;
        final Segment segment;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a trivial example
         */

        data = new DataLayer();
        chain = new TextChain();

        span = createSpan("Hello\nWorld", null);
        change = new InsertTextualChange(chain, 0, span);
        data.apply(change);

        /*
         * Now run conversion process.
         */

        segment = new NormalSegment();
        segment.setText(chain);

        converter = new QuackConverter();
        converter.append(new ComponentSegment());
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<text>",
                "Hello",
                "</text>",
                "<text>",
                "World",
                "</text>",
                "</chapter>"
        });
        assertEquals(blob, out.toString());
    }

    public final void testWriteComplexPara() throws IOException {
        final DataLayer data;
        final TextChain chain;
        final Span[] spans;
        int offset;
        Change change;
        Segment segment;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a more complicated example
         */

        spans = new Span[] {
                createSpan("Accessing the ", null),
                createSpan("/tmp", Common.FILENAME),
                createSpan(" directory directly is fine, but you are often better off using ", null),
                createSpan("File", Common.TYPE),
                createSpan('\'', null),
                createSpan('s', null),
                createSpan(' ', null),
                createSpan("createTemp", Common.FUNCTION),
                createSpan('F', Common.FUNCTION),
                createSpan('i', Common.FUNCTION),
                createSpan('l', Common.FUNCTION),
                createSpan('e', Common.FUNCTION),
                createSpan("()", Common.FUNCTION),
                createSpan(" function.", null),
        };

        data = new DataLayer();
        chain = new TextChain();
        offset = 0;

        for (Span span : spans) {
            change = new InsertTextualChange(chain, offset, span);
            data.apply(change);
            offset += span.getWidth();
        }

        /*
         * Now run conversion process.
         */
        converter = new QuackConverter();

        segment = new ComponentSegment();
        converter.append(segment);
        segment = new HeadingSegment();
        converter.append(segment);

        segment = new NormalSegment();
        segment.setText(chain);
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<text>",
                "Accessing the " + "<filename>/tmp</filename>" + " directory",
                "directly is fine, but you are often better",
                "off using " + "<type>" + "File" + "</type>'s",
                "<function>" + "createTempFile()" + "</function> function.",
                "</text>",
                "</chapter>"
        });

        /*
         * WARNING. If the word wrap width of our save output changes, then
         * the expected result blob will need to be reformatted to for the
         * test to pass. That's acceptable.
         */
        assertEquals(blob, out.toString());
    }

    public final void testLoadComplexDocument() throws IOException, ValidityException, ParsingException {
        final DataLayer data;
        final Series series;
        final TextChain chain;

        data = new DataLayer();
        data.loadDocument("tests/quill/quack/TemporaryFiles.xml");

        series = data.getActiveDocument().get(0);
        assertEquals(2, series.size());

        chain = series.get(1).getText();

        assertNotNull(chain);
        assertEquals("Accessing the /tmp directory directly is fine, "
                + "but you are often better off using File's createTempFile() function.",
                chain.toString());
    }

    public final void testWriteUnicode() throws IOException {
        final TextChain chain;
        final DataLayer data;
        final Span span;
        final Change change;
        Segment segment;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * This verifies for us that XOM can handle UTF-16 surrogates, at
         * least when they are expressed as Java escapes.
         */

        data = new DataLayer();
        chain = new TextChain();

        span = createSpan(":\ud835\udc5b:", null);
        change = new InsertTextualChange(chain, 0, span);
        data.apply(change);

        converter = new QuackConverter();

        segment = new ComponentSegment();
        converter.append(segment);

        segment = new NormalSegment();
        segment.setText(chain);
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<chapter schema=\"0.1\" xmlns=\"http://operationaldynamics.com/quack\">",
                "<text>",
                ":𝑛:",
                "</text>",
                "</chapter>"
        });
        assertEquals(blob, out.toString());
    }
}
