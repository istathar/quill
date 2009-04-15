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

class PreformatEditorTextView extends EditorTextView
{
    public PreformatEditorTextView() {
        super();

        super.modifyFont(fonts.mono);

        super.setWrapMode(WrapMode.NONE);
        super.setAcceptsTab(true);
    }
}
