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

import quill.textbase.Segment;

/**
 * We define notes as being only a single paragraph, so we subclass in order
 * to disallow <b><code>Enter</code></b>.
 * 
 * @author Andrew Cowie
 */
class ListitemEditorTextView extends EditorTextView
{
    ListitemEditorTextView(ComponentEditorWidget parent, Segment segment) {
        super(parent, segment);

        view.modifyFont(fonts.serif);
    }

    protected boolean isEnterAllowed() {
        return false;
    }
}
