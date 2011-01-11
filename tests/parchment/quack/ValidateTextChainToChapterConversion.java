/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2011 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
 */
package parchment.quack;

/*
 * Forked from quill.converter.ValidateTextChainToDocBookConversion
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import parchment.manuscript.Chapter;
import parchment.manuscript.Manuscript;
import quill.client.IOTestCase;
import quill.client.ImproperFilenameException;
import quill.textbase.ChapterSegment;
import quill.textbase.Common;
import quill.textbase.Extract;
import quill.textbase.HeadingSegment;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

public class ValidateTextChainToChapterConversion extends IOTestCase
{
    public final void testLoadQuack() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        final Extract entire;

        manuscript = new Manuscript();
        manuscript.setFilename("tests/parchment/quack/HelloWorld.parchment"); // fake
        chapter = new Chapter(manuscript);
        chapter.setFilename("HelloWorld.xml"); // real
        series = chapter.loadDocument();

        /*
         * Loaded <text> block gives a NormalSegment, now plus an automatic
         * empty FirstSegment for the UI.
         */
        assertEquals(2, series.size());

        entire = series.getSegment(1).getEntire();
        assertNotNull(entire);
        assertEquals("Hello world", entire.getText());
    }

    public final void testWritePlainParas() throws IOException {
        final TextChain chain;
        final Span span;
        Extract entire;
        final Segment segment;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * Build up a trivial example
         */

        chain = new TextChain();

        span = createSpan("Hello\nWorld", null);
        chain.append(span);
        entire = chain.extractAll();

        /*
         * Now run conversion process.
         */

        segment = new NormalSegment(entire);

        entire = Extract.create();
        converter = new QuackConverter();
        converter.append(new ChapterSegment(entire));
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<quack xmlns=\"http://namespace.operationaldynamics.com/parchment/5.0\">",
            "<text>",
            "Hello",
            "</text>",
            "<text>",
            "World",
            "</text>",
            "</quack>"
        });
        assertEquals(blob, out.toString());
    }

    public final void testWriteComplexPara() throws IOException {
        final TextChain chain;
        final Span[] spans;
        Extract entire;
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

        chain = new TextChain();

        for (Span span : spans) {
            chain.append(span);
        }

        /*
         * Now run conversion process.
         */
        converter = new QuackConverter();
        entire = Extract.create();

        segment = new ChapterSegment(entire);
        converter.append(segment);
        segment = new HeadingSegment(entire);
        converter.append(segment);

        entire = chain.extractAll();
        segment = new NormalSegment(entire);
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<quack xmlns=\"http://namespace.operationaldynamics.com/parchment/5.0\">",
            "<text>",
            "Accessing the " + "<filename>/tmp</filename>" + " directory directly is fine,",
            "but you are often better off using " + "<type>" + "File" + "</type>'s",
            "<function>" + "createTempFile()" + "</function> function.",
            "</text>",
            "</quack>"
        });

        /*
         * WARNING. If the word wrap width of our save output changes, then
         * the expected result blob will need to be reformatted to for the
         * test to pass. That's acceptable.
         */
        assertEquals(blob, out.toString());
    }

    public final void testLoadComplexDocument() throws IOException, ValidityException, ParsingException,
            ImproperFilenameException {
        final Manuscript manuscript;
        final Chapter chapter;
        final Series series;
        final Extract entire;

        manuscript = new Manuscript();
        manuscript.setFilename("tests/parchment/quack/HelloWorld.parchment"); // ignored
        chapter = new Chapter(manuscript);
        chapter.setFilename("TemporaryFiles.xml");
        series = chapter.loadDocument();
        assertEquals(2, series.size());

        entire = series.getSegment(1).getEntire();

        assertNotNull(entire);
        assertEquals("Accessing the /tmp directory directly is "
                + "fine, but you are often better off using File's createTempFile() function.",
                entire.getText());
    }

    public final void testWriteUnicode() throws IOException {
        final TextChain chain;
        final Span span;
        Segment segment;
        Extract entire;
        final QuackConverter converter;
        final ByteArrayOutputStream out;
        final String blob;

        /*
         * This verifies for us that XOM can handle UTF-16 surrogates, at
         * least when they are expressed as Java escapes.
         */

        chain = new TextChain();

        span = createSpan(":\ud835\udc5b:", null);
        chain.append(span);

        converter = new QuackConverter();
        entire = Extract.create();

        segment = new ChapterSegment(entire);
        converter.append(segment);

        entire = chain.extractAll();
        segment = new NormalSegment(entire);
        converter.append(segment);

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<quack xmlns=\"http://namespace.operationaldynamics.com/parchment/5.0\">",
            "<text>",
            ":𝑛:",
            "</text>",
            "</quack>"
        });
        assertEquals(blob, out.toString());
    }
}
