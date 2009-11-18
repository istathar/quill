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

    private EditorTextView caption;

    public ImageDisplayBox(Segment segment) {
        super(false, 0);

        setupBox(segment);
    }

    private void setupBox(Segment segment) {
        final String filename;
        String tooltip;
        Pixbuf pixbuf;

        box = this;

        filename = segment.getImage();
        try {
            pixbuf = new Pixbuf(filename);
            if (pixbuf.getWidth() > 150) {
                /*
                 * This is awful, but pixbuf.scale() doesn't take -1 on width
                 * or height, so how else could we find out?
                 */
                pixbuf = new Pixbuf(filename, 150, -1, true);
            }
            tooltip = " Image source: \n" + " <tt>" + filename + "</tt> ";
        } catch (FileNotFoundException e) {
            pixbuf = Gtk.renderIcon(new Label(), Stock.MISSING_IMAGE, IconSize.DIALOG);
            tooltip = " <b><big>Missing Image!</big></b> \n" + " Source file\n" + " <tt>" + filename
                    + "</tt> \n" + " not found ";
        }
        image = new Image(pixbuf);
        image.setTooltipMarkup(tooltip);
        box.packStart(image, false, false, 0);

        caption = new CaptionEditorTextView(segment);
        box.packStart(caption, true, true, 0);
    }

    EditorTextView getEditor() {
        return caption;
    }
}
