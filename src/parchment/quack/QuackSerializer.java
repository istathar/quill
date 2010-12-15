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
package parchment.quack;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

/**
 * Render a document as valid Quack XML. Modelled directly on the Xom
 * Serializer class, but after several iterations we finally just implemented
 * our own, so as to get better wrapping behaviour.
 * 
 * @author Andrew Cowie
 */
/*
 * We would have set this to 78 which is our habit for text files, but a)
 * XOM's line wrapping is a bit weak, and b) this gives us room for a \t in an
 * 80 column wide terminal.
 */
class QuackSerializer
{
    private Element root;

    private final PrintWriter out;

    /**
     * Are we in a Block which preserves whitespace?
     */
    private boolean preserving;

    /**
     * Accumulator.
     */
    private ArrayList<String> list;

    QuackSerializer(OutputStream actual) {
        OutputStreamWriter writer;
        BufferedWriter buffer;

        try {
            writer = new OutputStreamWriter(actual, "UTF-8");
            buffer = new BufferedWriter(writer);
            out = new PrintWriter(buffer);

            list = new ArrayList<String>(128);
        } catch (UnsupportedEncodingException uee) {
            // not possible unless your VM is non compliant
            throw new AssertionError();
        }
    }

    /**
     * Accumulate the given text. Characters must be legal in XML (ie,
     * ampersands escaped).
     */
    private void accumulate(String str) {
        list.add(str);
    }

    /**
     * Accumulate the text represented by this StringBuilder.
     * 
     * @param buf
     */
    private void accumulate(StringBuilder buf) {
        final String str;

        str = buf.toString();
        list.add(str);
    }

    /**
     * When we know that we have safely reached the end of a block, we can
     * write the accumulated buffer. This WILL result in a newline being
     * emmitted and the accumulation buffer being cleared.
     */
    private void writeAccumulated() {
        final int I;
        // width since space
        int p;
        // current column
        int x;
        int i, width;
        String str;
        int last;

        I = list.size();

        if (I == 0) {
            return;
        }

        x = 0;
        p = 0;
        last = -1;

        for (i = 0; i < I; i++) {
            str = list.get(i);

            /*
             * Bare newlines are the signal of a block start or block end
             * element.
             */
            if (str == "\n") {
                p = 0;
                x = 0;
                last = -1;
                continue;
            }
            if (str == " ") {
                p = 0;
                x++;
                last = i;
                continue;
            }

            width = str.length();
            p += width;

            if ((x + width > 70) && (last > 0)) {
                list.set(last, "\n");
                last = -1;
                x = p;
            } else {
                x += width;
            }
        }

        for (i = 0; i < I; i++) {
            str = list.get(i);
            out.print(str);
        }

        list.clear();
    }

    void write(Document doc) {
        final StringBuilder buf;
        final int I;
        int i, j, J;
        Element e;
        Node node;

        accumulate("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        accumulate("\n");

        root = doc.getRootElement();

        buf = new StringBuilder(128);
        buf.append('<');
        buf.append(root.getLocalName());
        buf.append(' ');
        buf.append("xmlns=");
        buf.append('\"');
        buf.append(root.getNamespaceURI());
        buf.append('\"');
        buf.append('>');

        accumulate(buf);
        accumulate("\n");

        /*
         * Children of Root are Blocks
         */

        I = root.getChildCount();
        for (i = 0; i < I; i++) {
            node = root.getChild(i);

            if (node instanceof Element) {
                e = (Element) node;
                J = e.getChildCount();

                if (J == 0) {
                    writeStartTag(e, true);
                } else {
                    writeStartTag(e, false);
                    for (j = 0; j < J; j++) {
                        node = e.getChild(j);
                        writeInline(node);
                    }
                    writeEndTag(e);
                }
            } else {
                throw new AssertionError("Unknown Node type in Root element");
            }

            writeAccumulated();
        }

        writeEndTag(root);
        writeAccumulated();

        out.flush();
    }

    private void writeInline(Node node) {
        final Element e;
        int j, J;

        if (node instanceof Text) {
            writeText(node);
        } else if (node instanceof Inline) {
            e = (Element) node;
            J = e.getChildCount();

            if (J == 0) {
                writeStartTag(e, true);
            } else {
                writeStartTag(e, false);
                for (j = 0; j < J; j++) {
                    node = e.getChild(j);
                    writeText(node);
                }
                writeEndTag(e);
            }

        } else {
            throw new AssertionError("Unknown Node type in Block level element");
        }
    }

    /**
     * Output the opening tag of an XML element, or the entire tag if it is an
     * empty element.
     */
    /*
     * When writing attributes, the "qualified" name includes the prefix,
     * which does us for xml:space.
     */
    private void writeStartTag(Element e, boolean empty) {
        StringBuilder buf;
        final int K;
        int k;
        Attribute a;

        buf = new StringBuilder();

        buf.append('<');
        buf.append(e.getLocalName());

        K = e.getAttributeCount();
        for (k = 0; k < K; k++) {
            a = e.getAttribute(k);

            buf.append(' ');
            buf.append(a.getQualifiedName());
            buf.append('=');
            buf.append('\"');
            buf.append(a.getValue());
            buf.append('\"');
        }
        if (empty) {
            buf.append('/');
        }
        buf.append('>');

        accumulate(buf);

        if (e instanceof Preserve) {
            preserving = true;
        }
        if (e instanceof Block) {
            accumulate("\n");
        }
    }

    private void writeEndTag(Element e) {
        final StringBuilder buf;

        buf = new StringBuilder();

        if (e instanceof Block) {
            /*
             * After writing a bunch of spans, take the cursor back to the
             * beginning of the line before writing the end tag.
             */
            accumulate("\n");
        }

        buf.append('<');
        buf.append('/');
        buf.append(e.getLocalName());
        buf.append('>');

        accumulate(buf);

        if (e instanceof Block) {
            accumulate("\n");
            preserving = false;
        } else if (e instanceof Root) {
            accumulate("\n");
        }
    }

    /*
     * Using Text's toXML() is expensive, though not nearly as expensive as
     * Serializer's writeEscaped(). Sicne this is the only way we can get to
     * the character sequence, this code can only be optimized out if we stop
     * using Xom entirely.
     */

    private void writeText(Node node) {
        final Text text;
        final String str;
        String sub;
        int i, p;

        text = (Text) node;
        str = text.toXML();

        if (preserving) {
            list.add(str);
        } else {
            i = str.indexOf(' ');
            p = 0;
            while (i != -1) {
                if (i > p) {
                    sub = str.substring(p, i);

                    accumulate(sub);
                }
                accumulate(" ");

                i++;
                p = i;
                i = str.indexOf(' ', i);
            }
            if (p != str.length()) {
                sub = str.substring(p);
                accumulate(sub);
            }
        }
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
}
