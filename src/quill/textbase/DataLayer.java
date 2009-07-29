/*
 * DataLayer.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.converter.DocBookConverter;
import quill.docbook.Book;

/**
 * Our in-memory intermediate representation. Provides access to mutate the
 * textbase, along with mechanisms for loading documents and getting at them
 * in memory.
 * 
 * @author Andrew Cowie
 */
public class DataLayer
{
    private ChangeStack stack;

    private Folio current;

    public DataLayer() {
        stack = new ChangeStack();

        current = null;
    }

    public void loadDocument(String filename) throws ValidityException, ParsingException, IOException {
        final File source;
        final DocBookNodeFactory factory;
        final Builder parser;
        final Document doc;
        final Series series;
        final Series[] collection;

        source = new File(filename);
        if (!source.exists()) {
            throw new FileNotFoundException();
        }

        factory = new DocBookNodeFactory();

        parser = new Builder(factory);
        doc = parser.build(source);

        series = FIXME;

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

    public void saveDocument(String filename) throws IOException {
        final Series series;
        final DocBookConverter converter;
        final OutputStream out;
        int i;
        final Book book;

        /*
         * On the temporary assumption that there's only one chapter being
         * edited
         */

        series = current.get(0);

        /*
         * Create an output converter and run the segments through it to turn
         * them into DocBook elements.
         */

        converter = new DocBookConverter();

        for (i = 0; i < series.size(); i++) {
            converter.append(series.get(i));
        }

        /*
         * Get the resultant top level Document and serialize it.
         */

        out = new FileOutputStream(filename);

        book = converter.createBook();
        book.toXML(out);
    }

    /**
     * Apply a Change to the data layer.
     */
    public void apply(Change change) {
        stack.apply(change);
    }

    /**
     * Undo. Return the Change which represents the delta from current to one
     * before.
     */
    public Change undo() {
        return stack.undo();
    }

    /**
     * Redo a previous undo. Returns the Change which is the delta you will
     * need to [re]apply.
     */
    public Change redo() {
        return stack.redo();
    }
}
