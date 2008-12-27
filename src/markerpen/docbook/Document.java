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
package markerpen.docbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Serializer;

/**
 * Base class for documents encoded in the <code>docbook</code> package.
 * Export it to XHTML XML using {@link #write(OutputStream) write()}.
 * 
 * @author Andrew Cowie
 */
public class Document
{
    private final nu.xom.Document xom;

    private final DocBookTag root;

    /**
     * Though strictly the top of DocBook is <code>&lt;book&gt;</code>, you
     * can also start from <code>&lt;article&gt;</code> and a number of other
     * places.
     */
    protected Document(DocBookTag root) {

        xom = new nu.xom.Document(root.element);

        /*
         * Setup the structural tags.
         */
        root.setAttribute("version", "5.0");

        this.root = root;
    }

    public void add(Component tag) {
        root.addChild(tag);
    }

    /*
     * For debugging
     */
    public void toFile(String filename) throws IOException {
        final FileOutputStream out;

        try {
            out = new FileOutputStream(filename);
            toXML(out);
            out.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    public void toXML(OutputStream out) throws IOException {
        final Serializer s;

        s = new DocBookSerializer(out, root);
        s.write(xom);
        s.flush();
    }
}
