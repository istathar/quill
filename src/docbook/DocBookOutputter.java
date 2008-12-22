/*
 * FOOutputter.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

/**
 * Render a document as valid DocBook XML, with slight formatting applied when
 * outputting.
 * 
 * @author Andrew Cowie
 */
class DocBookOutputter
{
    private StringBuilder buf;

    private String xhtml;

    DocBookOutputter() {
        buf = new StringBuilder(128);
    }

    /*
     * In essence, this replaces Document's toXML(). We do the top level
     * manually because it is quite predictable [more to the point, fixed as
     * to how it has to be according to the validators]. Yes, generic XML can
     * have more crammed into it, but renderable XHTML doesn't.
     */
    String writeAsXML(Document page) {
        final Element root;

        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buf.append("\n");

        buf.append(page.getDocType().toXML());
        buf.append("\n");

        /*
         * Now start the recursive descent.
         */

        root = page.getRootElement();
        xhtml = null;
        write(root);

        return buf.toString();
    }

    /*
     * TODO this does not deal with element or attribute namespaces. XOM is
     * perfectly capable of representing this information, but at present our
     * XHTML binding does not permit adding elements with foreign namespaces,
     * so we just ignore that aspect of things for the time being.
     */
    private void write(Node node) {
        DocBookElement element = null;
        DocBookTag tag = null;
        int i, num;

        num = node.getChildCount();

        if (node instanceof DocBookElement) {
            element = (DocBookElement) node;
            tag = element.proxy;

            if (num == 0) {
                writeEmptyTag(element);
            } else {
                writeStartTag(element);
            }
            // if (tag instanceof StructureTag) {
            buf.append("\n");
            // }
            // if (tag instanceof Division) {
            // buf.append("\n\n");
            // }
        } else if (node instanceof Text) {
            buf.append(node.toXML());
        } else {
            System.err.println("Warning: unhandled " + node.toString());
        }

        for (i = 0; i < num; i++) {
            write(node.getChild(i));
        }

        if (node instanceof DocBookElement) {
            if (tag instanceof Paragraph) {
                buf.append("\n");
            }

            if (num != 0) {
                writeEndTag(element);
                buf.append("\n");
            }

            // if (tag instanceof StructureTag) {
            // buf.append("\n");
            // }
            // if (tag instanceof BlockTag) {
            // buf.append("\n\n");
            // }

        }
    }

    private void writeStartTagContents(Element e) {
        final String xmlns;
        final int num;
        int i;
        Attribute a;

        buf.append(e.getLocalName());

        xmlns = e.getNamespaceURI();
        if (xmlns != xhtml) {
            /*
             * This takes care of writing the primary namespace declaration on
             * the root tag.
             */
            if (xhtml == null) {
                xhtml = xmlns;
            }
            buf.append(" xmlns=\"");
            buf.append(xmlns);
            buf.append("\"");
        }

        num = e.getAttributeCount();
        for (i = 0; i < num; i++) {
            a = e.getAttribute(i);
            buf.append(" ");
            buf.append(a.getLocalName());
            buf.append("=\"");
            buf.append(a.getValue());
            buf.append("\"");
        }
    }

    private void writeStartTag(Element e) {
        buf.append("<");
        writeStartTagContents(e);
        buf.append(">");
    }

    private void writeEmptyTag(Element e) {
        buf.append("<");
        writeStartTagContents(e);
        buf.append("/>");
    }

    private void writeEndTag(Element e) {
        buf.append("</");
        buf.append(e.getLocalName());
        buf.append(">");
    }
}
