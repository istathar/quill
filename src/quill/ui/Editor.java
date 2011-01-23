/*
 * Quill and Parchment, a WYSIWYN document editor and rendering engine. 
 *
 * Copyright © 2011 Operational Dynamics Consulting, Pty Ltd
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

import org.gnome.gtk.Entry;

import quill.textbase.Segment;

/**
 * Things that change state when a new Segment comes along, ie EditorTextViews
 * and ListitemEntries. This is a bit recursive.
 */
interface Editor
{

    void advanceTo(Segment segment);

    void reverseTo(Segment segment);

    /**
     * Get the Entry for editing the "label" (extra metadata) of this Segment,
     * if it has one. Otherwise <code>null</code>.
     */
    Entry getLabel();

    /**
     * Get the EditorTextView that is the main body (Quack inlines) editor for
     * this Segment. For EditorTextViews themselves this is effectively just a
     * cast. For Entries it'll be <code>null</code>.
     */
    EditorTextView getTextView();

    /**
     * Send the focus to the appropriate place amongst the children. This
     * would seem to be defined as the EditorTextView?
     */
    void grabFocus();
}
