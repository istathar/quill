/*
 * ExampleThereAndBackAgain.java
 *
 * Copyright (c) 2008-2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.converter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.NodeFactory;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.docbook.Book;
import quill.docbook.DocBookNodeFactory;
import quill.textbase.DataLayer;
import quill.textbase.DocBookLoader;
import quill.textbase.Series;

/**
 * <p>
 * I was watching <i>The Fellowship of the Ring</i> as I started writing this
 * test.
 * 
 * @author Andrew Cowie
 */
public class ExampleThereAndBackAgain
{
    public static void main(String[] args) throws IOException, ValidityException, ParsingException {
        final File source, target;
        final Builder parser;
        final NodeFactory factory;
        final Document doc;
        final DocBookLoader loader;
        final DataLayer data;
        final Series series;
        int i;
        final Book book;
        final DocBookConverter converter;
        final OutputStream out;

        source = new File("tests/ExampleProgram.xml");
        assert (source.exists());

        factory = new DocBookNodeFactory();
        parser = new Builder(factory);
        doc = parser.build(source);

        loader = new DocBookLoader();
        series = loader.process(doc);

        data = new DataLayer();
        data.loadDocument("tests/ExampleProgram.xml");

        if (true) {
            for (i = 1; i <= 70; i++) {
                System.err.print(i / 10);
            }
            System.err.println();
            for (i = 1; i <= 70; i++) {
                System.err.print(i % 10);
            }
            System.err.println("\n");
            System.err.flush();

            data.saveDocument(System.out);
        } else {
            data.saveDocument("tmp/unittests/markerpen/converter/ExampleProgram.xml");
        }
    }
}
