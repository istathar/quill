/*
 * QuoteEditorTextView.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import quill.textbase.Segment;

class QuoteEditorTextView extends EditorTextView
{
    public QuoteEditorTextView(Segment segment) {
        super(segment);

        view.modifyFont(fonts.serif);
        view.setMarginLeft(40);
        view.setMarginRight(40);
        view.setPaddingAboveParagraph(4);
        view.setPaddingBelowParagraph(6);
    }
}
