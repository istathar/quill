/*
 * PreformatEditorTextView.java
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

class PreformatEditorTextView extends EditorTextView
{
    PreformatEditorTextView(Segment segment) {
        super(segment);

        view.modifyFont(fonts.mono);

        view.setWrapMode(WrapMode.NONE);
    }

    protected boolean isCodeBlock() {
        return true;
    }

    protected boolean isSpellChecked() {
        return false;
    }
}
