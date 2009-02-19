/*
 * DocBookSerializer.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.docbook;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Element;
import nu.xom.Serializer;

/**
 * Render a document as valid DocBook XML, with slight formatting applied when
 * outputting.
 * 
 * @author Andrew Cowie
 */
/*
 * FIXME 50 is a convenient width for debugging, but we will of course set it
 * to 78 for production use.
 */
class DocBookSerializer extends Serializer
{
    private final DocBookTag root;

    DocBookSerializer(OutputStream out, DocBookTag root) {
        super(out);
        super.setLineSeparator("\n");
        super.setMaxLength(50);
        this.root = root;
    }

    protected void writeStartTag(Element e) throws IOException {
        DocBookTag tag = null;
        String nested;

        if (e instanceof DocBookElement) {
            tag = ((DocBookElement) e).proxy;
        }

        /*
         * We don't want inline tags pushing us past our word wrap margin if
         * we can help it. This isn't easy, because we need to know the width
         * of the inline up to the first available whitespace. So at the
         * moment we just wholesale convert the thing into XML and see if it's
         * going to exceed our available width. That's pretty ugly. We could
         * maybe look at the children nodes to be more intelligent about
         * figuring out whether we need to call the Element's toXML().
         */

        if (tag instanceof Inline) {
            nested = e.toXML();

            if (getColumnNumber() + firstBreakPoint(nested) > getMaxLength()) {
                breakLine();
            }
        }

        super.writeStartTag(e);

        if (tag instanceof Inline) {
            return;
        }
        breakLine();
    }

    /**
     * Find out the offset of the first space available that we can break at
     * in an Inline tag, or the whole width of the Element if there isn't one.
     */
    /*
     * Input of the form "<emphasis>One or more words</emphasis>"
     */
    private int firstBreakPoint(String frag) {
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
        DocBookTag tag = null;

        if (e instanceof DocBookElement) {
            tag = ((DocBookElement) e).proxy;
        }

        if (tag instanceof Block) {
            breakLine();
        }

        super.writeEndTag(e);

        if (tag instanceof Inline) {
            return;
        }
        if (tag == root) {
            /*
             * Serializer does a line break between each piece of a Document.
             * So on the very last of our tags, we don't need to do a newline.
             */
            return;
        }
        breakLine();
    }

    /*
     * There's no real reason for there to do anything special with empty
     * elements except that an empty block needs a trailing line break.
     */
    protected void writeEmptyElementTag(Element e) throws IOException {
        DocBookTag tag = null;

        if (e instanceof DocBookElement) {
            tag = ((DocBookElement) e).proxy;
        }

        super.writeEmptyElementTag(e);

        if (tag instanceof Block) {
            breakLine();
        }
    }
}
