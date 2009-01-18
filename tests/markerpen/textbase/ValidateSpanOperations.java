/*
 * ValidateSpanOperations.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package markerpen.textbase;

import markerpen.textbase.CharacterSpan;
import markerpen.textbase.ImageSpan;
import markerpen.textbase.Span;
import markerpen.textbase.StringSpan;
import markerpen.textbase.UrlSpan;
import junit.framework.TestCase;

public class ValidateSpanOperations extends TestCase
{
    public final void testSpanStringAccess() {
        final Span one, two;

        one = new StringSpan("Hello Devdas");
        assertEquals("Hello Devdas", one.getText());

        two = new CharacterSpan('£');
        assertEquals("£", two.getText());
    }

    public final void testMetaSpansTextForm() {
        final Span img, url;

        img = new ImageSpan("images/Logo.png");
        assertNotNull(img.getText());

        url = new UrlSpan("http://www.example.com/");
        assertNotNull(url.getText());
    }
}
