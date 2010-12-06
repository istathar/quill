/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2010 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.gtk.Alignment;
import org.gnome.gtk.Label;
import org.gnome.gtk.Widget;

import quill.textbase.Segment;

class ReferenceListitemBox extends ListitemBox
{
    ReferenceListitemBox(final ComponentEditorWidget parent, final Segment segment) {
        super();
        final Widget widget;
        final EditorTextView editor;

        widget = createLabel(segment);
        editor = new ReferenceEditorTextView(parent, segment);

        super.setupLabel(widget);
        super.setupBody(editor);
    }

    private Widget createLabel(final Segment segment) {
        final String text;
        final Label label; // field?
        final Alignment align;

        text = segment.getImage();

        label = new Label();
        label.setLabel(text);
        label.setWidthChars(3);

        align = new Alignment(Alignment.LEFT, Alignment.TOP, 0, 0);
        align.setPadding(3, 0, 0, 1);
        align.add(label);

        return align;
    }
}
