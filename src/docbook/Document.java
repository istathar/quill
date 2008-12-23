/*
 * Document.java
 *
 * Copyright (c) 2008 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package docbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Base class for documents encoded in the <code>docbook</code> package.
 * Export it to XHTML XML using {@link #write(OutputStream) write()}.
 * 
 * @author Andrew Cowie
 */
public class Document
{
    private static Charset UTF8;

    static {
        UTF8 = Charset.forName("UTF-8");
    }

    private final nu.xom.Document xom;

    private final RootTag root;

    protected Document(RootTag root) {

        xom = new nu.xom.Document(root.element);

        /*
         * Setup the structural tags.
         */
        this.root = root;
    }

    public void addChapter(Chapter tag) {
        root.addChild(tag);
    }

    /**
     * Render this document as DocBook XML.
     */
    public byte[] toXML() {
        final DocBookOutputter outputter;
        final String result;

        outputter = new DocBookOutputter();
        result = outputter.writeAsXML(xom);

        return result.getBytes(UTF8);
    }

    /*
     * For debugging
     */
    public void toFile(String filename) throws IOException {
        final FileOutputStream out;

        try {
            out = new FileOutputStream(filename);
            out.write(toXML());
            out.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }
}
