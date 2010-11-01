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
import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.Widget;

import static org.gnome.gtk.Alignment.CENTER;
import static org.gnome.gtk.Alignment.RIGHT;

/**
 * Simple convenience wrapper to put a label and an active control into a
 * consistently spaced HBox.
 * 
 * @author Andrew Cowie
 */
class KeyValueBox extends HBox
{
    /**
     * @param expand
     *            Whether or not to give extra space to value Widget
     */
    KeyValueBox(SizeGroup size, Label label, Widget value, boolean expand) {
        super(false, 0);

        super.packStart(label, false, false, 3);
        label.setAlignment(RIGHT, CENTER);
        size.add(label);

        super.packStart(value, expand, expand, 3);
    }

    KeyValueBox(SizeGroup size, Label label, Widget value, Widget suffix) {
        this(size, label, value, false);
        super.packStart(suffix, false, false, 3);
    }
}
