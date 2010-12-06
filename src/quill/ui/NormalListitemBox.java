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
import org.gnome.gtk.Entry;
import org.gnome.gtk.Widget;

import quill.textbase.Segment;

class NormalListitemBox extends ListitemBox
{
    NormalListitemBox(final ComponentEditorWidget parent, final Segment segment) {
        super();
        final Widget widget;
        final EditorTextView editor;

        widget = createLabel(segment);
        super.setupLabel(widget);

        editor = new NormalEditorTextView(parent, segment);
        super.setupBody(editor);
    }

    private Widget createLabel(final Segment segment) {
        final String text;
        final Entry entry; // field?
        final Alignment align;

        text = segment.getImage();

        entry = new Entry();
        entry.setText(text);
        entry.setWidthChars(3);
        entry.setHasFrame(false);
        entry.setAlignment(Alignment.RIGHT);

        align = new Alignment(Alignment.LEFT, Alignment.TOP, 0, 0);
        align.setPadding(5, 0, 0, 0);
        align.add(entry);

        return align;
    }
}
