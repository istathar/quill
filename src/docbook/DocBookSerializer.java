/*
 * DocBookOutputter.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Element;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * Render a document as valid DocBook XML, with slight formatting applied when
 * outputting.
 * 
 * @author Andrew Cowie
 */
class DocBookSerializer extends Serializer
{
    DocBookSerializer(OutputStream out) {
        super(out);
        super.setLineSeparator("\n");
        super.setMaxLength(30);
    }

    protected void writeStartTag(Element e) throws IOException {
        DocBookTag tag = null;

        super.writeStartTag(e);

        if (e instanceof DocBookElement) {
            tag = ((DocBookElement) e).proxy;

            if (tag instanceof Inline) {
                return;
            }
        }
        breakLine();
    }

    protected void write(Text t) throws IOException {
        super.write(t);
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
        breakLine();
    }
}
