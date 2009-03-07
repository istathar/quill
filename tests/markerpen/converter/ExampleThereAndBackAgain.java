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
package markerpen.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import markerpen.docbook.Document;
import markerpen.textbase.EfficientNoNodeFactory;
import markerpen.textbase.Segment;
import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

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
        final EfficientNoNodeFactory factory;
        final Segment[] segments;
        final Document doc;
        final DocBookConverter converter;
        final OutputStream out;

        source = new File("tests/markerpen/converter/ExampleProgram.xml");
        assert (source.exists());

        factory = new EfficientNoNodeFactory();

        parser = new Builder(factory);
        parser.build(source);

        segments = factory.createSegments();

        converter = new DocBookConverter();

        /*
         * This logic is going to need to go somewhere else!
         */

        for (Segment segment : segments) {
            converter.append(segment);
        }

        doc = converter.result();
        if (true) {
            for (int i = 1; i <= 70; i++) {
                System.err.print(i / 10);
            }
            System.err.println();
            for (int i = 1; i <= 70; i++) {
                System.err.print(i % 10);
            }
            System.err.println("\n");
            System.err.flush();

            out = System.out;
        } else {
            target = new File("tmp/unittests/markerpen/converter/ExampleProgram.xml");
            target.getParentFile().mkdirs();
            out = new FileOutputStream(target);
        }
        doc.toXML(out);
    }
}
