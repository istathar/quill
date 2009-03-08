/*
 * DocBookLoader.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.converter;

import java.io.File;
import java.io.IOException;

import quill.textbase.EfficientNoNodeFactory;
import quill.textbase.Segment;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 * Load a DocBook file!
 * 
 * @author Andrew Cowie
 */
/*
 * Not really clear that this needs to be an instantiatable class, but it'll
 * save us having to transition in the future if we need state and makes it
 * easier to change the interface.
 */
public class DocBookLoader
{
    private File source;

    public DocBookLoader(File source) {
        if (!source.exists()) {
            throw new IllegalArgumentException();
        }
        this.source = source;
    }

    public Segment[] parseTree() throws ValidityException, ParsingException, IOException {
        final Builder parser;
        final EfficientNoNodeFactory factory;

        factory = new EfficientNoNodeFactory();

        parser = new Builder(factory);
        parser.build(source);

        return factory.createSegments();
    }
}
