/*
 * PropertyEditorTextView.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.WrapMode;

import quill.textbase.Segment;

/**
 * Entry for meta properties
 * 
 * @author Andrew Cowie
 */
class PropertyEditorTextView extends EditorTextView
{
    PropertyEditorTextView(Segment segment) {
        super(segment);

        view.modifyFont(fonts.mono);
        view.setWrapMode(WrapMode.NONE);
        view.setPaddingAboveParagraph(0);
        view.setPaddingBelowParagraph(0);
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
