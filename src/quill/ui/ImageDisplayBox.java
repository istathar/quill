/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2010 Operational Dynamics Consulting, Pty Ltd
 *
 * The code in this file, and the program it is a part of, is made available
 * to you by its authors as open source software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License version
 * 2 ("GPL") as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GPL for more details.
 *
 * You should have received a copy of the GPL along with this program. If not,
 * see http://www.gnu.org/licenses/. The authors of this program may be
 * contacted through http://research.operationaldynamics.com/projects/quill/.
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

import parchment.format.Manuscript;
import quill.textbase.Folio;
import quill.textbase.Segment;

public class ImageDisplayBox extends VBox
{
    private VBox box;

    private Image image;

    private EditorTextView caption;

    public ImageDisplayBox(ComponentEditorWidget parent, Segment segment) {
        super(false, 0);
        final PrimaryWindow primary;
        final Folio folio;
        final Manuscript manuscript;
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

        primary = parent.getPrimary();
        folio = primary.getDocument();
        manuscript = folio.getManuscript();
        parentdir = manuscript.getDirectory();

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

        caption = new CaptionEditorTextView(parent, segment);
        box.packStart(caption, true, true, 0);
    }

    EditorTextView getEditor() {
        return caption;
    }
}
