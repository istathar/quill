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

import org.gnome.gtk.HBox;
import org.gnome.gtk.Widget;

/**
 * An Editor wrapper that has a label and a body. For list items, and notes
 * &amp; references.
 * 
 * @author Andrew Cowie
 */
abstract class ListitemBox extends HBox
{
    private final HBox box;

    private Widget label;

    private EditorTextView body;

    ListitemBox() {
        super(false, 0);
        box = this;
    }

    void setupLabel(final Widget widget) {
        label = widget;
        box.packStart(label, false, false, 0);
    }

    void setupBody(final EditorTextView editor) {
        body = editor;
        box.packStart(body, true, true, 0);
    }

    EditorTextView getEditor() {
        return body;
    }
}
