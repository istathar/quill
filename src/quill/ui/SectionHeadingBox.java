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

class SectionHeadingBox extends HBox
{
    private HBox box;

    private Entry title;

    private Label label;

    public SectionHeadingBox() {
        super(false, 0);
        box = this;

        title = new Entry();
        title.modifyFont(fonts.serif);
        box.packStart(title, true, true, 0);

        label = new Label();
        label.setLabel("Section");
        box.packEnd(label, true, true, 0);
    }
}
