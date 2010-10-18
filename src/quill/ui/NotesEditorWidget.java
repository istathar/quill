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

import org.gnome.gtk.Adjustment;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Viewport;

class NotesEditorWidget extends ScrolledWindow
{
    private ScrolledWindow scroll;

    private Adjustment adj;

    private VBox box;

    /**
     * What is the top level UI holding this document?
     */
    private PrimaryWindow primary;

    NotesEditorWidget(PrimaryWindow primary) {
        super();
        scroll = this;
        this.primary = primary;

        setupScrolling();
    }

    private void setupScrolling() {
        final Viewport port;
        box = new VBox(false, 3);

        scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS);
        scroll.addWithViewport(box);
    }

    void refreshDisplay() {}
}
