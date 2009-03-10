/*
 * SectionHeadingBox.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import org.gnome.gtk.Entry;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;

import quill.textbase.TextStack;

class HeadingBox extends HBox
{
    private HBox box;

    protected Entry title;

    protected Label label;

    public HeadingBox() {
        super(false, 0);
        box = this;

        title = new Entry();
        title.modifyFont(fonts.serif);
        title.setHasFrame(true);
        box.packStart(title, true, true, 0);

        label = new Label();
        box.packEnd(label, true, true, 0);
    }

    void loadText(TextStack load) {
        if (load.undo() != null) {
            throw new IllegalArgumentException();
        }

        /*
         * Should we change to rich markup in titles, we can use the logic in
         * EditorTextView's loadText() [although in that case, the Entry
         * Widget here will have to be replaced with an EditorTextView].
         */

        title.setText(load.toString());
    }

}
