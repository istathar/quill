/*
 * ImageDisplayBox.java
 *
 * Copyright (c) 2009 Operational Dynamics Consulting Pty Ltd
 * 
 * The code in this file, and the program it is a part of, are made available
 * to you by its authors under the terms of the "GNU General Public Licence,
 * version 2" See the LICENCE file for the terms governing usage and
 * redistribution.
 */
package quill.ui;

import java.io.FileNotFoundException;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VBox;

import quill.textbase.Segment;

public class ImageDisplayBox extends VBox
{
    private VBox box;

    private Image image;

    private PropertyEditorTextView src;

    public ImageDisplayBox(Segment segment) {
        super(false, 0);

        setupBox(segment);
    }

    private void setupBox(Segment segment) {
        final HBox center;
        final String filename;
        Pixbuf pixbuf;

        box = this;

        filename = segment.getImage();
        try {
            pixbuf = new Pixbuf(filename, 60, -1, true);
        } catch (FileNotFoundException e) {
            pixbuf = Gtk.renderIcon(new Label(), Stock.CANCEL, IconSize.DIALOG);
        }
        image = new Image(pixbuf);
        box.packStart(image, true, true, 0);

        src = new PropertyEditorTextView(segment);
        center = new HBox(false, 0);
        center.packStart(src, true, false, 50);
        box.packEnd(center, false, false, 0);

    }

    EditorTextView getEditor() {
        return src;
    }
}
