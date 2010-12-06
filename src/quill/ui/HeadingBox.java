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

import org.gnome.gtk.HBox;
import org.gnome.gtk.HSeparator;
import org.gnome.gtk.Label;
import org.gnome.gtk.VBox;

import quill.textbase.Segment;

abstract class HeadingBox extends VBox
{
    private VBox top;

    private HeadingEditorTextView title;

    private Label label;

    HeadingBox() {
        super(false, 0);
        top = this;
    }

    void setupBox(final ComponentEditorWidget parent, final Segment segment, final String text) {
        final HBox box;

        box = new HBox(false, 0);

        title = new HeadingEditorTextView(parent, segment);
        box.packStart(title, true, true, 0);

        label = new Label();
        label.setWidthChars(20);
        label.setUseMarkup(true);
        label.setLabel("<span color='gray'>" + text + "</span>");
        box.packEnd(label, false, false, 0);

        top.packStart(box, false, false, 0);
    }

    void setupLine() {
        final HSeparator sep;

        sep = new HSeparator();

        top.packStart(sep, false, false, 0);
    }

    EditorTextView getEditor() {
        return title;
    }
}
