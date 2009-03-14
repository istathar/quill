/*
 * DevelopmentHarness.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.client;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.textbase.Folio;

import static quill.client.Quill.data;
import static quill.client.Quill.initializeDataLayer;
import static quill.client.Quill.initializeUserInterface;
import static quill.client.Quill.runUserInterface;
import static quill.client.Quill.ui;

public class DevelopmentHarness
{
    public static void main(String[] args) throws ValidityException, ParsingException, IOException {
        initializeDataLayer();
        initializeUserInterface(args);
        loadExampleDocument();
        runUserInterface(); // blocks
    }

    private static void loadExampleDocument() throws ValidityException, ParsingException, IOException {
        final Folio folio;

        data.loadDocument("tests/quill/converter/ExampleProgram.xml");
        folio = data.getActiveDocument();
        ui.displayDocument(folio);
    }
}
