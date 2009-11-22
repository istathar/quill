/*
 * CaptionEditorTextView.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.Justification;
import org.gnome.pango.FontDescription;
import org.gnome.pango.Style;

import quill.textbase.Segment;

/**
 * Text to be used as the explanation (not to mention body, on disk) of an
 * image or figure.
 * 
 * @author Andrew Cowie
 */
class CaptionEditorTextView extends EditorTextView
{
    private static FontDescription desc;

    static {
        desc = fonts.serif.copy();
        desc.setStyle(Style.ITALIC);
    }

    CaptionEditorTextView(Segment segment) {
        super(segment);

        view.modifyFont(desc);
        view.setMarginLeft(40);
        view.setMarginRight(40);
        view.setPaddingAboveParagraph(2);
        view.setPaddingBelowParagraph(4);
        view.setJustify(Justification.CENTER);
    }

    protected boolean isTabAllowed() {
        return false;
    }

    protected boolean isSpellChecked() {
        return false;
    }

    protected boolean isEnterAllowed() {
        return false;
    }
}
