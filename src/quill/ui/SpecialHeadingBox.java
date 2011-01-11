/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010-2011 Operational Dynamics Consulting, Pty Ltd
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

import org.freedesktop.icons.EmblemIcon;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.Requisition;

import quill.textbase.Segment;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Show an icon to represent a placeholder for one of the SpecialSegments.
 * 
 * @author Andrew Cowie
 */
/*
 * Needs refactoring with HeadingBox; the Label is common code.
 */
class SpecialHeadingBox extends HBox
{
    /**
     * @param parent
     */
    SpecialHeadingBox(final SeriesEditorWidget parent, final Segment segment) {
        super(false, 0);
        final Requisition req;
        final int width;
        final Image image;
        final Label label, spacer;
        final String type, text;

        type = segment.getExtra();

        if (type.equals("endnotes")) {
            text = _("Endnotes");
        } else if (type.equals("references")) {
            text = _("References");
        } else if (type.equals("contents")) {
            text = _("Table of Contents");
        } else {
            throw new IllegalStateException("Unknown Segment type");
        }

        image = new Image(EmblemIcon.EMBLEM_PACKAGE, IconSize.DIALOG);
        image.setTooltipText(_("Insertion point for automatically generated content"));

        label = new Label();
        label.setWidthChars(20);
        label.setUseMarkup(true);
        label.setLabel("<span color='gray'>" + text + "</span>");

        super.packEnd(label, false, false, 0);

        /*
         * In order to put the placeholder image in the middle of the editor
         * area, we need an equal sized Widget on the leading edge of the HBox
         * to balance the real Label on the trailing end.
         */
        req = label.getRequisition();
        width = req.getWidth();

        spacer = new Label();
        spacer.setSizeRequest(width, -1);

        super.packStart(spacer, false, false, 0);
        super.packStart(image, true, true, 0);
    }
}
