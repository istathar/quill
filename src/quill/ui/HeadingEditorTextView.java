/*
 * HeadingEditorTextView.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.pango.FontDescription;

class HeadingEditorTextView extends EditorTextView
{
    private static FontDescription desc;

    static {
        desc = fonts.serif.copy();
        desc.setSize(14.0);
    }

    HeadingEditorTextView() {
        super();
        view.modifyFont(desc);
    }
}
