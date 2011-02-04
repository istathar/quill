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

import org.gnome.gdk.Screen;
import org.gnome.gtk.Menu;
import org.gnome.gtk.Requisition;

/**
 * Base class for positioning the popup location of a context menu.
 * 
 * @author Andrew Cowie
 */
abstract class ContextMenu extends Menu
{
    /**
     * The top level user interface Window that this Menu is being poped over.
     */
    protected final PrimaryWindow primary;

    /**
     * This Menu.
     */
    protected final Menu menu;

    ContextMenu(SeriesEditorWidget parent) {
        super();
        menu = this;
        primary = parent.getPrimary();
    }

    /**
     * Present the suggestions menu at x, y (and supplying current line's
     * y-range [height] as R).
     */
    void presentAt(final int x, final int y, final int R) {
        final Screen screen;
        final int h, H;
        int target;
        final Requisition req;

        /*
         * Get the available height. We use the user's screen rather than the
         * parent window because there's nothing wrong with a popup menu
         * overlapping (say) the gnome-panel.
         */

        screen = primary.getScreen();
        H = screen.getHeight();

        req = menu.getRequisition();
        h = req.getHeight();

        /*
         * Figure out if there is sufficient room for the popup below, as we
         * would prefer. The plus 3 is sufficient to continue to show the red
         * squiggle in spelling mistakes.
         */

        target = y + R + 3;

        if (target + h > H) {
            target = y - h;
            if (target < 0) {
                target = y;
            }
        }
        menu.popup(x, target);
    }
}
