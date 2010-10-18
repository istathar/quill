/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2008-2010 Operational Dynamics Consulting, Pty Ltd
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

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * Render a document as valid Quack XML.
 * 
 * @author Andrew Cowie
 */
/*
 * We would have set this to 78 which is our habit for text files, but a)
 * XOM's line wrapping is a bit weak, and b) this gives us room for a \t in an
 * 80 column wide terminal.
 */
class QuackSerializer extends Serializer
{
    private final QuackElement root;

    private String swollowed;

    QuackSerializer(OutputStream out, QuackElement root) {
        super(out);
        super.setLineSeparator("\n");
        super.setMaxLength(70);
        this.root = root;
        this.swollowed = "";
    }

    protected void writeStartTag(Element e) throws IOException {
        final String nested;
        final int col;
        final int first;
        final int max;
        final int len;

        /*
         * We don't want inline tags pushing us past our word wrap margin if
         * we can help it. This isn't easy, because we need to know the width
         * of the inline up to the first available whitespace. So at the
         * moment we just wholesale convert the thing into XML and see if it's
         * going to exceed our available width. That's pretty ugly. We could
         * maybe look at the children nodes to be more intelligent about
         * figuring out whether we need to call the Element's toXML().
         */

        if (e instanceof Inline) {
            nested = e.toXML();

            col = super.getColumnNumber();
            first = firstBreakPoint(nested);
            len = swollowed.length();
            max = super.getMaxLength();

            if (col + first + len > max) {
                if (col > 0) {
                    breakLine();
                }
                if (swollowed != "") {
                    if ((len == 1) && (swollowed.equals(" "))) {
                        swollowed = "";
                    } else {
                        swollowed = swollowed.substring(1, len);
                    }
                }
            }
        }
        if (swollowed != "") {
            writeEscaped(swollowed);
            swollowed = "";
        }

        super.writeStartTag(e);

        if (e instanceof Inline) {
            return;
        }
        breakLine();
    }

    /**
     * Find out the offset of the first space available that we can break at
     * in an Inline tag, or the whole width of the Element if there isn't one.
     * Note that we're not breaking inside start tags.
     */
    /*
     * Input of the form:
     * 
     * <emphasis role="bold">One or more words</emphasis>
     * 
     * will return 26, the space between "One" and "or".
     */
    private static int firstBreakPoint(String frag) {
        int i = 0;

        i = frag.indexOf('>');
        i = frag.indexOf(' ', i);

        if (i == -1) {
            return frag.length();
        } else {
            return i;
        }
    }

    protected void writeEndTag(Element e) throws IOException {
        if (swollowed != null) {
            writeEscaped(swollowed);
            swollowed = "";
        }

        if (e instanceof Block) {
            /*
             * After writing a bunch of spans, take the cursor back to the
             * beginning of the line before writing the end tag.
             */
            breakLine();
        }

        super.writeEndTag(e);

        if (e instanceof Inline) {
            return;
        }
        if (e == root) {
            /*
             * Serializer does a line break between each piece of a Document.
             * So on the very last of our tags, we don't need to do a newline.
             */
            return;
        }
        breakLine();
    }

    /*
     * TODO The swollowed logic is duplicated from writeStartTag() and should
     * be extracted into a function.
     */
    protected void writeEmptyElementTag(Element e) throws IOException {
        final String nested;
        final int col;
        final int first;
        final int max;
        final int len;

        if (e instanceof Inline) {
            nested = e.toXML();

            col = super.getColumnNumber();
            first = firstBreakPoint(nested);
            len = swollowed.length();
            max = super.getMaxLength();

            if (col + first + len > max) {
                if (col > 0) {
                    breakLine();
                }
                if (swollowed != "") {
                    if (swollowed == " ") {
                        swollowed = "";
                    } else {
                        swollowed = swollowed.substring(1, len);
                    }

                }
            }
        }
        if (swollowed != "") {
            writeEscaped(swollowed);
            swollowed = "";
        }

        super.writeEmptyElementTag(e);

        if (e instanceof Block) {
            breakLine();
        }
    }

    /*
     * Override XOM's text writing method so that we can be more intelligent
     * about [not] writing trailing whitespace.
     */
    protected void write(Text text) throws IOException {
        String str;
        int len, i;

        if (swollowed != "") {
            writeEscaped(swollowed);
            swollowed = "";
        }

        str = text.getValue();
        len = str.length();

        i = str.lastIndexOf(' ');

        if (i != -1) {
            if (i == len - 1) {
                swollowed = " ";
                str = str.substring(0, len - 1);
            } else {
                swollowed = str.substring(i, len);
                str = str.substring(0, i);
            }
        }
        writeEscaped(str);
    }
}
