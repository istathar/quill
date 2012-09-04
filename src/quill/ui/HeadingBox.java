/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2009-2011 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.gtk.Grid;
import org.gnome.gtk.HSeparator;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;

import quill.textbase.Segment;

abstract class HeadingBox extends Grid
{
    private Grid grid;

    private HeadingEditorTextView title;

    private Label label;

    HeadingBox() {
        super();
        grid = this;
    }

    void setupBox(final SeriesEditorWidget parent, final Segment segment, final String text) {

        title = new HeadingEditorTextView(parent, segment);
        grid.attach(title, 0, 0, 2, 1);

        label = new Label();
        label.setWidthChars(20);
        label.setUseMarkup(true);
        label.setLabel("<span color='gray'>" + text + "</span>");

        grid.attach(label, 1, 0, 1, 1);
    }

    void setupLine() {
        final HSeparator sep;

        sep = new HSeparator();

        grid.add(sep);
    }

    void setCentered() {
        final Label spacer;

        spacer = new Label();
        spacer.setWidthChars(20);
        grid.add(spacer);
        title.setJustify(Justification.CENTER);
    }

    EditorTextView getTextView() {
        return title;
    }
}
