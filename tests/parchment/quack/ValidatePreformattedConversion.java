/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import parchment.manuscript.Manuscript;
import quill.client.IOTestCase;
import quill.textbase.Common;
import quill.textbase.Extract;
import quill.textbase.Folio;
import quill.textbase.NormalSegment;
import quill.textbase.PreformatSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.TextChain;

import static quill.textbase.Span.createSpan;

/**
 * This tested matters when we doing programlisting blocks as a Markup. The
 * infrastructure is good, though, so keep this around until we can use it
 * properly.
 * 
 * @author Andrew Cowie
 */
public class ValidatePreformattedConversion extends IOTestCase
{
    public final void testWritePreformatting() throws IOException {
        final Manuscript manuscript;
        final QuackConverter converter;
        TextChain chain;
        Extract entire;
        Span[] spans;
        Segment segment;
        final Folio folio;
        Series series;
        int i;
        final int len;
        final ByteArrayOutputStream out;
        final String blob;

        manuscript = new Manuscript();
        folio = manuscript.createDocument();
        series = folio.getSeries(0);

        /*
         * Construct some data
         */

        spans = new Span[] {
            createSpan(
                    "Consider the following simple and yet profound expression of quality program code:",
                    null)
        };

        chain = new TextChain();
        for (Span span : spans) {
            chain.append(span);
        }

        entire = chain.extractAll();
        segment = new NormalSegment(entire);
        series = series.update(0, segment);

        spans = new Span[] {
            createSpan("public class Hello {", null),
            createSpan("\n", null),
            createSpan("    public static void main(String[] args) {", null),
            createSpan("\n", null),
            createSpan("        System.out.println(\"Hello World\");", null),
            createSpan("\n", null),
            createSpan("    }", null),
            createSpan("\n", null),
            createSpan("}", null)
        };

        chain = new TextChain();
        for (Span span : spans) {
            chain.append(span);
        }

        entire = chain.extractAll();
        segment = new PreformatSegment(entire);
        series = series.update(1, segment);

        spans = new Span[] {
            createSpan("There really isn't anything like saying ", null),
            createSpan("Hello World", Common.ITALICS),
            createSpan(" to a nice friendly programmer.", null),
        };

        chain = new TextChain();
        for (Span span : spans) {
            chain.append(span);
        }

        entire = chain.extractAll();
        segment = new NormalSegment(entire);
        series = series.insert(2, segment);

        /*
         * Now run conversion process.
         */

        len = series.size();
        converter = new QuackConverter();

        for (i = 0; i < len; i++) {
            segment = series.getSegment(i);
            converter.append(segment);
        }

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        blob = combine(new String[] {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
            "<quack xmlns=\"http://namespace.operationaldynamics.com/parchment/5.0\">",
            "<text>",
            "Consider the following simple and yet profound expression of quality",
            "program code:",
            "</text>",
            "<code xml:space=\"preserve\">",
            "public class Hello {",
            "    public static void main(String[] args) {",
            "        System.out.println(\"Hello World\");",
            "    }",
            "}",
            "</code>",
            "<text>",
            "There really isn't anything like saying <italics>Hello World</italics>",
            "to a nice friendly programmer.",
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
}
