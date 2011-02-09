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

import quill.textbase.Segment;

// cloned from ReferenceListitemBox
class EndnoteListitemBox extends ListitemBox
{
    EndnoteListitemBox(final SeriesEditorWidget parent, final Segment segment) {
        super(parent);
        final ListitemEntry entry;
        final EditorTextView editor;

        entry = new EndnoteListitemEntry(parent, segment);
        editor = new ReferenceEditorTextView(parent, segment);

        super.setupLabelSide(entry, 0, 1);
        super.setupBody(editor);
    }
}

/**
 * @see ReferenceListitemEntry
 */
class EndnoteListitemEntry extends ListitemEntry
{
    EndnoteListitemEntry(SeriesEditorWidget parent, Segment segment) {
        super(parent, segment);
    }
}