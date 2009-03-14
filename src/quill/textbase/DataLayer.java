/*
 * DataStore.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.textbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Mechanism for loading documents and getting at them in memory.
 * 
 * @author Andrew Cowie
 */
public class DataLayer
{
    private Folio current;

    public DataLayer() {
        current = null;
    }

    public void loadDocument(String filename) throws ValidityException, ParsingException, IOException {
        final File source;
        final Builder parser;
        final EfficientNoNodeFactory factory;
        final Series series;
        final Series[] collection;

        source = new File(filename);
        if (!source.exists()) {
            throw new FileNotFoundException();
        }

        /*
         * Call our custom machinery to use XOM to parse the documents and
         * create arrays of Components containing TextStacks.
         */

        factory = new EfficientNoNodeFactory();

        parser = new Builder(factory);
        parser.build(source);

        series = factory.createSeries();

        /*
         * For now, we assume we only read one Series
         */

        collection = new Series[] {
            series
        };

        /*
         * And we assume we're only managing one document.
         */

        current = new Folio(collection);
    }

    public Folio getActiveDocument() {
        return current;
    }
}
