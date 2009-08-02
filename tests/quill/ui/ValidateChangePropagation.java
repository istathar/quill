/*
 * ValidateChangePropagation.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the suite it is a part of, are made available
 * to you by the authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.ValidityException;
import quill.textbase.DataLayer;
import quill.textbase.Folio;

import static quill.client.Quill.ui;

public class ValidateChangePropagation extends GraphicalTestCase
{
    public final void testReplaceText() throws ValidityException, ParsingException, IOException {
        final DataLayer data;
        final Folio folio;

        data = new DataLayer();
        ui = new UserInterface(data);

        data.loadDocument("tests/ExampleProgram.xml");
        folio = data.getActiveDocument();
        ui.displayDocument(folio);
    }
}
