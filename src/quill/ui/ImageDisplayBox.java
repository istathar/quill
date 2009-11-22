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

import java.io.IOException;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VBox;

import quill.textbase.DataLayer;
import quill.textbase.Segment;

public class ImageDisplayBox extends VBox
{
    private VBox box;

    private Image image;

    private EditorTextView caption;

    public ImageDisplayBox(DataLayer data, Segment segment) {
        super(false, 0);
        final String source, parentdir, filename;
        final int width;
        String tooltip;
        Pixbuf pixbuf;

        box = this;

        /*
         * Image paths specified in src= tags are assumed to be relatve to the
         * base document. So using that relative path and our cached parent
         * location, work out an actual pathname to the file.
         */

        source = segment.getImage();
        parentdir = data.getDirectory();

        filename = parentdir + "/" + source;

        try {
            width = Pixbuf.getFileInfoWidth(filename);
            if (width > 150) {
                pixbuf = new Pixbuf(filename, -1, 100, true);
            } else {
                pixbuf = new Pixbuf(filename);
            }
            tooltip = " Image source: \n" + " <tt>" + filename + "</tt> ";
        } catch (IOException ioe) {
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
