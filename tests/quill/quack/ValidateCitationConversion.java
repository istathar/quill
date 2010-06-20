/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009 Operational Dynamics Consulting, Pty Ltd
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
package quill.quack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.client.IOTestCase;
import quill.textbase.ComponentSegment;
import quill.textbase.DataLayer;
import quill.textbase.Extract;
import quill.textbase.MarkerSpan;
import quill.textbase.NormalSegment;
import quill.textbase.Segment;
import quill.textbase.Series;
import quill.textbase.Span;
import quill.textbase.SpanVisitor;
import quill.textbase.Special;
import quill.textbase.StringSpan;
import quill.textbase.TextChain;

public class ValidateCitationConversion extends IOTestCase
{
    public final void testMarkerSpan() {
        Span span;

        span = Span.createMarker("[Penrose 1989]", Special.CITE);
        assertTrue(span instanceof MarkerSpan);
        assertEquals("[Penrose 1989]", span.getText());
        assertEquals(Special.CITE, span.getMarkup());
    }

    public final void testInlineCite() throws IOException, ValidityException, ParsingException {
        final String FILE;
        final DataLayer data;
        final Series series;
        Segment segment;
        final TextChain chain;
        final Extract entire;
        final QuackConverter converter;
        int i;
        final ByteArrayOutputStream out;
        final String original, result;

        FILE = "tests/quill/quack/Citation.xml";

        original = loadFileIntoString(FILE);

        data = new DataLayer();
        data.loadChapter(FILE);

        series = data.getActiveDocument().getSeries(0);
        assertEquals(2, series.size());

        segment = series.get(0);
        assertTrue(segment instanceof ComponentSegment);
        segment = series.get(1);
        assertTrue(segment instanceof NormalSegment);

        chain = segment.getText();
        entire = chain.extractAll();
        entire.visit(new SpanVisitor() {
            private int i = 0;

            public boolean visit(Span span) {
                switch (i) {
                case 0:
                    assertTrue(span instanceof StringSpan);
                    break;
                case 1:
                    assertTrue(span instanceof MarkerSpan);
                    assertEquals(Special.CITE, span.getMarkup());
                    break;
                default:
                    fail();
                }
                i++;
                return false;
            }
        });

        converter = new QuackConverter();

        for (i = 0; i < series.size(); i++) {
            converter.append(series.get(i));
        }

        out = new ByteArrayOutputStream();
        converter.writeChapter(out);

        result = out.toString();
        assertEquals(original, result);
    }
}
